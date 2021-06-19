using GasStationWeb.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Web;
using System.Web.Mvc;

namespace GasStationWeb.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }

        public ActionResult MobileApp()
        {
            return View();
        }
        public ActionResult Map()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Contact(string customerMail, string subject, string message)
        {
            try
            {
                if (ModelState.IsValid)
                {

                    var senderEmail = new MailAddress(System.Configuration.ConfigurationManager.AppSettings["mail:addres"], "BeastInc");
                    var receiverEmail = new MailAddress(System.Configuration.ConfigurationManager.AppSettings["mail:addres"], customerMail);
                    var password = System.Configuration.ConfigurationManager.AppSettings["mail:pass"];
                    var sub = subject;
                    var body = message;
                    var smtp = new SmtpClient  //URL(istemci),model
                    {
                        Host = "smtp.gmail.com",
                        Port = 587,
                        EnableSsl = true,
                        DeliveryMethod = SmtpDeliveryMethod.Network,
                        UseDefaultCredentials = false,
                        Credentials = new NetworkCredential(senderEmail.Address, password)
                    };
                    using (var mess = new MailMessage(senderEmail, receiverEmail)
                    {
                        Subject = subject,
                        Body = body
                    })
                    {
                        smtp.Send(mess); //smtp ile mesaj birleşir.

                    }
                    return View();
                }

            }
            catch (Exception e)
            {
                ViewBag.Error = e.ToString();
            }

            return View();
        }
        [HttpPost]
        public JsonResult GetDistricts(int id)
        {
            GasStationEntities entities = new GasStationEntities();

            var districts= entities.Ilceler.Where(s=>s.IlID==id).Select(s=>s);
            List<Ilceler> result = new List<Ilceler>();
            foreach(var dist in districts)
            {
                result.Add(new Ilceler { IlceID = dist.IlceID, Ilce = dist.Ilce });
            }

           


            return Json(result,JsonRequestBehavior.AllowGet);
        }

        public JsonResult GetPrices(int id , string brand)
        {
            GasStationEntities entities = new GasStationEntities();
            List<PriceViewModel> result = new List<PriceViewModel>();
            if (brand == "bp")
            {
                var prices = entities.BP.Where(s => s.IlceID == id).Select(s => s);
                foreach (var price in prices)
                {
                    result.Add(new PriceViewModel { benzin = price.benzin, motorin = price.motorin, otogaz = price.otogaz });
                }
              
            }
            else if (brand == "opet")
            {
                var prices = entities.Opet.Where(s => s.IlceID == id).Select(s => s);
                foreach (var price in prices)
                {
                    result.Add(new PriceViewModel { benzin = price.benzin, motorin = price.motorin, otogaz = "-" });
                }

                
            }
            else if (brand == "total")
            {
                var prices = entities.Total.Where(s => s.IlceID == id).Select(s => s);
                foreach (var price in prices)
                {
                    result.Add(new PriceViewModel { benzin = price.benzin.ToString(), motorin = price.motorin.ToString(), otogaz = price.otogaz.ToString() });
                }
                if(result==null)

                    return Json("Result Not Found");

            }
            else if (brand == "shell")
            {
                var prices = entities.Shell.Where(s => s.IlceID == id).Select(s => s);
                foreach (var price in prices)
                {
                    result.Add(new PriceViewModel { benzin = price.benzin, motorin = price.motorin, otogaz = price.otogaz });
                }

            }
            else if (brand == "turkpetrol")
            {
                var prices = entities.turkPetrol.Where(s => s.IlceID == id).Select(s => s);
                foreach (var price in prices)
                {
                    result.Add(new PriceViewModel { benzin = price.benzin, motorin = price.motorin, otogaz = price.otogaz });
                }

            }else
            return Json("Result Not Found");
                
            return Json(result, JsonRequestBehavior.AllowGet);
        }
    }
}