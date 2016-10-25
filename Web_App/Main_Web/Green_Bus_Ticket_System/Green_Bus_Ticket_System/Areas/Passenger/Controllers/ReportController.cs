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
        ICardService _cardService;
        public ReportController(ITicketService ticketService, ICardService cardService)
        {
            _ticketService = ticketService;
            _cardService = cardService;
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


            var cards = _cardService.GetCardsByUser(GetCurrentUser().UserId);

            List<Card> filteredCards = new List<Card>();
            foreach(Card item in cards)
            {
                item.Tickets = item.Tickets.Where(t =>
                    t.BoughtDated >= lastThirtyDate
                    && t.BoughtDated <= currentDate
                    ).OrderByDescending(p => p.BoughtDated).ToList();
                filteredCards.Add(item);
            }

            ViewBag.Cards = filteredCards;

            return View();
        }

        public ActionResult GetReport(string beginDate, string endDate)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime begin = DateTime.ParseExact(beginDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
            DateTime end = DateTime.ParseExact(endDate + " 11:59:59 PM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

           
            var cards = _cardService.GetCardsByUser(GetCurrentUser().UserId);

            List<Card> filteredCards = new List<Card>();
            foreach (Card item in cards)
            {
                item.Tickets = item.Tickets.Where(t =>
                    t.BoughtDated >= begin
                    && t.BoughtDated <= end
                    ).OrderByDescending(p => p.BoughtDated).ToList();
                filteredCards.Add(item);
            }

            ViewBag.Cards = filteredCards;

            return PartialView();

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