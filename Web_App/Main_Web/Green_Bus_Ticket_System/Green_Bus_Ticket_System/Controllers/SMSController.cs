using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Twilio.TwiML;
using Twilio.TwiML.Mvc;

namespace Green_Bus_Ticket_System.Controllers
{
    public class SMSController : TwilioController
    {
        int SilverCardCodeBalance = Int32.Parse(ConfigurationManager.AppSettings["SilverCardCodeBalance"]);
        string SilverCardCode = ConfigurationManager.AppSettings["SilverCardCode"];

        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        IScratchCardService _scratchCardService;
        IOfferSubscriptionService _offerSubscriptionService;
        IUserSubscriptionService _userSubscriptionService;
        IPaymentTransactionService _paymentTransactionService;
        ICreditPlanService _creditPlanService;

        public SMSController(ICardService cardService, IUserService userService,
            IScratchCardService scratchCardService,
            IOfferSubscriptionService offerSubscriptionService,
        IUserSubscriptionService userSubscriptionService,
        IPaymentTransactionService paymentTransactionService,
        ICreditPlanService creditPlanService)
        {
            _cardService = cardService;
            _userService = userService;
            _scratchCardService = scratchCardService;
            _offerSubscriptionService = offerSubscriptionService;
            _userSubscriptionService = userSubscriptionService;
            _paymentTransactionService = paymentTransactionService;
            _creditPlanService = creditPlanService;
        }

        public JsonResult ActivateAccount(string From, string Body)
        {
            //Activate Account Format: GB Card_ID
            string responseMessage = "";
            string phone = CommonUtils.VietnamingPhone(From);
            if (Body != null && Body.Length > 0)
            {
                string[] data = Body.Split(' ');
                string command = data[0];
                



                if (command.Equals("GB", StringComparison.CurrentCultureIgnoreCase))
                {
                    string cardId = data[1];
                    Card card = _cardService.GetCardByUID(cardId);
                    if (card != null)
                    {
                        if (card.Status != (int)StatusReference.CardStatus.NON_ACTIVATED)
                        {
                            responseMessage = "The nay da duoc su dung, vui long kiem tra lai!";
                        }
                        else
                        {
                            User user = null;
                            string password = "G" + CommonUtils.GeneratePassword(5);
                            //Matching account
                            if (_userService.IsUserExist(phone))
                            {
                                user = _userService.GetUserByPhone(phone);
                                card.User = user;
                                card.Status = (int)StatusReference.CardStatus.ACTIVATED;
                                card.RegistrationDate = DateTime.Now;
                                _cardService.Update(card);
                                responseMessage = "Kich hoat the thanh cong, the da duoc them vao tai khoan " + phone;
                            }
                            else
                            {
                                user = _userService.AddUser(phone, null, password, (int)StatusReference.RoleID.PASSENGER);
                                card.User = user;
                                card.Status = (int)StatusReference.CardStatus.ACTIVATED;
                                card.RegistrationDate = DateTime.Now;
                                _cardService.Update(card);
                                responseMessage = "Kich hoat the thanh cong. Tai khoan: " + phone + ". Mat khau: " + password;
                            }
                        }

                    }
                    else
                    {
                        responseMessage = "The khong ton tai, vui long kiem tra lai!";
                    }
                }
                else if (command.Equals("NT", StringComparison.CurrentCultureIgnoreCase))
                {
                    string cardId = data[1];
                    Card card = _cardService.GetCardByUID(cardId);
                    if (card != null)
                    {
                        if (card.Status == (int)StatusReference.CardStatus.BLOCKED)
                        {
                            responseMessage = "The nay da bi khoa, vui long kiem tra lai!";
                        }
                        else
                        {
                            string code = data[2];
                            ScratchCard scCard = _scratchCardService.GetScratchCardByCode(code);
                            if (scCard != null && scCard.Status == (int)StatusReference.ScratchCardStatus.AVAILABLE)
                            {
                                card.Balance = card.Balance + scCard.Price;
                                _cardService.Update(card);

                                scCard.Status = (int)StatusReference.ScratchCardStatus.USED;
                                _scratchCardService.Update(scCard);

                                CreditPlan cp = _creditPlanService.GetAll().FirstOrDefault();
                                PaymentTransaction payment = new PaymentTransaction();
                                payment.CardId = card.Id;
                                payment.CreditPlanId = cp.Id;
                                payment.TransactionId = "TOPU_" + code;
                                payment.PaymentDate = DateTime.Now;
                                payment.Total = scCard.Price;
                                _paymentTransactionService.Create(payment);


                                responseMessage = "Nap tien vao the thanh cong!";
                            }
                            else
                            {
                                responseMessage = "Ma the nap khong hop le";
                            }
                        }

                    }
                    else
                    {
                        responseMessage = "The khong ton tai, vui long kiem tra lai!";
                    }
                }
                else if (command.Equals("MG", StringComparison.CurrentCultureIgnoreCase))
                {
                    string code = data[1];

                    OfferSubscription offer = _offerSubscriptionService.GetOfferSubscriptionByCode(code);
                    User user = _userService.GetUserByPhone(phone);

                    if (user != null)
                    {
                        if (offer != null)
                        {
                            bool isEnough = true;
                            List<Card> cards = user.Cards.Where(c => c.Status == (int)StatusReference.CardStatus.ACTIVATED).ToList();
                            if (cards.Count > 0)
                            {
                                Card targetCard = null;
                                foreach (var item in cards)
                                {
                                    if (item.Balance >= offer.Price)
                                    {
                                        targetCard = item;
                                        break;
                                    }
                                }

                                if (targetCard != null)
                                {
                                    targetCard.Balance = targetCard.Balance - offer.Price;
                                    _cardService.Update(targetCard);
                                }
                                else
                                {
                                    responseMessage = "Khong du so du de mua goi uu dai!";
                                    isEnough = false;
                                }
                            }
                            else
                            {
                                responseMessage = "Ban chua kich hoat the!";
                                isEnough = false;
                            }

                            if (isEnough)
                            {
                                UserSubscription currentSub = _userSubscriptionService.GetUserSubscriptionByPhone(phone);
                                if (currentSub == null)
                                {

                                    //Dang ki moi
                                    var newSub = new UserSubscription();
                                    string expStr = DateTime.Now.AddDays(1).ToString("dd/MM/yyyy") + " 06:00:00 AM";
                                    DateTime expireDate = DateTime.ParseExact(expStr, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                                    newSub.IsActive = true;
                                    newSub.SubscriptionId = offer.Id;
                                    newSub.TicketRemaining = offer.TicketNumber;
                                    newSub.UserId = user.UserId;
                                    newSub.ExpiredDate = expireDate;
                                    _userSubscriptionService.Create(newSub);

                                    responseMessage = "Dang ky goi uu dai thanh cong! De huy tu dong gia han, soan tin HUY gui 14794342404";
                                }
                                else
                                {
                                    string expStr = DateTime.Now.AddDays(1).ToString("dd/MM/yyyy") + " 06:00:00 AM";
                                    DateTime expireDate = DateTime.ParseExact(expStr, "dd/MM/yyyy hh:mm:ss tt", CultureInfo.CurrentCulture);

                                    currentSub.IsActive = true;
                                    currentSub.SubscriptionId = offer.Id;
                                    currentSub.TicketRemaining = offer.TicketNumber;
                                    currentSub.ExpiredDate = expireDate;

                                    _userSubscriptionService.Update(currentSub);

                                    responseMessage = "Kich hoat lai goi uu dai thanh cong!";
                                }
                            }
                            
                        }
                        else
                        {
                            responseMessage = "Goi uu dai khong ton tai, vui long kiem tra lai!";
                        }
                    }
                    else
                    {
                        responseMessage = "Ban chua co tai khoan, vui long kich hoat the de tao tai khoan!";
                    }


                }
                else if (command.Equals("HUY", StringComparison.CurrentCultureIgnoreCase))
                {

                    User user = _userService.GetUserByPhone(phone);

                    if (user != null)
                    {
                        
                        if (user.UserSubscriptions.Count > 0 && user.UserSubscriptions.FirstOrDefault() != null)
                        {
                            var sub = user.UserSubscriptions.FirstOrDefault();
                            sub.IsActive = false;
                            _userSubscriptionService.Update(sub);
                            responseMessage = "Huy tu dong gia han goi uu dai thanh cong! ";
                        }
                        else
                        {
                            responseMessage = "Ban chua tham gia goi uu dai nao ca!";
                        }
                        

                    }
                    else
                    {
                        responseMessage = "Ban chua co tai khoan, vui long kich hoat the de tao tai khoan!";
                    }


                }
                else
                {
                    responseMessage = "Sai cu phap, vui long kiem tra lai!";
                }

                SMSMessage.SendSMS(From, responseMessage);

            }
            return Json(new { message = responseMessage }); ;
        }
    }
}