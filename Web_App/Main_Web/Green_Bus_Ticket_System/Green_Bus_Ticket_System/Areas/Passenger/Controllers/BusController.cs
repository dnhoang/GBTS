using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class BusController : Controller
    {
        IBusRouteService _busRouteService;
        IUserService _userService;

        public BusController(IBusRouteService busRouteService, IUserService userService)
        {
            _busRouteService = busRouteService;
            _userService = userService;
        }

        // GET: Passenger/Bus
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }
            ViewBag.Routes = _busRouteService.GetAll().ToList();
            return View();
        }

        public ActionResult SearchRoute(string term)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Routes = _busRouteService.GetAll().Where(r => r.Code.Contains(term) || r.Name.ToLower().Contains(term.ToLower())).ToList();
            return PartialView();
        }

        public ActionResult GetMap(double latitude, double longitude)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            ViewBag.Latitude = latitude;
            ViewBag.Longitude = longitude;
            IEnumerable<StopInfo> data = NearestStop.GetNearest(latitude, longitude);
            ViewBag.Marker = data;

            return PartialView(data);
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
    }
}