using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Manager.Controllers
{
    public class ReportController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        IBusRouteService _busRouteService;
        IUserService _userService;
        ITicketService _ticketService;

        public ReportController(IBusRouteService busRouteService, IUserService userService, ITicketService ticketService)
        {
            _busRouteService = busRouteService;
            _userService = userService;
            _ticketService = ticketService;
        }
        // GET: Manager/Report
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


            var routes = _busRouteService.GetAll().ToList();
            var byCashRoutes = _busRouteService.GetAll().ToList();

            List<BusRoute> cardRoutes = new List<BusRoute>();
            List<BusRoute> cashRoutes = new List<BusRoute>();

            foreach (BusRoute item in routes)
            {
                BusRoute tmp = new BusRoute();
                tmp.Code = item.Code;
                tmp.Name = item.Name;
                tmp.Tickets = item.Tickets.Where(t =>
                    t.IsNoCard == false && 
                    t.BoughtDated >= begin
                    && t.BoughtDated <= end
                    ).ToList();

                cardRoutes.Add(tmp);
            }

            foreach (BusRoute item in byCashRoutes)
            {
                BusRoute tmp = new BusRoute();
                tmp.Code = item.Code;
                tmp.Name = item.Name;
                tmp.Tickets = item.Tickets.Where(t =>
                    t.IsNoCard == true &&
                    t.BoughtDated >= begin
                    && t.BoughtDated <= end
                    ).ToList();

                cashRoutes.Add(tmp);
            }

            ViewBag.CardRoutes = cardRoutes;
            ViewBag.CashRoutes = cashRoutes;

            return PartialView();

        }

        public JsonResult DrawChart(string beginDate, string endDate)
        {

            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }


            DateTime begin = DateTime.ParseExact(beginDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
            DateTime end = DateTime.ParseExact(endDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

            List<DateTime> allDates = new List<DateTime>();
            List<string> lables = new List<string>();
            List<double> incomes = new List<double>();

            for (DateTime date = begin; date <= end; date = date.AddDays(3))
            {
                allDates.Add(date);
                lables.Add(date.Day + "/" + date.Month);
            }

            foreach(DateTime item in allDates)
            {
                String cur = item.ToString("dd/MM/yyyy");
                DateTime start = DateTime.ParseExact(cur + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
                DateTime finish = DateTime.ParseExact(cur + " 11:59:59 PM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                List<Ticket> tickets = new List<Ticket>();
                tickets = _ticketService.GetAll().Where(t =>
                    t.BoughtDated >= start && t.BoughtDated <= finish
                ).ToList();

                double total = 0;
                foreach(Ticket ticket in tickets)
                {
                    total += ticket.Total;
                }

                incomes.Add(total);
            }

            success = true;

            return Json(new { success = success, message = message, lables = lables, incomes = incomes }, JsonRequestBehavior.AllowGet);




        }
        private bool AuthorizeRequest()
        {
            User user = (User)Session["user"];
            return (user != null && user.RoleId == (int)StatusReference.RoleID.MANAGER);
        }
        private User GetCurrentUser()
        {
            return (User)Session["user"];
        }
    }
}