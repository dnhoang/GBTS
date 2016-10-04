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

namespace Green_Bus_Ticket_System.Controllers
{
    public class ApiController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        string apiKey = ConfigurationManager.AppSettings["ApiKey"];
        int minBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);
        int defaultBalance = Int32.Parse(ConfigurationManager.AppSettings["DefaultBalance"]);
        string storageConn = ConfigurationManager.AppSettings["StorageConnection"];

        ICardService _cardService;
        ITicketTypeService _ticketTypeService;
        ITicketService _ticketService;
        IBusRouteService _busRouteService;
        IUserService _userService;
        public ApiController(ICardService cardService, ITicketTypeService ticketTypeService,
            ITicketService ticketService, IBusRouteService busRouteService, IUserService userService)
        {
            _cardService = cardService;
            _ticketTypeService = ticketTypeService;
            _ticketService = ticketService;
            _busRouteService = busRouteService;
            _userService = userService;

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
                card.CardId = cardId;
                card.CardName = "Thẻ " + cardId;
                card.Balance = defaultBalance;
                card.RegistrationDate = DateTime.Now;
                card.Status = (int) StatusReference.CardStatus.NON_ACTIVATED;
                _cardService.Create(card);

                success = true;
                message = "Thêm thẻ vào hệ thống thành công";
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

            if(cardId !=null && cardId.Length > 0)
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

            if(busRoute == null)
            {
                success = false;
                message = "Tuyến xe không tồn tại.";
            }else
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
        // GET: SellTicket
        public JsonResult SellTicket(string key, string cardId, int ticketTypeId, string routeCode)
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
                Card card = _cardService.GetCard(cardId);
                TicketType ticketType = _ticketTypeService.GetTicketType(ticketTypeId);
                BusRoute busRoute = _busRouteService.GetBusRouteByCode(routeCode);

                if (card == null) message = "Thẻ không tồn tại.";
                else if (card.Status == (int)StatusReference.CardStatus.BLOCKED) message = "Thẻ đang bị tạm khóa.";
                else if(ticketType == null) message = "Loại vé không tồn tại.";
                else if (busRoute == null) message = "Tuyến xe không tồn tại.";
                else
                {
                    if(card.Balance < ticketType.Price)
                    {
                        message = "Không đủ số dư để mua vé.";
                    }else
                    {
                        card.Balance = card.Balance - ticketType.Price;
                        _cardService.Update(card);

                        Ticket ticket = new Ticket();
                        ticket.CardId = card.CardId;
                        ticket.BusRouteId = busRoute.Id;
                        ticket.TicketTypeId = ticketType.Id;
                        ticket.BoughtDated = DateTime.Now;
                        ticket.Total = ticketType.Price;

                        _ticketService.Create(ticket);

                        success = true;
                        message = "Mua vé thành công.";

                        //Check balance is running out & if user have installed mobile app
                        if (card.Balance <= minBalance && card.User.NotificationCode != null)
                        {
                            SendNotification(card.User.NotificationCode);
                        }
                    }
                }
            }
            catch(Exception e)
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

            if (!apiKey.Equals(key))
            {
                message = "Sai api key.";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (_userService.IsUserExist(phone))
            {
                User user = _userService.GetUserByPhone(phone);
                if(user.RoleId != (int)StatusReference.RoleID.PASSENGER)
                {
                    success = false;
                    message = "Ứng dụng chỉ dành cho hành khách.";
                }
                else
                {
                    var hashedPassword = CommonUtils.HashPassword(password);
                    if (user.Password.Equals(hashedPassword))
                    {
                        success = true;
                        message = "Đăng nhập thành công!";
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
            return Json(new { success = success, message = message });
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

            Card card = _cardService.GetCard(cardId);
            if (card != null)
            {
                if (card.Status != (int)StatusReference.CardStatus.NON_ACTIVATED)
                {
                    message = "Thẻ đã được sử dụng, vui lòng kiểm tra lại!";
                }
                else
                {
                    User user = null;
                    string password = CommonUtils.GeneratePassword(8);
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
                        CardId = c.CardId,
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
                if(user != null)
                {
                    List<Ticket> tickets = _ticketService.GetTicketByDateRange(user.UserId, begin, end);

                    report = tickets.Select(
                        t => new
                        {
                            BoughtDated = t.BoughtDated.ToString("dd/MM/yyyy hh:mm:ss tt"),
                            BusCode = t.BusRoute.Code,
                            CardName = (t.Card.CardName == null || t.Card.CardName.Length == 0) ? t.CardId : t.Card.CardName,
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
        private void SendNotification(string code)
        {
            CloudStorageAccount account = CloudStorageAccount.Parse(storageConn);

            CloudQueueClient client = account.CreateCloudQueueClient();
            CloudQueue queue = client.GetQueueReference("gbtscardbalance");
            queue.CreateIfNotExists();

            CloudQueueMessage message = new CloudQueueMessage(code);

            queue.AddMessage(message);
        } 
       
    }
}