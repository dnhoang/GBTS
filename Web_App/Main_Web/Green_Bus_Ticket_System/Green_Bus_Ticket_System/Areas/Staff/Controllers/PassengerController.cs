using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Staff.Controllers
{
    public class PassengerController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        ICreditPlanService _creditPlanService;
        IPaymentTransactionService _paymentService;
        public PassengerController(ICardService cardService, IUserService userService,
            ICreditPlanService creditPlanService, IPaymentTransactionService paymentService)
        {
            _cardService = cardService;
            _userService = userService;
            _creditPlanService = creditPlanService;
            _paymentService = paymentService;
        }
        // GET: Staff/Passenger
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Users = _userService.GetAll().OrderByDescending(c => c.UserId).ToList();
            return View();
        }

        public ActionResult GetUser(string phone)
        {

            if (!AuthorizeRequest())
            {
                ViewBag.Message = "Vui lòng đăng nhập lại!";
                return PartialView();
            }

            User user = _userService.GetUserByPhone(phone);
            Object result = new { };

            if (user != null)
            {
                ViewBag.User = user;
            }
            else
            {
                ViewBag.Message = "Số điện thoại không tồn tại!";
            }
            return PartialView();
        }

        public JsonResult UpdateName(string phone, string name)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUserByPhone(phone);
            if(user != null)
            {
                user.Fullname = name;
                _userService.Update(user);
                success = true;
            }
            else
            {
                success = false;
                message = "Khách hàng này không tồn tại hoặc đã bị xóa.";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }

        private bool AuthorizeRequest()
        {
            User user = (User)Session["user"];
            return (user != null && user.RoleId == (int)StatusReference.RoleID.STAFF);
        }

        private User GetCurrentUser()
        {
            return (User)Session["user"];
        }
    }
}