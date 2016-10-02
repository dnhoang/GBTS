using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Staff.Controllers
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
        // GET: Staff/Card
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Cards = _cardService.GetAll().OrderByDescending(c=>c.RegistrationDate).ToList();
            return View();
        }

        public JsonResult LockCard(string id)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Card card = _cardService.GetCard(id);
            if (card != null)
            {
                card.Status = (int)StatusReference.CardStatus.BLOCKED;
                _cardService.Update(card);
            }

            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }
        public JsonResult UnLockCard(string id)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            Card card = _cardService.GetCard(id);
            if (card != null)
            {
                card.Status = (int)StatusReference.CardStatus.ACTIVATED;
                _cardService.Update(card);
            }

            success = true;
            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
        }

        public JsonResult AddCard(string cardId, int balance)
        {
            string message = "";
            bool success = false;

            if (!AuthorizeRequest())
            {
                message = "Bạn chưa đăng nhập";
                success = false;
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            if (_cardService.IsCardExist(cardId))
            {
                message = "Thẻ này đã tồn tại trên hệ thống!";
                success = false;
            }
            else
            {
                Card card = new Card();
                card.CardId = cardId;
                card.Balance = balance;
                card.RegistrationDate = DateTime.Now;
                card.CardName = "Thẻ " + cardId;
                card.Status = (int)StatusReference.CardStatus.NON_ACTIVATED;
                _cardService.Create(card);
                success = true;

            }

            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);


        }
        public JsonResult GetCards()
        {
            string message = "";
            bool success = false;
            List<List<string>> result = new List<List<string>>();
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
            }
            else
            {

                List<Card> cards = _cardService.GetAll().OrderByDescending(c => c.RegistrationDate).ToList();

                foreach (Card item in cards)
                {
                    List<string> oneCard = new List<string>();
                    oneCard.Add(item.CardId);
                    oneCard.Add(item.RegistrationDate.ToString("dd/MM/yyyy"));
                    oneCard.Add("Chưa có");
                    oneCard.Add(item.Balance.ToString("#,##0") + " đ");
                    if (item.Status == (int)StatusReference.CardStatus.ACTIVATED)
                    {
                        oneCard.Add("<span class='statuslable' id='" + item.CardId + "'><span class='label label-success'>ĐÃ KÍCH HOẠT</span></span>");
                        oneCard.Add("<button type='button' class='btn btn-danger lockcard' id='" + item.CardId + "'><i class='fa fa-lock'></i> KHÓA THẺ</button>");
                    }
                    else if (item.Status == (int)StatusReference.CardStatus.BLOCKED)
                    {
                        oneCard.Add("<span class='statuslable' id='" + item.CardId + "'><span class='label label-danger'>KHÓA</span></span>");
                        oneCard.Add("<button type='button' class='btn btn-primary unlockcard' id='" + item.CardId + "'><i class='fa fa-unlock'></i> MỞ KHÓA</button>");
                    }
                    else if (item.Status == (int)StatusReference.CardStatus.NON_ACTIVATED)
                    {
                        oneCard.Add("<span class='label label-primary'>CHƯA KÍCH HOẠT</span>");
                        oneCard.Add("");
                    }
                    result.Add(oneCard);
                }

                success = true;
            }
            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);

        }

        private bool AuthorizeRequest()
        {
            User user = (User)Session["user"];
            return (user != null && user.RoleId == (int)StatusReference.RoleID.STAFF);
        }

        private User GetCurrentUser()
        {
            return (User)Session["user"];
        }
    }
}