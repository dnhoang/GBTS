using Green_Bus_Ticket_System_Data;
using Green_Bus_Ticket_System_Data.Services;
using Green_Bus_Ticket_System_Utils;
using log4net;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Passenger.Controllers
{
    public class BusController : Controller
    {
        IBusRouteService _busRouteService;
        IUserService _userService;

        private static readonly ILog log = LogManager.GetLogger("WebLog");
        static string apiServer = ConfigurationManager.AppSettings["CrawlServerAPI"];
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

        public ActionResult Direction()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }
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

        public ActionResult RouteDetails(string routeCode, double latitude, double longitude)
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }

            List<StopObject> goStops = new List<StopObject>();
            List<StopObject> backStops = new List<StopObject>();
            List<StopInfo> result = new List<StopInfo>();

            string endPoint = apiServer + "/businfo/getallroute";
            var client = new RestClient(endPoint);
            var json = client.MakeRequest();
            if (json != null)
            {
                try
                {
                    List<RouteObject> routes = (List<RouteObject>)
                    JsonConvert.DeserializeObject(json, typeof(List<RouteObject>));
                    RouteObject targetRoute = routes.Where(r => r.RouteNo.Equals(routeCode)).FirstOrDefault();
                    if (targetRoute != null)
                    {
                        endPoint = apiServer + "/businfo/getvarsbyroute/" + targetRoute.RouteId;
                        client = new RestClient(endPoint);
                        json = client.MakeRequest();
                        List<PointObject> points = (List<PointObject>)
                        JsonConvert.DeserializeObject(json, typeof(List<PointObject>));
                        if (points.Count > 0)
                        {
                            endPoint = apiServer + "/businfo/getstopsbyvar/" + targetRoute.RouteId + "/" + points[0].RouteVarId;
                            client = new RestClient(endPoint);
                            json = client.MakeRequest();
                            goStops = (List<StopObject>)
                            JsonConvert.DeserializeObject(json, typeof(List<StopObject>));

                            endPoint = apiServer + "/businfo/getstopsbyvar/" + targetRoute.RouteId + "/" + points[1].RouteVarId;
                            client = new RestClient(endPoint);
                            json = client.MakeRequest();
                            backStops = (List<StopObject>)
                            JsonConvert.DeserializeObject(json, typeof(List<StopObject>));
                        }
                    }

                    foreach (var stop in goStops)
                    {
                        StopInfo tmp = new StopInfo();
                        tmp.Id = stop.StopId;
                        tmp.Longitude = stop.Lng;
                        tmp.Latitude = stop.Lat;
                        tmp.Title = stop.Name;
                        tmp.ZIndex = stop.StopId;
                        tmp.ImagePath = "bus.png";

                        var html = "<h3>" + stop.StopType + " " + stop.Name + "</h3>";
                        html += "<p><b>Địa chỉ:</b> " + stop.AddressNo + " - "
                            + stop.Street + " - " + stop.Zone + "</p>";
                        html += "<p><b>Tuyến xe dừng:</b> " + stop.Routes + "</p>";

                        tmp.InfoWindowContent = html;

                        result.Add(tmp);
                    }
                }
                catch
                {
                    log.Error("Can not call api!");
                }

            }



            ViewBag.Latitude = latitude;
            ViewBag.Longitude = longitude;
            return PartialView(result);
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