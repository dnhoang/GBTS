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

namespace Green_Bus_Ticket_System.Controllers
{
    public class ApiController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        string apiKey = ConfigurationManager.AppSettings["ApiKey"];
        int minBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);
        int defaultBalance = Int32.Parse(ConfigurationManager.AppSettings["DefaultBalance"]);

        ICardService _cardService;
        ITicketTypeService _ticketTypeService;
        ITicketService _ticketService;
        IBusRouteService _busRouteService;
        public ApiController(ICardService cardService, ITicketTypeService ticketTypeService,
            ITicketService ticketService, IBusRouteService busRouteService)
        {
            _cardService = cardService;
            _ticketTypeService = ticketTypeService;
            _ticketService = ticketService;
            _busRouteService = busRouteService;
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

            IEnumerable<TicketType> ticketTypes = tmpTicketTypes.Select(
                t => new TicketType
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

            IEnumerable<BusRoute> busRoutes = tmpBusRoutes.Select(
                b => new BusRoute
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
                            SendNotification("");
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

        private void SendNotification(string message)
        {
            //Add to cloud queue
        } 
       
    }
}