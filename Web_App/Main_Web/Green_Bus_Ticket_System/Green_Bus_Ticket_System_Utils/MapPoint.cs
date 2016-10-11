using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public static class MapPoint
    {
        public static void GetSquarePoint(double distance, double lat, double lon, ref MPoint firstPoint, ref MPoint secondPoint)
        {
            double R = 6371;
            double p = (distance / R);

            var middleLat = Math.PI / 180 * lat;
            var middleLon = Math.PI / 180 * lon;

            //Get first point of square
            var brng = Math.PI / 180 * 45;
            firstPoint.lat = Math.Asin(Math.Sin(middleLat) * Math.Cos(p) +
                        Math.Cos(middleLat) * Math.Sin(p) * Math.Cos(brng)) * 180 / Math.PI;
            firstPoint.lon = (middleLon + Math.Atan2(Math.Sin(brng) * Math.Sin(p) * Math.Cos(middleLat),
                                     Math.Cos(p) - Math.Sin(middleLat) * Math.Sin(firstPoint.lat))) * 180 / Math.PI;

            brng = Math.PI / 180 * 225;
            secondPoint.lat = Math.Asin(Math.Sin(middleLat) * Math.Cos(p) +
                        Math.Cos(middleLat) * Math.Sin(p) * Math.Cos(brng)) * 180 / Math.PI;
            secondPoint.lon = (middleLon + Math.Atan2(Math.Sin(brng) * Math.Sin(p) * Math.Cos(middleLat),
                                     Math.Cos(p) - Math.Sin(middleLat) * Math.Sin(secondPoint.lat))) * 180 / Math.PI;

        }

    }
    public class MPoint
    {
        public double lat { get; set; }
        public double lon { get; set; }
    }
}
