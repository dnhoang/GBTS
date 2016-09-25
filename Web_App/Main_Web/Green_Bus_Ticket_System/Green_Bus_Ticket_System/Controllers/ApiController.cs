using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Configuration;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Data;
using log4net;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System.Controllers
{
    public class ApiController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        string apiKey = ConfigurationManager.AppSettings["ApiKey"];
        int minBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);

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

                if(card == null) message = "Thẻ không tồn tại.";
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
                        if (card.Balance <= minBalance && card.User.NotificationId != null)
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