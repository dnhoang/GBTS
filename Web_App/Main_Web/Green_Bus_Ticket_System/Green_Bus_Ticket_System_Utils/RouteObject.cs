using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public class RouteObject
    {
        public int RouteId { get; set; }
        public string RouteNo { get; set; }
        public string RouteName { get; set; }
    }

    public class StopObject
    {
        public int StopId { get; set; }
        public string Code { get; set; }
        public string Name { get; set; }
        public string StopType { get; set; }
        public string Zone { get; set; }
        public string Ward { get; set; }
        public string AddressNo { get; set; }
        public string Street { get; set; }
        public string SupportDisability { get; set; }
        public string Status { get; set; }
        public double Lng { get; set; }
        public double Lat { get; set; }
        public string Search { get; set; }
        public string Routes { get; set; }
    }

    public class PointObject
    {
        public int RouteId { get; set; }
        public int RouteVarId { get; set; }
        public string RouteVarName { get; set; }
        public string RouteVarShortName { get; set; }
        public string RouteNo { get; set; }
        public string StartStop { get; set; }
        public string EndStop { get; set; }
        public double Distance { get; set; }
        public bool Outbound { get; set; }
        public int RunningTime { get; set; }
    }

    public class MapStopObject
    {
        public int StopId { get; set; }
        public string Code { get; set; }
        public string Name { get; set; }
        public string StopType { get; set; }
        public string Zone { get; set; }
        public string Ward { get; set; }
        public string AddressNo { get; set; }
        public string Street { get; set; }
        public string SupportDisability { get; set; }
        public string Status { get; set; }
        public double Lng { get; set; }
        public double Lat { get; set; }
        public string Search { get; set; }
        public string Routes { get; set; }
    }
}
