﻿using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Repositories;
using Green_Bus_Ticket_System_Data.UnitOfWork;
using Green_Bus_Ticket_System_Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Services
{
    public interface ITicketTypeService : IEntityService<TicketType>
    {
        TicketType GetTicketType(int id);
        int GetMinPrice();
    }

    public class TicketTypeService : EntityService<TicketType>, ITicketTypeService
    {
        IUnitOfWork _unitOfWork;
        ITicketTypeRepository _repository;

        public TicketTypeService(IUnitOfWork unitOfWork, ITicketTypeRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public TicketType GetTicketType(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }

        public int GetMinPrice()
        {
            return _repository.FindBy(c => c.Status == (int)StatusReference.TicketTypeStatus.ACTIVATED).Min(c => c.Price);
        }
    }
}
