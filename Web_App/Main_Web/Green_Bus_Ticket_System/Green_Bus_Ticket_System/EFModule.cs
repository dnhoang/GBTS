using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Autofac;
using Green_Bus_Ticket_System_Data.UnitOfWork;

namespace Green_Bus_Ticket_System
{
    public class EFModule : Autofac.Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterModule(new RepositoryModule());
            builder.RegisterType(typeof(Green_Bus_Ticket_System_Data.Model.GreenBusEntities)).As(typeof(Green_Bus_Ticket_System_Data.Model.GreenBusEntities)).InstancePerLifetimeScope();
            builder.RegisterType(typeof(UnitOfWork)).As(typeof(IUnitOfWork)).InstancePerRequest();

        }

    }
}