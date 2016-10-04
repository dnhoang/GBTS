using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Controllers
{
    public class DefaultController : Controller
    {
        // GET: Home
        public ActionResult Index()
        {
            return Redirect("/Access/Login");
        }
    }
}