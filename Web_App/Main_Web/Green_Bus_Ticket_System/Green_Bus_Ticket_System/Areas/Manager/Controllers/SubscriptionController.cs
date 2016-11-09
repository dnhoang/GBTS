using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Manager.Controllers
{
    public class SubscriptionController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        IUserService _userService;
        IOfferSubscriptionService _subscriptionService;
        public SubscriptionController(IUserService userService, IOfferSubscriptionService subscriptionService)
        {
            _subscriptionService = subscriptionService;
            _userService = userService;
        }
        // GET: Manager/Subscription
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Subscriptions = _subscriptionService.GetAll()
                .Where(s => s.Status == (int)StatusReference.SubscriptionStatus.ACTIVATED).ToList();

            return View();
        }
        public ActionResult CreateForm()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

           
            return PartialView();
        }
        public JsonResult Delete(int id)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            OfferSubscription sub = _subscriptionService.GetOfferSubscription(id);
            if (sub != null)
            {
                sub.Code = "";
                sub.Status = (int)StatusReference.SubscriptionStatus.DEACTIVATED;

                _subscriptionService.Update(sub);
            }

            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult Update(int id, string name, string code, int price, int percent, int num)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            OfferSubscription sub = _subscriptionService.GetOfferSubscription(id);
            if (sub != null)
            {
                sub.Name = name;
                sub.Price = price;
                sub.TicketNumber = num;
                sub.DiscountPercent = percent;
                sub.Code = code;
                sub.Status = (int)StatusReference.SubscriptionStatus.ACTIVATED;

                _subscriptionService.Update(sub);
            }

            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult Create(string name, string code, int price, int percent, int num)
        {
            string message = "";
            bool success = false;
            var id = 0;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            OfferSubscription sub = _subscriptionService.GetOfferSubscriptionByCode(code);
            if (sub == null)
            {
                sub = new OfferSubscription();
                sub.Name = name;
                sub.Price = price;
                sub.TicketNumber = num;
                sub.DiscountPercent = percent;
                sub.Code = code;
                sub.Status = (int)StatusReference.SubscriptionStatus.ACTIVATED;

                _subscriptionService.Create(sub);
                id = sub.Id;
                success = true;
            }
            else
            {
                success = false;
                message = "Đã tồn tại ưu đãi với mã: " + code;
            }

            
            return Json(new { success = success, message = message, Id = id }, JsonRequestBehavior.AllowGet);
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