using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System.Globalization;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class ReportController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ITicketService _ticketService;
        public ReportController(ITicketService ticketService)
        {
            _ticketService = ticketService;
        }
        // GET: Passenger/Report
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime currentDate = DateTime.Now;
            DateTime lastThirtyDate = currentDate.AddMonths(-1);

            ViewBag.BeginDate = lastThirtyDate.ToString("dd/MM/yyyy");
            ViewBag.EndDate = currentDate.ToString("dd/MM/yyyy");

            ViewBag.Tickets = _ticketService.GetTicketByDateRange(
                GetCurrentUser().UserId, lastThirtyDate, currentDate);

            return View();
        }

        public JsonResult GetReport(string beginDate, string endDate)
        {
            string message = "";
            bool success = false;
            List<List<string>> result = new List<List<string>>();
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
            }
            else
            {
                String current = DateTime.Now.ToString("hh:mm:ss tt");
                
                DateTime begin = DateTime.ParseExact(beginDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
                DateTime end = DateTime.ParseExact(endDate + " " + current, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                List<Ticket> tickets = _ticketService.GetTicketByDateRange(
                    GetCurrentUser().UserId, begin, end);

                
                foreach (Ticket item in tickets)
                {
                    List<string> oneTicket = new List<string>();
                    oneTicket.Add(item.BoughtDated.ToString("dd/MM/yyyy hh:mm:ss tt"));
                    oneTicket.Add(item.BusRoute.Code);
                    oneTicket.Add((item.Card.CardName == null || item.Card.CardName.Length == 0) ? item.CardId : item.Card.CardName);
                    oneTicket.Add(item.Total.ToString("#,##0") + " đ");
                    result.Add(oneTicket);
                }

                success = true;
            }
            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
            
        }
        

        private bool AuthorizeRequest()
        {
            User user = (User)Session["user"];
            return (user != null && user.RoleId == (int)StatusReference.RoleID.PASSENGER);
        }

        private User GetCurrentUser()
        {
            return (User)Session["user"];
        }
    }
}