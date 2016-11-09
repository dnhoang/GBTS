using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Admin.Controllers
{
    public class HomeController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        IUserService _userService;
        public HomeController(IUserService userService)
        {
            _userService = userService;
        }
        // GET: Admin/Home
        public ActionResult Index()
        {
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
            if (password.Trim().Length > 0)
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
            return (user != null && user.RoleId == (int)StatusReference.RoleID.ADMIN);
        }
        private User GetCurrentUser()
        {
            return (User)Session["user"];
        }
    }
}