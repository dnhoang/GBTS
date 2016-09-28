using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Twilio.TwiML;
using Twilio.TwiML.Mvc;

namespace Green_Bus_Ticket_System.Controllers
{
    public class SMSController : TwilioController
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        public SMSController(ICardService cardService, IUserService userService)
        {
            _cardService = cardService;
            _userService = userService;
        }
        [HttpPost]
        public ActionResult ActivateAccount(string From, string Body)
        {
            //Activate Account Format: GB Card_ID
            string phone = CommonUtils.VietnamingPhone(From);
            if (Body != null && Body.Length > 0)
            {
                string[] data = Body.Split(' ');
                string command = data[0];
                string cardId = data[1];

                string responseMessage = "";

                if (command.Equals("GB", StringComparison.CurrentCultureIgnoreCase))
                {
                    Card card = _cardService.GetCard(cardId);
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
                                _cardService.Update(card);
                                responseMessage = "Kich hoat the thanh cong, the da duoc them vao tai khoan " + phone;
                            }
                            else
                            {
                                user = _userService.AddUser(phone, null, password, (int)StatusReference.RoleID.PASSENGER);
                                card.User = user;
                                card.Status = (int)StatusReference.CardStatus.ACTIVATED;
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
                else
                {
                    responseMessage = "Sai cu phap, vui long kiem tra lai!";
                }

                SMSMessage.SendSMS(From, responseMessage);

            }
            return null;
        }
    }
}