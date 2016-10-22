﻿using Green_Bus_Ticket_System_Data;
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
    public class HomeController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        IUserService _userService;
        ITicketService _ticketService;
        public HomeController(ICardService cardService, IUserService userService, ITicketService ticketService)
        {
            _cardService = cardService;
            _userService = userService;
            _ticketService = ticketService;
        }
        // GET: Manager/Home
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            DateTime currentDate = DateTime.Now;
            DateTime firstDate = new DateTime(currentDate.Year, currentDate.Month, 1);

            List<Ticket> tickets = _ticketService.GetSoldTicketByDateRange(firstDate, currentDate);

            ViewBag.TicketNum = tickets.Count;

            int total = 0;
            foreach (var ticket in tickets)
            {
                total += ticket.Total;
            }
            ViewBag.Total = total;

            ViewBag.NumCard = _cardService.GetNewActivateCardNum(firstDate, currentDate);

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