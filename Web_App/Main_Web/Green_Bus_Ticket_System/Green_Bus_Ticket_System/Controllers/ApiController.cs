using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Configuration;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using Newtonsoft.Json;
using System.Globalization;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Queue;
using Microsoft.AspNet.SignalR;
using Newtonsoft.Json.Linq;
using System.Net;
using System.IO;
using System.Text;

namespace Green_Bus_Ticket_System.Controllers
{
    public class ApiController : Controller
    {

        private static readonly ILog log = LogManager.GetLogger("WebLog");
        string apiKey = ConfigurationManager.AppSettings["ApiKey"];
        int minBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);
        int defaultBalance = Int32.Parse(ConfigurationManager.AppSettings["DefaultBalance"]);
        string storageConn = ConfigurationManager.AppSettings["StorageConnection"];
        int SilverCardCodeBalance = Int32.Parse(ConfigurationManager.AppSettings["SilverCardCodeBalance"]);
        string SilverCardCode = ConfigurationManager.AppSettings["SilverCardCode"];
        static string key = ConfigurationManager.AppSettings["FireBaseKey"];
        static string senderId = ConfigurationManager.AppSettings["FireBaseSender"];

        ICardService _cardService;
        ITicketTypeService _ticketTypeService;
        ITicketService _ticketService;
        IBusRouteService _busRouteService;
        IUserService _userService;
        ICreditPlanService _creditPlanService;
        IPaymentTransactionService _paymentTransactionService;
        IScratchCardService _scratchCardService;
        IOfferSubscriptionService _offerSubscriptionService;
        IUserSubscriptionService _userSubscriptionService;

        public ApiController(ICardService cardService, ITicketTypeService ticketTypeService,
            ITicketService ticketService, IBusRouteService busRouteService, IUserService userService,
            ICreditPlanService creditPlanService, IPaymentTransactionService paymentTransactionService,
            IScratchCardService scratchCardService, IOfferSubscriptionService offerSubscriptionService,
        IUserSubscriptionService userSubscriptionService)
        {
            _cardService = cardService;
            _ticketTypeService = ticketTypeService;
            _ticketService = ticketService;
            _busRouteService = busRouteService;
            _userService = userService;
            _creditPlanService = creditPlanService;
            _paymentTransactionService = paymentTransactionService;
            _scratchCardService = scratchCardService;
            _offerSubscriptionService = offerSubscriptionService;
            _userSubscriptionService = userSubscriptionService;
        }



        public JsonResult CrawlBusRoute(string key)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            string endPoint = @"http://apicms.ebms.vn/businfo/getallroute";
            var client = new RestClient(endPoint);
            var json = client.MakeRequest();
            if (json != null)
            {
                List<RouteObject> routes = (List<RouteObject>)
                    JsonConvert.DeserializeObject(json, typeof(List<RouteObject>));
                foreach (var route in routes)
                {
                    BusRoute dbRoute = _busRouteService.GetBusRouteByCode(route.RouteNo);
                    if (dbRoute != null)
                    {
                        dbRoute.Name = route.RouteName;
                        _busRouteService.Update(dbRoute);

                    }
                    else
                    {
                        BusRoute newRoute = new BusRoute();
                        newRoute.Code = route.RouteNo;
                        newRoute.Name = route.RouteName;
                        _busRouteService.Create(newRoute);
                    }
                }

                success = true;
                message = "Bus route is updated!";
            }
            else
            {
                success = false;
                message = "Unable to call api";
                log.Error("Unable to call api to update bus routes");
            }
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }
        public JsonResult SyncCard(string key, string cardId)
        {
            string message = "";
            bool success = false;
            Object result = new { };
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }
            Card card = _cardService.GetCardByUID(cardId);
            if (card == null)
            {
                success = false;
                message = "Thẻ không tồn tại!";
            }
            else
            {
                result = new
                {
                    Balance = card.Balance,
                    DataVersion = card.DataVersion
                };
                success = true;
            }
            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }

        public async Task<JsonResult> PushOfflineData(string key, string cardId, int ticketTypeId, string routeCode, string boughtDate)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            try
            {
                Card card = _cardService.GetCardByUID(cardId);
                TicketType ticketType = _ticketTypeService.GetTicketType(ticketTypeId);
                BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);

                if (card == null) message = "Thẻ không tồn tại.";
                else if (card.Status == (int)StatusReference.CardStatus.BLOCKED) message = "Thẻ đang bị tạm khóa.";
                else if (ticketType == null) message = "Loại vé không tồn tại.";
                else if (busRoute == null) message = "Tuyến xe không tồn tại.";
                else
                {
                    string current = DateTime.Now.ToString("hh:mm:ss tt");
                    DateTime bought = DateTime.ParseExact(boughtDate + " " + current, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                    card.Balance = card.Balance - ticketType.Price;
                    card.DataVersion = long.Parse(DateTime.Now.ToString("ddMMyyyyhhmmss"));
                    _cardService.Update(card);

                    Ticket ticket = new Ticket();
                    ticket.CardId = card.Id;
                    ticket.BusRouteId = busRoute.Id;
                    ticket.TicketTypeId = ticketType.Id;
                    ticket.BoughtDated = bought;
                    ticket.Total = ticketType.Price;
                    ticket.IsNoCard = false;

                    _ticketService.Create(ticket);

                    success = true;
                    message = "Thành công.";

                    //Check balance is running out & if user have installed mobile app
                    if (card.Balance <= minBalance && card.User != null && card.User.NotificationCode != null)
                    {
                        string msg = "Thẻ " + card.UniqueIdentifier + " sắp hết tiền, vui lòng nạp thêm.";
                        if (card.Balance < _creditPlanService.GetMinPlan())
                        {
                            msg = "Thẻ " + card.UniqueIdentifier + " đã hết tiền, vui lòng nạp thêm.";
                        }
                        Task task = SendToFireBase(card.User.NotificationCode, "Green Bus", msg);
                        Task.WhenAll(task);
                    }

                }
            }
            catch (Exception e)
            {
                log.Error(e.Message);
                message = "Hệ thống đang bận, thử lại sau.";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult GetBusStop(string key, string routeCode)
        {
            string message = "";
            bool success = false;
            List<StopObject> goStops = new List<StopObject>();
            List<StopObject> backStops = new List<StopObject>();
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            string endPoint = @"http://apicms.ebms.vn/businfo/getallroute";
            var client = new RestClient(endPoint);
            var json = client.MakeRequest();
            if (json != null)
            {
                try
                {
                    List<RouteObject> routes = (List<RouteObject>)
                    JsonConvert.DeserializeObject(json, typeof(List<RouteObject>));
                    RouteObject targetRoute = routes.Where(r => r.RouteNo.Equals(routeCode)).FirstOrDefault();
                    if (targetRoute != null)
                    {
                        endPoint = @"http://apicms.ebms.vn/businfo/getvarsbyroute/" + targetRoute.RouteId;
                        client = new RestClient(endPoint);
                        json = client.MakeRequest();
                        List<PointObject> points = (List<PointObject>)
                        JsonConvert.DeserializeObject(json, typeof(List<PointObject>));
                        if (points.Count > 0)
                        {
                            endPoint = @"http://apicms.ebms.vn/businfo/getstopsbyvar/" + targetRoute.RouteId + "/" + points[0].RouteVarId;
                            client = new RestClient(endPoint);
                            json = client.MakeRequest();
                            goStops = (List<StopObject>)
                            JsonConvert.DeserializeObject(json, typeof(List<StopObject>));

                            endPoint = @"http://apicms.ebms.vn/businfo/getstopsbyvar/" + targetRoute.RouteId + "/" + points[1].RouteVarId;
                            client = new RestClient(endPoint);
                            json = client.MakeRequest();
                            backStops = (List<StopObject>)
                            JsonConvert.DeserializeObject(json, typeof(List<StopObject>));

                            success = true;
                            message = "Thành công!";
                        }
                    }
                }
                catch
                {
                    success = false;
                    message = "Hệ thống đang bận...";
                    log.Error("Unable to call api to update bus routes");
                }

            }
            else
            {
                success = false;
                message = "Unable to call api";
                log.Error("Unable to call api to update bus routes");
            }
            return Json(new { success = success, message = message, goStops = goStops, backStops = backStops }, JsonRequestBehavior.AllowGet);

        }

        public JsonResult GetRate(string key)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            string json = CommonUtils.GetCurrentRate();
            float rate = 22500;
            if (json != null)
            {
                var obj = JObject.Parse(json);
                rate = (float)obj["results"]["USD_VND"]["val"];
                success = true;
            }

            return Json(new { success = success, message = message, data = new { Rate = rate } }, JsonRequestBehavior.AllowGet);
        }

        //GET: AddCard
        public JsonResult AddCard(string key, string cardId)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }


            if (_cardService.IsCardExist(cardId))
            {
                success = false;
                message = "Thẻ này đã tồn tại trên hệ thống";
            }
            else
            {
                Card card = new Card();
                card.UniqueIdentifier = cardId;
                card.CardName = "Thẻ " + cardId;
                card.Balance = defaultBalance;
                card.RegistrationDate = DateTime.Now;
                card.Status = (int)StatusReference.CardStatus.NON_ACTIVATED;
                card.DataVersion = 0;
                _cardService.Create(card);

                success = true;
                message = "Thêm thẻ vào hệ thống thành công";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        public JsonResult ChangeCardName(string key, string cardId, string name)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }


            if (!_cardService.IsCardExist(cardId))
            {
                success = false;
                message = "Thẻ không tồn tại trên hệ thống";
            }
            else
            {
                Card card = _cardService.GetCardByUID(cardId);
                if (name == null || name.Length == 0)
                    name = "Thẻ " + cardId;

                card.CardName = name;
                _cardService.Update(card);

                success = true;
                message = "Cập nhật tên thẻ thành công";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        public JsonResult RequestAddCard(string key, string phone, string cardId)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (cardId != null && cardId.Length > 0)
            {
                var hubContext = GlobalHost.ConnectionManager.GetHubContext<CardHub>();
                hubContext.Clients.All.autoFill(phone, cardId);
                success = true;
                message = "Server đã nhận thông tin thẻ.";
            }
            else
            {
                success = false;
                message = "Mã thẻ không hợp lệ";
            }


            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult Topup(string key, string cardId, string code)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Card card = _cardService.GetCardByUID(cardId);
            
            if (card != null)
            {
                ScratchCard scCard = _scratchCardService.GetScratchCardByCode(code);
                if (scCard != null && scCard.Status == (int)StatusReference.ScratchCardStatus.AVAILABLE)
                {
                    card.Balance = card.Balance + SilverCardCodeBalance;
                    _cardService.Update(card);

                    scCard.Status = (int)StatusReference.ScratchCardStatus.USED;
                    _scratchCardService.Update(scCard);

                    success = true;
                    message = "Nạp tiền vào thẻ thành công!";
                }
                else
                {
                    success = false;
                    message = "Mã nạp tiền không hợp lệ";
                }
                
            }
            else
            {
                success = false;
                message = "Thẻ không tồn tại";
            }


            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult GetCardInfo(string key, string cardId)
        {
            string message = "";
            bool success = false;
            Object result = null;
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (!_cardService.IsCardExist(cardId))
            {
                success = false;
                message = "Mã thẻ không tồn tại.";
            }
            else
            {
                Card card = _cardService.GetCardByUID(cardId);
                result = new
                {
                    CardId = card.UniqueIdentifier,
                    CardName = card.CardName,
                    Balance = card.Balance,
                    RegistrationDate = card.RegistrationDate.ToString("dd/MM/yyyy"),
                    Status = card.Status
                };

                success = true;
            }

            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);


        }

        public JsonResult AddCardBalance(string key, string cardId, int creditPlanId, string transactionId)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            CreditPlan plan = _creditPlanService.GetCreditPlan(creditPlanId);

            if (!_cardService.IsCardExist(cardId))
            {
                success = false;
                message = "Mã thẻ không tồn tại.";
            }
            else if (plan == null)
            {
                success = false;
                message = "Mã gói nạp không tồn tại.";
            }
            else if (transactionId == null)
            {
                success = false;
                message = "Mã giao dịch từ Paypal không hợp lệ.";
            }
            else
            {
                Card card = _cardService.GetCardByUID(cardId);

                card.Balance = card.Balance + plan.Price;
                _cardService.Update(card);

                PaymentTransaction payment = new PaymentTransaction();
                payment.CardId = card.Id;
                payment.CreditPlanId = creditPlanId;
                payment.TransactionId = transactionId;
                payment.PaymentDate = DateTime.Now;
                payment.Total = plan.Price;
                _paymentTransactionService.Create(payment);

                success = true;
                message = "Cập nhật số dư thành công";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);


        }

        public JsonResult AddCardBalanceByCash(string key, string cardId, int creditPlanId, string staffPhone)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            CreditPlan plan = _creditPlanService.GetCreditPlan(creditPlanId);
            User staff = _userService.GetUserByPhone(staffPhone);
            if (!_cardService.IsCardExist(cardId))
            {
                success = false;
                message = "Mã thẻ không tồn tại.";
            }
            else if (plan == null)
            {
                success = false;
                message = "Mã gói nạp không tồn tại.";
            }
            else if (staff == null)
            {
                success = false;
                message = "Số điện thoại nhân viên không tồn tại.";
            }
            else if (staff.Role.Id != (int) StatusReference.RoleID.STAFF)
            {
                success = false;
                message = "Đăng nhập sai vai trò - chỉ staff mới được quyền thực hiện.";
            }
            else
            {
                Card card = _cardService.GetCardByUID(cardId);

                card.Balance = card.Balance + plan.Price;
                _cardService.Update(card);

                PaymentTransaction payment = new PaymentTransaction();
                payment.CardId = card.Id;
                payment.CreditPlanId = creditPlanId;
                payment.TransactionId = "CASH_PAYMENT_BY_STAFF_"+ staffPhone;
                payment.PaymentDate = DateTime.Now;
                payment.Total = plan.Price;
                _paymentTransactionService.Create(payment);

                success = true;
                message = "Cập nhật số dư thành công";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        //GET: GetUserInfo

        public JsonResult GetStaffInfo(string key, string phone)
        {
            string message = "";
            bool success = false;
            Object result = new { };
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (_userService.IsUserExist(phone))
            {
                User user = _userService.GetUserByPhone(phone);

                if (user.Status == (int)StatusReference.UserStatus.DEACTIVATED)
                {
                    success = false;
                    message = "Tài khoản đang bị khóa.";
                }
                else
                {
                    result = new
                    {
                        UserId = user.UserId,
                        PhoneNumber = user.PhoneNumber,
                        Fullname = user.Fullname
                    };
                    success = true;
                }
            }
            else
            {
                success = false;
                message = "Số điện thoại không tồn tại.";
            }

            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }


        //GET: GetAllCreditPlan
        public JsonResult GetAllCreditPlan(string key)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            IEnumerable<CreditPlan> tmpCreditPlans = _creditPlanService.GetAll()
                .Where(t => t.Status == (int)StatusReference.CreditPlansStatus.ACTIVATED).ToList();

            IEnumerable<Object> creditPlans = tmpCreditPlans.Select(
                c => new
                {
                    Id = c.Id,
                    Name = c.Name,
                    Description = "",
                    Price = c.Price
                }
            );

            success = true;

            return Json(new { success = success, message = message, data = creditPlans }, JsonRequestBehavior.AllowGet);

        }
        //GET: GetAllTiketType
        public JsonResult GetAllTicketType(string key)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            IEnumerable<TicketType> tmpTicketTypes = _ticketTypeService.GetAll()
                .Where(t => t.Status == (int)StatusReference.TicketTypeStatus.ACTIVATED).ToList();

            IEnumerable<Object> ticketTypes = tmpTicketTypes.Select(
                t => new
                {
                    Id = t.Id,
                    Name = t.Name,
                    Price = t.Price,
                    Description = t.Description
                }
            );

            success = true;

            return Json(new { success = success, message = message, data = ticketTypes }, JsonRequestBehavior.AllowGet);

        }

        //GET: GetBusRouteByCode
        public JsonResult GetBusRouteByCode(string key, string routeCode)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);
            Object route = new { };

            if (busRoute == null)
            {
                success = false;
                message = "Tuyến xe không tồn tại.";
            }
            else
            {
                success = true;
                route = new
                {
                    Id = busRoute.Id,
                    Code = busRoute.Code,
                    Name = busRoute.Name
                };
            }
            return Json(new { success = success, message = message, data = route }, JsonRequestBehavior.AllowGet);
        }

        //GET: GetAllBusRoutes
        public JsonResult GetAllBusRoutes(string key)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            IEnumerable<BusRoute> tmpBusRoutes = _busRouteService.GetAll().ToList();

            IEnumerable<Object> busRoutes = tmpBusRoutes.Select(
                b => new
                {
                    Id = b.Id,
                    Code = b.Code,
                    Name = b.Name
                }
            );

            success = true;

            return Json(new { success = success, message = message, data = busRoutes }, JsonRequestBehavior.AllowGet);

        }
        // GET:Ticket
        public async Task<JsonResult> SellTicket(string key, string cardId, int ticketTypeId, string routeCode, long currentBalance, long dataVersion)
        {
            string message = "";
            bool success = false;
            bool needUpdate = false;
            double balance = 0;
            int amount = 0;
            long? version = 0;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            try
            {
                Card card = _cardService.GetCardByUID(cardId);
                TicketType ticketType = _ticketTypeService.GetTicketType(ticketTypeId);
                BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);

                if (card == null) message = "Thẻ không tồn tại.";
                else if (card.Status == (int)StatusReference.CardStatus.BLOCKED) message = "Thẻ đang bị tạm khóa.";
                else if (ticketType == null) message = "Loại vé không tồn tại.";
                else if (busRoute == null) message = "Tuyến xe không tồn tại.";
                else
                {
                    //Check Data Version
                    long clientVersion = dataVersion;
                    long? serverVersion = card.DataVersion;

                    if (serverVersion == null)
                    {
                        serverVersion = 0;
                        card.DataVersion = 0;
                        _cardService.Update(card);
                    }

                    //Server has newest data, get server data
                    if (serverVersion >= clientVersion)
                    {
                        int newPrice = ticketType.Price;
                        var sub = card.User.UserSubscriptions.FirstOrDefault();
                        if (sub != null && sub.IsActive && sub.TicketRemaining > 0 && sub.ExpiredDate >= DateTime.Now)
                        {
                            newPrice = (int)(ticketType.Price * (100 - sub.OfferSubscription.DiscountPercent) / 100);
                        }
                            

                        if (card.Balance < newPrice)
                        {
                            message = "Không đủ số dư để mua vé.";
                            needUpdate = true;
                            balance = card.Balance;
                            amount = newPrice;
                            version = card.DataVersion;
                        }
                        else
                        {
                            card.Balance = card.Balance - newPrice;
                            _cardService.Update(card);

                            Ticket ticket = new Ticket();
                            ticket.CardId = card.Id;
                            ticket.BusRouteId = busRoute.Id;
                            ticket.TicketTypeId = ticketType.Id;
                            ticket.BoughtDated = DateTime.Now;
                            ticket.Total = newPrice;
                            ticket.IsNoCard = false;

                            _ticketService.Create(ticket);

                            success = true;
                            message = "Mua vé thành công.";

                            if (sub != null && sub.IsActive && sub.TicketRemaining > 0 && sub.ExpiredDate >= DateTime.Now)
                            {
                                sub.TicketRemaining = sub.TicketRemaining - 1;
                                _userSubscriptionService.Update(sub);
                            }

                            needUpdate = true;
                            balance = card.Balance;
                            amount = newPrice;
                            version = card.DataVersion;

                            //Check balance is running out & if user have installed mobile app
                            if (card.Balance <= minBalance && card.User != null && card.User.NotificationCode != null)
                            {
                                string msg = "Thẻ " + card.UniqueIdentifier + " sắp hết tiền, vui lòng nạp thêm.";
                                if (card.Balance < _creditPlanService.GetMinPlan())
                                {
                                    msg = "Thẻ " + card.UniqueIdentifier + " đã hết tiền, vui lòng nạp thêm.";
                                }
                                Task task = SendToFireBase(card.User.NotificationCode, "Green Bus", msg);
                                Task.WhenAll(task);
                            }
                        }

                    }
                    //Client has newest data, using client data
                    else
                    {
                        int newPrice = ticketType.Price;
                        var sub = card.User.UserSubscriptions.FirstOrDefault();
                        
                        if (sub != null && sub.IsActive && sub.TicketRemaining > 0 && sub.ExpiredDate >= DateTime.Now)
                        {
                            newPrice = (int)(ticketType.Price * (100 - sub.OfferSubscription.DiscountPercent) / 100);
                        }

                        if (currentBalance < newPrice)
                        {
                            message = "Không đủ số dư để mua vé.";
                        }
                        else
                        {
                            card.Balance = card.Balance - newPrice;
                            _cardService.Update(card);

                            Ticket ticket = new Ticket();
                            ticket.CardId = card.Id;
                            ticket.BusRouteId = busRoute.Id;
                            ticket.TicketTypeId = ticketType.Id;
                            ticket.BoughtDated = DateTime.Now;
                            ticket.Total = newPrice;
                            ticket.IsNoCard = false;

                            _ticketService.Create(ticket);

                            success = true;
                            message = "Mua vé thành công.";

                            if (sub != null && sub.IsActive && sub.TicketRemaining > 0 && sub.ExpiredDate >= DateTime.Now)
                            {
                                sub.TicketRemaining = sub.TicketRemaining - 1;
                                _userSubscriptionService.Update(sub);
                            }

                            needUpdate = false;
                            balance = card.Balance;
                            amount = newPrice;
                            version = dataVersion;
                            //Check balance is running out & if user have installed mobile app
                            if (card.Balance <= minBalance && card.User != null && card.User.NotificationCode != null)
                            {
                                string msg = "Thẻ " + card.UniqueIdentifier + " sắp hết tiền, vui lòng nạp thêm.";
                                if (card.Balance < _creditPlanService.GetMinPlan())
                                {
                                    msg = "Thẻ " + card.UniqueIdentifier + " đã hết tiền, vui lòng nạp thêm.";
                                }
                                Task task = SendToFireBase(card.User.NotificationCode, "Green Bus", msg);
                                Task.WhenAll(task);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.Error(e.Message);
                message = "Hệ thống đang bận, thử lại sau.";
            }

            return Json(new { success = success, message = message, needUpdate = needUpdate, balance = balance, amount = amount, version = version }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult SellCashTicket(string key, int ticketTypeId, string routeCode)
        {
            bool success = false;
            string message = "";


            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }
            try
            {
                TicketType ticketType = _ticketTypeService.GetTicketType(ticketTypeId);
                BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);

                if (ticketType == null) message = "Loại vé không tồn tại.";
                else if (busRoute == null) message = "Tuyến xe không tồn tại.";
                else
                {

                    Ticket ticket = new Ticket();
                    ticket.BusRouteId = busRoute.Id;
                    ticket.TicketTypeId = ticketType.Id;
                    ticket.BoughtDated = DateTime.Now;
                    ticket.Total = ticketType.Price;
                    ticket.IsNoCard = true;

                    _ticketService.Create(ticket);

                    success = true;
                    message = "Mua vé thành công.";


                }
            }
            catch (Exception e)
            {
                log.Error(e.Message);
                message = "Hệ thống đang bận, thử lại sau.";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        public JsonResult PushCashTicketOffline(string key, int ticketTypeId, string routeCode, string boughtDate)
        {
            bool success = false;
            string message = "";


            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }
            try
            {
                TicketType ticketType = _ticketTypeService.GetTicketType(ticketTypeId);
                BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);

                if (ticketType == null) message = "Loại vé không tồn tại.";
                else if (busRoute == null) message = "Tuyến xe không tồn tại.";
                else
                {
                    string current = DateTime.Now.ToString("hh:mm:ss tt");
                    DateTime bought = DateTime.ParseExact(boughtDate + " " + current, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);


                    Ticket ticket = new Ticket();
                    ticket.BusRouteId = busRoute.Id;
                    ticket.TicketTypeId = ticketType.Id;
                    ticket.BoughtDated = bought;
                    ticket.Total = ticketType.Price;
                    ticket.IsNoCard = true;

                    _ticketService.Create(ticket);

                    success = true;
                    message = "Cập nhật thành công.";


                }
            }
            catch (Exception e)
            {
                log.Error(e.Message);
                message = "Hệ thống đang bận, thử lại sau.";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        //POST: Login
        [HttpPost]
        public JsonResult Login(string key, string phone, string password)
        {
            bool success = false;
            string message = "";
            Object result = new { };

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (_userService.IsUserExist(phone))
            {
                User user = _userService.GetUserByPhone(phone);
                if (user.RoleId != (int)StatusReference.RoleID.PASSENGER)
                {
                    success = false;
                    message = "Ứng dụng chỉ dành cho hành khách.";
                }
                else if (user.Status == (int)StatusReference.UserStatus.DEACTIVATED)
                {
                    success = false;
                    message = "Tài khoản của bạn đang bị khóa.";
                }
                else
                {
                    var hashedPassword = CommonUtils.HashPassword(password);
                    if (user.Password.Equals(hashedPassword))
                    {
                        success = true;
                        message = "Đăng nhập thành công!";
                        result = new
                        {
                            PhoneNumber = user.PhoneNumber,
                            Fullname = user.Fullname
                        };
                    }
                    else
                    {
                        success = false;
                        message = "Sai điện thoại hoặc mật khẩu!";
                    }
                }

            }
            else
            {
                success = false;
                message = "Sai điện thoại hoặc mật khẩu!";
            }
            return Json(new { success = success, message = message, data = result });
        }

        //GET: RequestPasswordToken
        public JsonResult RequestPasswordToken(string key, string phone)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUserByPhone(phone);
            if (user != null)
            {
                string token = user.Password.Substring(0, 4);
                string smsMessage = "Neu ban dang yeu cau thay doi mat khau, vui long dien ma xac nhan: " + token;
                SMSMessage.SendSMS(CommonUtils.GlobalingingPhone(phone), smsMessage);

                success = true;
                message = "Vui lòng nhập mã xác nhận được gửi tới điện thoại của bạn";
            }
            else
            {
                success = false;
                message = "Số điện thoại không tồn tại trên hệ thống.";
            }
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        //POST: ChangePassword
        [HttpPost]
        public JsonResult ChangePassword(string key, string phone, string otp, string newPassword)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUserByPhone(phone);
            if (user != null)
            {
                string token = user.Password.Substring(0, 4);
                if (token.Equals(otp))
                {
                    user.Password = CommonUtils.HashPassword(newPassword);
                    _userService.Update(user);

                    success = true;
                    message = "Đổi mật khẩu thành công";
                }
                else
                {
                    success = false;
                    message = "Sai mã xác nhận, vui lòng kiểm tra lại!";
                }
            }
            else
            {
                success = false;
                message = "Số điện thoại không tồn tại trên hệ thống.";
            }
            return Json(new { success = success, message = message });
        }

        //POST: UpdateProfile
        [HttpPost]
        public JsonResult UpdateProfile(string key, string phone, string fullname, string password)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }


            User user = _userService.GetUserByPhone(phone);

            if (user != null)
            {

                user.Fullname = fullname;
                if (password.Trim().Length > 0)
                {
                    user.Password = CommonUtils.HashPassword(password);
                }

                _userService.Update(user);

                success = true;
                message = "Cập nhật thông tin thành công";
            }
            else
            {
                success = false;
                message = "Số điện thoại không tồn tại trên hệ thống.";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        //GET: ActivateAccountByApp
        public JsonResult ActivateAccountByApp(string key, string phone, string cardId)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Card card = _cardService.GetCardByUID(cardId);
            if (card != null)
            {
                if (card.Status != (int)StatusReference.CardStatus.NON_ACTIVATED)
                {
                    message = "Thẻ đã được sử dụng, vui lòng kiểm tra lại!";
                }
                else
                {
                    User user = null;
                    string password = "G" + CommonUtils.GeneratePassword(5);
                    //Matching account
                    if (_userService.IsUserExist(phone))
                    {
                        user = _userService.GetUserByPhone(phone);
                        card.User = user;
                        card.Status = (int)StatusReference.CardStatus.ACTIVATED;
                        card.RegistrationDate = DateTime.Now;
                        _cardService.Update(card);

                        success = true;
                        message = "Kích hoạt thành công, thẻ được thêm vào tài khoản " + phone;
                    }
                    else
                    {
                        if (phone != null && phone.Trim().Length > 0)
                        {
                            user = _userService.AddUser(phone, null, password, (int)StatusReference.RoleID.PASSENGER);
                            card.User = user;
                            card.Status = (int)StatusReference.CardStatus.ACTIVATED;
                            card.RegistrationDate = DateTime.Now;
                            _cardService.Update(card);

                            success = true;
                            message = "Kích hoạt thành công, thông tin tài khoản sẽ được gửi qua SMS";

                            string SmsMessage = "Kich hoat the thanh cong. Tai khoan: " + phone + ". Mat khau: " + password;
                            SMSMessage.SendSMS(CommonUtils.GlobalingingPhone(phone), SmsMessage);
                        }
                        else
                        {
                            success = false;
                            message = "Số điện thoại không được rỗng!";
                        }
                    }

                }

            }
            else
            {
                success = false;
                message = "Mã thẻ không tồn tại, vui lòng kiểm tra lại!";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        //GET: GetAllCard
        public JsonResult GetAllCard(string key, string phone)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            IEnumerable<Object> cards = new List<Object>();

            User user = _userService.GetUserByPhone(phone);
            if (user != null)
            {
                IEnumerable<Card> tmpCards = _cardService.GetCardsByUser(user.UserId);
                cards = tmpCards.Select(
                    c => new
                    {
                        CardId = c.UniqueIdentifier,
                        CardName = c.CardName,
                        RegistrationDate = c.RegistrationDate.ToString("dd/MM/yyyy"),
                        Balance = c.Balance,
                        Status = c.Status
                    }
                );
            }


            success = true;

            return Json(new { success = success, message = message, data = cards }, JsonRequestBehavior.AllowGet);

        }

        //GET; GetReport
        public JsonResult GetReport(string key, string phone, string beginDate, string endDate)
        {
            string message = "";
            bool success = false;
            IEnumerable<Object> report = new List<Object>();
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            try
            {
                String current = DateTime.Now.ToString("hh:mm:ss tt");

                DateTime begin = DateTime.ParseExact(beginDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
                DateTime end = DateTime.ParseExact(endDate + " " + current, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                User user = _userService.GetUserByPhone(phone);
                if (user != null)
                {
                    List<Ticket> tickets = _ticketService.GetTicketByDateRange(user.UserId, begin, end);

                    report = tickets.Select(
                        t => new
                        {
                            BoughtDated = t.BoughtDated.ToString("dd/MM/yyyy hh:mm:ss tt"),
                            BusCode = t.BusRoute.Code,
                            CardName = (t.Card.CardName == null || t.Card.CardName.Length == 0) ? t.Card.UniqueIdentifier : t.Card.CardName,
                            Total = t.Total.ToString("#,##0") + " đ"
                        }
                    );

                    success = true;
                }

            }
            catch (FormatException e)
            {
                log.Error("GetReport input date with wrong format");
                success = false;
                message = "Ngày tháng sai định dạng dd/MM/yyyy";
            }

            return Json(new { success = success, message = message, data = report }, JsonRequestBehavior.AllowGet);

        }

        //GET: RegisterNotificationToken
        public JsonResult RegisterNotificationToken(string key, string phone, string token)
        {
            string message = "";
            bool success = false;

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUserByPhone(phone);
            if (user != null)
            {
                user.NotificationCode = token;
                _userService.Update(user);
                success = true;
                message = "Đăng ký token thành công";
            }
            else
            {
                success = false;
                message = "Số điện thoại không tồn tại trên hệ thống.";
            }
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult MiningBalance(string key)
        {
            string message = "";
            bool success = false;
            List<string> data = new List<string>();
            
            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            DateTime currentDate = DateTime.Now;
            DateTime lastSevenDate = currentDate.AddDays(-7);
            DateTime lastMonthDate = currentDate.AddDays(-30);

            List<User> allMobileUsers = _userService.GetAll().Where(u => u.NotificationCode != null).ToList();
            foreach(var user in allMobileUsers)
            {
                List<Card> cards = user.Cards.Where(c => c.Tickets.Count >= 30 && c.Tickets.Last().BoughtDated >= lastSevenDate).ToList();
                if(cards.Count > 0)
                {
                    foreach(var card in cards)
                    {
                        List<Ticket> tickes = card.Tickets.Where(c => c.BoughtDated >= lastMonthDate).ToList();
                        int total = 0;
                        HashSet<string> dates = new HashSet<string>();
                        foreach(var ticket in tickes)
                        {
                            total += ticket.Total;
                            if (!dates.Contains(ticket.BoughtDated.ToString("dd/MM/yyyy")))
                                dates.Add(ticket.BoughtDated.ToString("dd/MM/yyyy"));
                        }

                        int avg = total / dates.Count;

                        var oneData = "Card: " + card.UniqueIdentifier
                                                + " | Balance: " + card.Balance
                                                + " | Daily Spend: " + avg
                                                + " | Notified: ";

                        if (card.Balance < avg)
                        {
                            var msg = "Số dư thẻ " + card.CardName + " có thể sẽ không đủ chi tiêu trong ngày hôm nay. Bạn nên nạp thêm tiền vào thẻ!";
                            Task task = SendToFireBase(card.User.NotificationCode, "Green Bus", msg);
                            Task.WhenAll(task);
                            oneData += " YES";
                        }
                        else
                        {
                            oneData += " NO";
                        }
                        data.Add(oneData);
                    }
                }
            }
            success = true;
            return Json(new { success = success, message = message, data = data }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult TanPushSms(string key, string phone, string content)
        {
            string message = "";
            bool success = false;

            if (!"tandeptrai".Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            SMSMessage.SendSMS(CommonUtils.GlobalingingPhone(phone), content);
            success = true;
            message = "Server đã gửi tin nhắn cho Tân rồi nha!";
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        private void SendNotification(string code, string msg)
        {
            //CloudStorageAccount account = CloudStorageAccount.Parse(storageConn);

            //CloudQueueClient client = account.CreateCloudQueueClient();
            //CloudQueue queue = client.GetQueueReference("gbtscardbalance");
            //queue.CreateIfNotExists();

            //CloudQueueMessage message = new CloudQueueMessage(code + "*" + msg);

            //queue.AddMessage(message);
            //await SendToFireBase(code, "Green Bus", msg);
        }

        private Task SendToFireBase(string token, string title, string message)
        {
            return Task.Run(() =>
            {
                WebRequest tRequest;
                tRequest = WebRequest.Create("https://fcm.googleapis.com/fcm/send");
                tRequest.Method = "POST";
                tRequest.UseDefaultCredentials = true;

                tRequest.PreAuthenticate = true;

                tRequest.Credentials = CredentialCache.DefaultNetworkCredentials;

                tRequest.ContentType = "application/json";
                tRequest.Headers.Add(string.Format("Authorization: key={0}", key));
                tRequest.Headers.Add(string.Format("Sender: id={0}", senderId));


                string RegArr = token;

                string postData = "{ \"registration_ids\": [ \"" + RegArr + "\" ],\"data\": {\"message\": \"" + message + "\",\"body\": \"" + message + "\",\"title\": \"" + title + "\",\"collapse_key\":\"" + message + "\"}}";

                Byte[] byteArray = Encoding.UTF8.GetBytes(postData);
                tRequest.ContentLength = byteArray.Length;

                Stream dataStream = tRequest.GetRequestStream();
                dataStream.Write(byteArray, 0, byteArray.Length);
                dataStream.Close();

                WebResponse tResponse = tRequest.GetResponse();

                dataStream = tResponse.GetResponseStream();

                StreamReader tReader = new StreamReader(dataStream);

                String sResponseFromServer = tReader.ReadToEnd();

                log.Info(sResponseFromServer);
                tReader.Close();
                dataStream.Close();
                tResponse.Close();
            });

        }

    }
}