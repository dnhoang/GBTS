using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Controllers
{
    public class AccessController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");

        IUserService _userService;
        public AccessController(IUserService userService)
        {
            _userService = userService;
        }

        // GET: Access/Login
        public ActionResult Login()
        {
            return View();
        }

        [HttpPost]
        public JsonResult LoginAccount(string phone, string password)
        {
            bool success = false;
            string message = "";
            string url = "";
            if (_userService.IsUserExist(phone))
            {
                User user = _userService.GetUserByPhone(phone);
                var hashedPassword = CommonUtils.HashPassword(password);
                if (user.Password.Equals(hashedPassword))
                {
                    Session["user"] = user;
                    success = true;
                    message = "Đăng nhập thành công!";
                    if (user.RoleId == (int)StatusReference.RoleID.ADMIN)
                        url = "/Admin/Home/";
                    else if (user.RoleId == (int)StatusReference.RoleID.MANAGER)
                        url = "/Manager/Home/";
                    else if (user.RoleId == (int)StatusReference.RoleID.STAFF)
                        url = "/Staff/Home/";
                    else if (user.RoleId == (int)StatusReference.RoleID.PASSENGER)
                        url = "/Passenger/Home/";                    
                }
                else
                {
                    success = false;
                    message = "Sai điện thoại hoặc mật khẩu!";
                }
            }
            else
            {
                success = false;
                message = "Sai điện thoại hoặc mật khẩu!";
            }
            return Json(new { success = success, message = message, url = url });
        }


    }
}