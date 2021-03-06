﻿using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Globalization;

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
        public ActionResult Publish(int num, string type)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            List<ScratchCard> cards = new List<ScratchCard>();
            string[] types = type.Split('|');
            foreach(var t in types)
            {
                int price = Int32.Parse(t);
                for (int i = 0; i < num; i++)
                {
                    ScratchCard card = new ScratchCard();
                    card.Price = price;
                    card.Status = (int)StatusReference.ScratchCardStatus.AVAILABLE;
                    card.Code = CommonUtils.GeneratePre(1) + CommonUtils.GeneratePassword(6);
                    card.ExpiredDate = DateTime.Now.AddMonths(6);

                    _scratchCardService.Create(card);
                    cards.Add(card);
                }

            }



            ViewBag.Cards = cards;
            return View();
        }
        public ActionResult History(string date)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime pDate = DateTime.ParseExact(date, "dd-MM-yyyy", CultureInfo.CurrentCulture).AddMonths(6);

            List<ScratchCard> cards = _scratchCardService.GetAll().
                Where(s => s.ExpiredDate.Day == pDate.Day
                            && s.ExpiredDate.Month == pDate.Month
                            && s.ExpiredDate.Year == pDate.Year).ToList();


            ViewBag.Cards = cards;
            return View();
        }

        public ActionResult GetDate()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            List<String> dates = _scratchCardService.GetAll().Select(s => s.ExpiredDate.AddMonths(-6).ToString("dd-MM-yyyy")).Distinct().ToList();
            ViewBag.Dates = dates;

            return PartialView();
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