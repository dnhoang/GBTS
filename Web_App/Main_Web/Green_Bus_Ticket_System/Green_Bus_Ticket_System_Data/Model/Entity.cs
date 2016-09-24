using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Model
{
    public interface IEntity<T>
    {
        T ID { get; set; }
    }
    public abstract class BaseEntity { }
    public abstract class Entity<T> : BaseEntity, IEntity<T>
    {
        public virtual T ID { get; set; }
    }
}
