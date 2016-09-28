using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class CardController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        ICreditPlanService _creditPlanService;
        public CardController(ICardService cardService, IUserService userService,
            ICreditPlanService creditPlanService)
        {
            _cardService = cardService;
            _userService = userService;
            _creditPlanService = creditPlanService;
        }
        // GET: Passenger/Card
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Cards = _cardService.GetCardsByUser(GetCurrentUser().UserId);
            var creditPlans = _creditPlanService.GetAll().
                Where(c => c.Status == (int)StatusReference.CreditPlansStatus.ACTIVATED).ToList();

            ViewBag.CreditPlans = creditPlans;


            ViewBag.PhoneNumber = GetCurrentUser().PhoneNumber;

            //Hard code here
            ViewBag.Rate = 22500;
            ViewBag.Return = "http://localhost:1185/Passenger/Card/Success";
            ViewBag.Cancel = "http://localhost:1185/Passenger/Card/";
            ViewBag.Notify = "http://googlexml.com/paypal/callback.php";
            ViewBag.Receiver = "dongochoangvn-facilitator@gmail.com";

            

            return View();
        }

        public ActionResult Success()
        {
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