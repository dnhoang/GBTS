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
        IPaymentTransactionService _paymentTransactionService;
        

        public ReportController(IBusRouteService busRouteService, IUserService userService,
            ITicketService ticketService, IPaymentTransactionService paymentTransactionService)
        {
            _busRouteService = busRouteService;
            _userService = userService;
            _ticketService = ticketService;
            _paymentTransactionService = paymentTransactionService;
        }
        // GET: Manager/Report
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime currentDate = DateTime.Now;
            
            DateTime lastThirtyDate = currentDate.AddMonths(-3);

            ViewBag.BeginDate = "01/" + lastThirtyDate.ToString("MM/yyyy");
            ViewBag.EndDate = "01/" + currentDate.ToString("MM/yyyy");
            ViewBag.Busroutes = _busRouteService.GetAll().ToList();

            return View();
        }
        public ActionResult GetReport(string beginDate, string endDate, int route)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime begin = DateTime.ParseExact(beginDate + " 00:00:00 AM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);
            DateTime end = DateTime.ParseExact(endDate + " 11:59:59 PM", "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture).AddDays(-1);

            List<DateTime> monthList = new List<DateTime>();
            for (var date = begin; date <= end; date = date.AddMonths(1))
            {
                monthList.Add(date);
            }
            
            List<Income> cardIncomes = new List<Income>();
            List<Income> cashIncomes = new List<Income>();
            List<Income> scDefaultIncomes = new List<Income>();
            List<Income> scCashIncomes = new List<Income>();
            List<Income> scTopupIncomes = new List<Income>();

            List<PaymentTransaction> paymentDefaults = _paymentTransactionService.GetByDefault();
            List<PaymentTransaction> paymentCashs = _paymentTransactionService.GetByCash();
            List<PaymentTransaction> paymentTopups = _paymentTransactionService.GetByTopup();


            foreach (DateTime month in monthList)
            {
                int cardTotalTicket = _ticketService.GetSoldTicketByDateRange(begin, end)
                    .Where(t => t.BusRouteId == route 
                    && t.BoughtDated.Month == month.Month && t.BoughtDated.Year == month.Year
                    && t.IsNoCard == false).Count();

                long cardTotalIncome = _ticketService.GetSoldTicketByDateRange(begin, end)
                    .Where(t => t.BusRouteId == route
                    && t.BoughtDated.Month == month.Month && t.BoughtDated.Year == month.Year
                    && t.IsNoCard == false).Sum(t => t.Total);

                Income cardIncome = new Income();
                cardIncome.Title = month.ToString("MM/yyyy");
                cardIncome.TotalTicket = cardTotalTicket;
                cardIncome.TotalIncome = cardTotalIncome;

                cardIncomes.Add(cardIncome);

                int cashTotalTicket = _ticketService.GetSoldTicketByDateRange(begin, end)
                    .Where(t => t.BusRouteId == route
                    && t.BoughtDated.Month == month.Month && t.BoughtDated.Year == month.Year
                    && t.IsNoCard == true).Count();

                long cashTotalIncome = _ticketService.GetSoldTicketByDateRange(begin, end)
                    .Where(t => t.BusRouteId == route
                    && t.BoughtDated.Month == month.Month && t.BoughtDated.Year == month.Year
                    && t.IsNoCard == true).Sum(t => t.Total);

                Income cashIncome = new Income();
                cashIncome.Title = month.ToString("MM/yyyy");
                cashIncome.TotalTicket = cashTotalTicket;
                cashIncome.TotalIncome = cashTotalIncome;

                cashIncomes.Add(cashIncome);

                int scDefaultTotalPayment = paymentDefaults.Where(
                    p => p.PaymentDate.Month == month.Month
                     && p.PaymentDate.Year == month.Year).ToList().Count();

                long scDefaultTotalIncome = paymentDefaults.Where(
                    p => p.PaymentDate.Month == month.Month
                     && p.PaymentDate.Year == month.Year).ToList().Sum(p => p.Total);

                Income scDefaultIncome = new Income();
                scDefaultIncome.Title = month.ToString("MM/yyyy");
                scDefaultIncome.TotalTicket = scDefaultTotalPayment;
                scDefaultIncome.TotalIncome = scDefaultTotalIncome;

                scDefaultIncomes.Add(scDefaultIncome);

                int scCashTotalPayment = paymentCashs.Where(
                   p => p.PaymentDate.Month == month.Month
                    && p.PaymentDate.Year == month.Year).ToList().Count();

                long scCashTotalIncome = paymentCashs.Where(
                    p => p.PaymentDate.Month == month.Month
                     && p.PaymentDate.Year == month.Year).ToList().Sum(p => p.Total);

                Income scCashIncome = new Income();
                scCashIncome.Title = month.ToString("MM/yyyy");
                scCashIncome.TotalTicket = scCashTotalPayment;
                scCashIncome.TotalIncome = scCashTotalIncome;

                scCashIncomes.Add(scCashIncome);

                int scTopupTotalPayment = paymentTopups.Where(
                   p => p.PaymentDate.Month == month.Month
                    && p.PaymentDate.Year == month.Year).ToList().Count();

                long scTopupTotalIncome = paymentTopups.Where(
                    p => p.PaymentDate.Month == month.Month
                     && p.PaymentDate.Year == month.Year).ToList().Sum(p => p.Total);

                Income scTopupIncome = new Income();
                scTopupIncome.Title = month.ToString("MM/yyyy");
                scTopupIncome.TotalTicket = scTopupTotalPayment;
                scTopupIncome.TotalIncome = scTopupTotalIncome;

                scTopupIncomes.Add(scTopupIncome);


            }


            ViewBag.CardIncomes = cardIncomes;
            ViewBag.CashIncomes = cashIncomes;
            ViewBag.DefaultPays = scDefaultIncomes;
            ViewBag.CashPays = scCashIncomes;
            ViewBag.TopupPays = scTopupIncomes;

            //var routes = _busRouteService.GetAll().ToList();
            //var byCashRoutes = _busRouteService.GetAll().ToList();

            //List<BusRoute> cardRoutes = new List<BusRoute>();
            //List<BusRoute> cashRoutes = new List<BusRoute>();

            //foreach (BusRoute item in routes)
            //{
            //    BusRoute tmp = new BusRoute();
            //    tmp.Code = item.Code;
            //    tmp.Name = item.Name;
            //    tmp.Tickets = item.Tickets.Where(t =>
            //        t.IsNoCard == false && 
            //        t.BoughtDated >= begin
            //        && t.BoughtDated <= end
            //        && t.BusRouteId == route
            //        ).ToList();

            //    cardRoutes.Add(tmp);
            //}

            //foreach (BusRoute item in byCashRoutes)
            //{
            //    BusRoute tmp = new BusRoute();
            //    tmp.Code = item.Code;
            //    tmp.Name = item.Name;
            //    tmp.Tickets = item.Tickets.Where(t =>
            //        t.IsNoCard == true &&
            //        t.BoughtDated >= begin
            //        && t.BoughtDated <= end
            //        && t.BusRouteId == route
            //        ).ToList();

            //    cashRoutes.Add(tmp);
            //}


            //ViewBag.CardRoutes = cardRoutes;
            //ViewBag.CashRoutes = cashRoutes;

            return PartialView();

        }

        public JsonResult DrawChart(string beginDate, string endDate, int route)
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
            List<long> incomes = new List<long>();

            List<DateTime> monthList = new List<DateTime>();
            for (var date = begin; date <= end; date = date.AddMonths(1))
            {
                monthList.Add(date);
            }


            foreach (DateTime month in monthList)
            {

                long totalIncome = _ticketService.GetSoldTicketByDateRange(begin, end)
                    .Where(t => t.BusRouteId == route
                    && t.BoughtDated.Month == month.Month).Sum(t => t.Total);

               

                lables.Add( month.ToString("MM/yyyy"));
                incomes.Add(totalIncome);

                
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