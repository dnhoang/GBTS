//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Green_Bus_Ticket_System_Data.Model
{
    using System;
    using System.Collections.Generic;
    
    public partial class PaymentTransaction
    {
        public int Id { get; set; }
        public string CardId { get; set; }
        public int CreditPlanId { get; set; }
        public string TransactionId { get; set; }
        public int Total { get; set; }
        public System.DateTime PaymentDate { get; set; }
    
        public virtual Card Card { get; set; }
        public virtual CreditPlan CreditPlan { get; set; }
    }
}
