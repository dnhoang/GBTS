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
    public class ScratchCardController : Controller
    {
        IUserService _userService;
        IScratchCardService _scratchCardService;
        public ScratchCardController(IUserService userService, IScratchCardService scratchCardService)
        {
            _scratchCardService = scratchCardService;
            _userService = userService;
        }

        // GET: Staff/ScratchCard
        public ActionResult Publish(int num, int price)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            List<ScratchCard> cards = new List<ScratchCard>();
            for(int i=0; i < num; i++)
            {
                ScratchCard card = new ScratchCard();
                card.Price = price;
                card.Status = (int)StatusReference.ScratchCardStatus.AVAILABLE;
                card.Code = "M" + CommonUtils.GeneratePassword(6);

                _scratchCardService.Create(card);
                cards.Add(card);
            }

            ViewBag.Cards = cards;
            return View();
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