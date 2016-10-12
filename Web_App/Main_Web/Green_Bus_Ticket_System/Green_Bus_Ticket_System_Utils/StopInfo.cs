using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public static class NearestStop
    {

        public static IEnumerable<StopInfo> GetNearest(double lat, double lon)
        {

            List<StopInfo> result = new List<StopInfo>();

            MPoint firstPoint = new MPoint();
            MPoint secondPoint = new MPoint();

            //MapPoint.GetSquarePoint(1, lat, lon, ref firstPoint, ref secondPoint);
            //string endPoint = @"http://api.openfpt.vn/fbusinfo/businfo/getstopsinbounds/106.68340787887573/10.768808774874774/106.72228231430054/10.786346643944889";
            //endPoint += firstPoint.lon + "/" + firstPoint.lat + "/";
            //endPoint += secondPoint.lon + "/" + secondPoint.lat;

            //var client = new RestClient(endPoint);
            //var json = client.MakeRequest();

            //if (json != null)
            //{
            //    List<MapStopObject> stops = (List<MapStopObject>)
            //        JsonConvert.DeserializeObject(json, typeof(List<MapStopObject>));
            //    foreach(var stop in stops)
            //    {
            //        StopInfo tmp = new StopInfo();
            //        tmp.Id = stop.StopId;
            //        tmp.Longitude = stop.Lng;
            //        tmp.Latitude = stop.Lat;
            //        tmp.Title = stop.Name;
            //        tmp.ZIndex = stop.StopId;
            //        tmp.ImagePath = "bus.png";

            //        var html = "<h3>"+ stop.StopType + " " + stop.Name + "</h3>";
            //        html += "<p><b>Địa chỉ:</b> "+ stop.AddressNo + " - " 
            //            + stop.Street +  " - " + stop.Zone + "</p>";
            //        html += "<p><b>Tuyến xe dừng:</b> " + stop.Routes + "</p>";

            //        tmp.InfoWindowContent = html;

            //        result.Add(tmp);
            //    }
            //}

            
            return result;

        }



    }
    public class StopInfo
    {
        public int Id { get; set; }

        public double Longitude { get; set; }

        public double Latitude { get; set; }

        public string Title { get; set; }

        public int ZIndex { get; set; }

        public string ImagePath { get; set; }

        public string InfoWindowContent { get; set; }

        public string Address
        {
            get { return this.Title; }
        }
    }
}
