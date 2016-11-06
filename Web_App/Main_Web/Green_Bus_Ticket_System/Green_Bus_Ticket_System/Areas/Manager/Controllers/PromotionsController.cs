using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Globalization;

namespace Green_Bus_Ticket_System.Areas.Manager.Controllers
{
    public class PromotionsController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        IPromotionService _promotionService;
        IUserService _userService;
        public PromotionsController(IPromotionService promotionService, IUserService userService)
        {
            _promotionService = promotionService;
            _userService = userService;
        }
        // GET: Manager/Promotions
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Promos = _promotionService.GetAll().OrderByDescending(p => p.Id).Take(20).ToList();
            return View();
        }

        public JsonResult GetPromo(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Promotion promo = _promotionService.GetPromotion(id);
            Object result = new { };
            if (promo != null)
            {
                result = new
                {
                    Id = promo.Id,
                    Name = promo.Name,
                    Description = promo.Description,
                    Expired = promo.ExpiredDate.ToString("dd/MM/yyyy")
                };
                success = true;
            }
            else
            {
                message = "Quảng cáo không tồn tại!";
            }


            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);

        }

        public JsonResult CancelPromo(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Promotion promo = _promotionService.GetPromotion(id);
            Object result = new { };
            if (promo != null)
            {
                _promotionService.Delete(promo);

                success = true;
            }
            else
            {
                message = "Quảng cáo không tồn tại!";
            }


            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);

        }


        [HttpPost, ValidateInput(false)]
        public ActionResult AddPromo(FormCollection form)
        {
            if (!AuthorizeRequest())
            {
                ViewBag.Login = true;
            }
            else
            {

                Promotion promo = new Promotion();
                promo.Name = form["name"];
                promo.Description = form["content"];
                promo.ExpiredDate = DateTime.ParseExact(form["expired"], "dd/MM/yyyy", CultureInfo.CurrentCulture);
                promo.CreatedDate = DateTime.Now;

                if(promo.ExpiredDate >= DateTime.Now)
                    promo.Status = (int)StatusReference.PromotionStatus.SENDING;
                else
                    promo.Status = (int)StatusReference.PromotionStatus.SENT;

                _promotionService.Create(promo);

                ViewBag.Login = false;
                ViewBag.Promo = promo;
            }
            return PartialView();
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