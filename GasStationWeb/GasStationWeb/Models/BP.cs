//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace GasStationWeb.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class BP
    {
        public int IlceID { get; set; }
        public Nullable<System.DateTime> date { get; set; }
        public string benzin { get; set; }
        public string motorin { get; set; }
        public string ilce { get; set; }
        public string otogaz { get; set; }
    
        public virtual Ilceler Ilceler { get; set; }
    }
}