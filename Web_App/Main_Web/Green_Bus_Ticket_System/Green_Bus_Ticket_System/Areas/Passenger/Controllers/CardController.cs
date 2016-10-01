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
        IPaymentTransactionService _paymentService;
        public CardController(ICardService cardService, IUserService userService,
            ICreditPlanService creditPlanService, IPaymentTransactionService paymentService)
        {
            _cardService = cardService;
            _userService = userService;
            _creditPlanService = creditPlanService;
            _paymentService = paymentService;
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
            ViewBag.Rate = GetRate();
            ViewBag.Return = "http://localhost:1185/Passenger/Card/Success";
            ViewBag.Cancel = "http://localhost:1185/Passenger/Card/";
            ViewBag.Notify = "http://googlexml.com/paypal/callback.php";
            ViewBag.Receiver = "dongochoangvn-facilitator@gmail.com";

            

            return View();
        }

        [HttpPost]
        public EmptyResult CallBackTransaction(string custom, string txn_id, 
            string payment_status, decimal mc_gross, int item_number)
        {
            string cardId = custom;
            if(cardId != null && cardId.Length > 0)
            {
                Card card = _cardService.GetCard(cardId);
                if (payment_status.Equals("Completed") && card!=null)
                {
                    CreditPlan creditPlan = _creditPlanService.GetCreditPlan(item_number);
                    decimal total = Math.Round((decimal)((float)creditPlan.Price / GetRate()), 2);
                    if(total == mc_gross)
                    {
                        PaymentTransaction payment = new PaymentTransaction();
                        payment.CardId = cardId;
                        payment.CreditPlanId = item_number;
                        payment.TransactionId = txn_id;
                        payment.Total = creditPlan.Price;
                        payment.PaymentDate = DateTime.Now;

                        _paymentService.Create(payment);

                        card.Balance += creditPlan.Price;
                        _cardService.Update(card);
                        
                    }
                }
            }
            return null;
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

        private int GetRate()
        {
            return 22500;
        }
    }
}