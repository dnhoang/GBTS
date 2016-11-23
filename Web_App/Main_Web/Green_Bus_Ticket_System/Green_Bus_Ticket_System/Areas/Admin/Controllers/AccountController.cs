using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Admin.Controllers
{
    public class AccountController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        IUserService _userService;
        IRoleService _roleService;
        public AccountController(IUserService userService, IRoleService roleService)
        {
            _userService = userService;
            _roleService = roleService;

        }
        // GET: Admin/Account
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }
            ViewBag.Roles = _roleService.GetAll().ToList();
            return View();
        }

        public ActionResult SearchAccount(string term)
        {
            List<User> users = _userService.GetAll().Where(u => u.PhoneNumber.Contains(term)
            && u.RoleId != (int)StatusReference.RoleID.ADMIN).ToList();
            ViewBag.Users = users;
            return PartialView();
        }



        public JsonResult Lock(int id)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUser(id);
            if (user != null)
            {
                user.Status = (int)StatusReference.UserStatus.DEACTIVATED;
                _userService.Update(user);
            }

            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }
        public JsonResult UnLock(int id)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            User user = _userService.GetUser(id);
            if (user != null)
            {
                user.Status = (int)StatusReference.UserStatus.ACTIVATED;
                _userService.Update(user);
            }


            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult AddAccount(string phone, int role)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (_userService.IsUserExist(phone))
            {
                message = "Số điện thoại đã tồn tại trên hệ thống!";
                success = false;
            }
            else
            {
                Role roleSv = _roleService.GetRole(role);
                if(roleSv != null)
                {
                    User user = new User();
                    user.Fullname = roleSv.Name;
                    user.RoleId = role;
                    user.PhoneNumber = phone;
                    user.Status = (int)StatusReference.UserStatus.ACTIVATED;
                    string password = "G" + CommonUtils.GeneratePassword(5);
                    user.Password = CommonUtils.HashPassword(password);
                    user.MinBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);
                    _userService.Create(user);

                    string responseMessage = "Tai khoan: " + phone + ". Mat khau: " + password;
                    SMSMessage.SendSMS(CommonUtils.GlobalingingPhone(phone), responseMessage);
                    success = true;
                }else
                {
                    message = "Vai trò không tồn tại!";
                    success = false;
                }
                

            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);


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