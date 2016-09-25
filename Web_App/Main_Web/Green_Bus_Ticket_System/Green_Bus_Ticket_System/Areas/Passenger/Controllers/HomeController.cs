using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using Green_Bus_Ticket_System_Data.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using log4net;
using System.Reflection;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class HomeController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ICardService _cardService;
        public HomeController(ICardService cardService)
        {
            _cardService = cardService;
        }
        // GET: Passenger/Home
        public ActionResult Index()
        {
            return View();
        }
    }
}