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
    public class PlansController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICreditPlanService _creditPlanService;
        IUserService _userService;
        public PlansController(ICreditPlanService creditPlanService, IUserService userService)
        {
            _creditPlanService = creditPlanService;
            _userService = userService;
        }
        // GET: Manager/Plans
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }
            ViewBag.Plans = _creditPlanService.GetAll().Where(c => c.Status != (int)StatusReference.CreditPlansStatus.DEACTIVATED).ToList();
            return View();
        }

        public JsonResult GetPlan(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            CreditPlan plan = _creditPlanService.GetCreditPlan(id);
            Object result = new { };
            if (plan != null)
            {
                result = new
                {
                    Id = plan.Id,
                    Name = plan.Name,
                    Price = plan.Price
                };
                success = true;
            }
            else
            {
                message = "Gói nạp không tồn tại!";
            }


            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }
        [HttpPost]
        public JsonResult UpdatePlan(int id, string name, int price)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            CreditPlan plan = _creditPlanService.GetCreditPlan(id);
            Object result = new { };
            if (plan != null)
            {
                plan.Name = name;
                plan.Price = price;
                _creditPlanService.Update(plan);

                result = new
                {
                    Id = plan.Id,
                    Name = plan.Name,
                    Price = plan.Price.ToString("#,##0")
                };

                success = true;
            }
            else
            {
                message = "Gói nạp không tồn tại!";
            }


            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }

        [HttpPost]
        public ActionResult AddPlan(string name, int price)
        {

            if (!AuthorizeRequest())
            {
                ViewBag.Login = true;
            }
            else
            {

                CreditPlan plan = new CreditPlan();
                Object result = new { };

                plan.Name = name;
                plan.Price = price;
                plan.Status = (int)StatusReference.CreditPlansStatus.ACTIVATED;
                _creditPlanService.Create(plan);

                ViewBag.Login = false;
                ViewBag.Plan = plan;

            }

            return PartialView();
        }
        public JsonResult DeletePlan(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            CreditPlan plan = _creditPlanService.GetCreditPlan(id);
            Object result = new { };
            if (plan != null)
            {
                if (plan.PaymentTransactions.Count > 0)
                {
                    plan.Status = (int)StatusReference.CreditPlansStatus.DEACTIVATED;
                    _creditPlanService.Update(plan);
                }
                else
                {
                    _creditPlanService.Delete(plan);
                }
                success = true;
            }
            else
            {
                message = "Gói nạp không tồn tại!";
            }


            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
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