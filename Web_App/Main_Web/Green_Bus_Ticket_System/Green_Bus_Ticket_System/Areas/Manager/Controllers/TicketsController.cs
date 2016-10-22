using Green_Bus_Ticket_System_Data;
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
    public class TicketsController : Controller
    {
        private static readonly ILog log = LogManager.GetLogger("WebLog");
        ITicketTypeService _ticketTypeService;
        IUserService _userService;
        public TicketsController(ITicketTypeService ticketTypeService, IUserService userService)
        {
            _ticketTypeService = ticketTypeService;
            _userService = userService;
        }
        // GET: Manager/Tickets
        public ActionResult Index()
        {
            if (!AuthorizeRequest())
            {
                return Redirect("/Access/Login");
            }
            ViewBag.TicketTypes = _ticketTypeService.GetAll().Where(t => t.Status != (int)StatusReference.TicketTypeStatus.DEACTIVATED).ToList();
            return View();
        }

        public JsonResult GetTicketType(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            TicketType ticketType = _ticketTypeService.GetTicketType(id);
            Object result = new { };
            if (ticketType != null)
            {
                result = new
                {
                    Id = ticketType.Id,
                    Name = ticketType.Name,
                    Price = ticketType.Price,
                    Description = ticketType.Description
                };
                success = true;
            }
            else
            {
                message = "Loại vé không tồn tại!";
            }


            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }
        [HttpPost]
        public JsonResult UpdateTicketType(int id, string name, int price, string description)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            TicketType ticketType = _ticketTypeService.GetTicketType(id);
            Object result = new { };
            if (ticketType != null)
            {
                ticketType.Name = name;
                ticketType.Price = price;
                ticketType.Description = description;
                _ticketTypeService.Update(ticketType);

                result = new
                {
                    Id = ticketType.Id,
                    Name = ticketType.Name,
                    Price = ticketType.Price.ToString("#,##0"),
                    Description = ticketType.Description
                };

                success = true;
            }
            else
            {
                message = "Loại vé không tồn tại!";
            }


            return Json(new { success = success, message = message, data = result }, JsonRequestBehavior.AllowGet);
        }

        [HttpPost]
        public ActionResult AddTicketType(string name, int price, string description)
        {

            if (!AuthorizeRequest())
            {
                ViewBag.Login = true;
            }
            else
            {

                TicketType ticketType = new TicketType();
                Object result = new { };

                ticketType.Name = name;
                ticketType.Price = price;
                ticketType.Description = description;
                ticketType.Status = (int)StatusReference.TicketTypeStatus.ACTIVATED;
                _ticketTypeService.Create(ticketType);

                ViewBag.Login = false;
                ViewBag.TicketType = ticketType;

            }

            return PartialView();
        }
        public JsonResult DeleteTicketType(int id)
        {
            bool success = false;
            string message = "";
            if (!AuthorizeRequest())
            {
                success = false;
                message = "Bạn chưa đăng nhập!";
                return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
            }

            TicketType ticketType = _ticketTypeService.GetTicketType(id);
            Object result = new { };
            if (ticketType != null)
            {
                if (ticketType.Tickets.Count > 0)
                {
                    ticketType.Status = (int)StatusReference.TicketTypeStatus.DEACTIVATED;
                    _ticketTypeService.Update(ticketType);
                }
                else
                {
                    _ticketTypeService.Delete(ticketType);
                }
                success = true;
            }
            else
            {
                message = "Loại vé không tồn tại!";
            }


            return Json(new { success = success, message = message }, JsonRequestBehavior.AllowGet);
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