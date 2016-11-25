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


            return View();
        }

        [HttpPost]
        public EmptyResult CallBackTransaction(string custom, string txn_id, 
            string payment_status, decimal mc_gross, int item_number)
        {
            string cardId = custom;
            if(cardId != null && cardId.Length > 0)
            {
                Card card = _cardService.GetCardByUID(cardId);
                if (payment_status.Equals("Completed") && card!=null)
                {
                    CreditPlan creditPlan = _creditPlanService.GetCreditPlan(item_number);
                    decimal total = ConvertToUSD(creditPlan.Price);
                    if(total == mc_gross)
                    {
                        PaymentTransaction payment = new PaymentTransaction();
                        payment.CardId = card.Id;
                        payment.CreditPlanId = item_number;
                        payment.TransactionCode = txn_id;
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

        [HttpPost]
        public JsonResult UpdateCardName(string id, string name)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Card card = _cardService.GetCardByUID(id);
            if (card != null)
            {
                card.CardName = name;
                _cardService.Update(card);
                success = true;
                message = "Cập nhật tên thẻ thành công";
            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult PayPaltoken(int creditPlanId, string cardId)
        {
            Session["CurrentSectionPayment"] = Guid.NewGuid();

            CreditPlan plan = _creditPlanService.GetCreditPlan(creditPlanId);


            if (plan != null && cardId!=null)
            {
                PayPalOrder objPay = new PayPalOrder();
                objPay.Amount = ConvertToUSD(plan.Price);
                objPay.CreditPlanId = creditPlanId;
                objPay.CreditPlanName = plan.Name;
                objPay.CardId = cardId;
                PayPalRedirect redirect = PayPal.ExpressCheckout(objPay);
                return Json(redirect.Token, JsonRequestBehavior.AllowGet);
            }

            return Json(new { }, JsonRequestBehavior.AllowGet);

        }


        public ActionResult DoCheckoutPayment(string token, string payerID)
        {
            bool success = false;
            string creditPlan = "";
            string cardId = "";
            string transactionId = "";
            bool result = PayPal.DoCheckoutPayment(token, payerID, ref creditPlan, ref transactionId);
            if (result)
            {
                cardId = Session["CurrentCardId"].ToString();

                if (cardId != null)
                {
                    CreditPlan plan = _creditPlanService.GetCreditPlan(Int32.Parse(creditPlan));
                    Card card = _cardService.GetCardByUID(cardId);

                    card.Balance = card.Balance + plan.Price;
                    _cardService.Update(card);

                    PaymentTransaction payment = new PaymentTransaction();
                    payment.CardId = card.Id;
                    payment.CreditPlanId = plan.Id;
                    payment.PaymentDate = DateTime.Now;
                    payment.Total = plan.Price;
                    payment.TransactionCode = transactionId;
                    _paymentService.Create(payment);

                    Session["CurrentCardId"] = null;

                    success = true;
                }
            }

            if (success)
            {
                return Redirect("/Passenger/Card");
            }
            else
            {
                return Redirect("/Passenger/Card/Fail");
            }
        }

        public ActionResult Success()
        {
            return View();
        }
        public ActionResult Fail()
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

        private float GetRate()
        {
            float rate;
            try
            {
                rate = float.Parse(CommonUtils.GetCurrentRate());
            }
            catch
            {
                rate = 22500;
            }
            
            return rate;
        }

        private decimal ConvertToUSD(int vnd)
        {
            return Math.Round((decimal)((float)vnd / GetRate()), 2);
        }
    }
}