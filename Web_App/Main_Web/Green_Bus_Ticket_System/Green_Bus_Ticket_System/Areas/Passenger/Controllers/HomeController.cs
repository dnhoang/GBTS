using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using Green_Bus_Ticket_System_Data.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using log4net;
using System.Reflection;
using Green_Bus_Ticket_System_Data;
using System.Globalization;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class HomeController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        ITicketService _ticketService;
        public HomeController(ICardService cardService, IUserService userService, ITicketService ticketService)
        {
            _cardService = cardService;
            _userService = userService;
            _ticketService = ticketService;
        }
        // GET: Passenger/Home
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Cards = _cardService.GetCardsByUser(GetCurrentUser().UserId).ToList();

            DateTime currentDate = DateTime.Now;
            DateTime firstDate = new DateTime(currentDate.Year, currentDate.Month, 1);

            List<Ticket> tickets = _ticketService.GetTicketByDateRange(
                GetCurrentUser().UserId, firstDate, currentDate);

            ViewBag.TicketNum = tickets.Count;

            int total = 0;
            foreach(var ticket in tickets)
            {
                total += ticket.Total;
            }


            ViewBag.Total = total;

            return View();
        }

        [HttpPost]
        public JsonResult UpdateProfile(string name, string password)
        {

            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = GetCurrentUser();
            user.Fullname = name;
            if(password.Trim().Length > 0)
            {
                user.Password = CommonUtils.HashPassword(password);
            }

            _userService.Update(user);

            success = true;
            message = "Cập nhật thông tin thành công";

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Profiles()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.User = GetCurrentUser();
            return View();
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