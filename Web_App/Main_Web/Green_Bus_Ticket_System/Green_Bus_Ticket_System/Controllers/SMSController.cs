using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Configuration;
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
        public SMSController(ICardService cardService, IUserService userService)
        {
            _cardService = cardService;
            _userService = userService;
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
                string cardId = data[1];

                

                if (command.Equals("GB", StringComparison.CurrentCultureIgnoreCase))
                {
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
                            string password = CommonUtils.GeneratePassword(8);
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
                            if (code.Equals(SilverCardCode))
                            {
                                card.Balance = card.Balance + SilverCardCodeBalance;
                                _cardService.Update(card);

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
                else
                {
                    responseMessage = "Sai cu phap, vui long kiem tra lai!";
                }

                SMSMessage.SendSMS(From, responseMessage);

            }
            return Json( new { message = responseMessage } ); ;
        }
    }
}