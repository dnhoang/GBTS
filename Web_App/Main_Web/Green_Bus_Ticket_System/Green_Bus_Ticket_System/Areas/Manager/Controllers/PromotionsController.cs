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

            ViewBag.Promos = _promotionService.GetAll().OrderByDescending(p => p.Id).Take(5).ToList();
            return View();
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