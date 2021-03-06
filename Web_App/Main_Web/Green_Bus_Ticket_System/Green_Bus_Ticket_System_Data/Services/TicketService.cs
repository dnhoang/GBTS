﻿using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Repositories;
using Green_Bus_Ticket_System_Data.UnitOfWork;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Services
{
    public interface ITicketService : IEntityService<Ticket>
    {
        Ticket GetTicket(int id);
        List<Ticket> GetTicketByDateRange(int userId, DateTime beginDate, DateTime endDate);
        List<Ticket> GetSoldTicketByDateRange(DateTime beginDate, DateTime endDate);
        List<Ticket> GetTicketByDateRangeAndCard(string cardUID, DateTime beginDate, DateTime endDate);
        List<Ticket> GetLastTicketByCard(string id);
    }

    public class TicketService : EntityService<Ticket>, ITicketService
    {
        IUnitOfWork _unitOfWork;
        ITicketRepository _repository;

        public TicketService(IUnitOfWork unitOfWork, ITicketRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public Ticket GetTicket(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }

        public List<Ticket> GetTicketByDateRange(int userId, DateTime beginDate, DateTime endDate)
        {
            return _repository.FindBy(t => 
            t.IsNoCard == false
            && t.Card.UserId == userId 
            && t.BoughtDated >= beginDate 
            && t.BoughtDated <= endDate
            ).OrderByDescending(p => p.BoughtDated).ToList();
        }

        public List<Ticket> GetSoldTicketByDateRange(DateTime beginDate, DateTime endDate)
        {
            return _repository.FindBy(t =>
            t.BoughtDated >= beginDate
            && t.BoughtDated <= endDate
            ).OrderByDescending(p => p.BoughtDated).ToList();
        }

        public List<Ticket> GetTicketByDateRangeAndCard(string cardUID, DateTime beginDate, DateTime endDate)
        {
            return _repository.FindBy(t =>
            t.IsNoCard == false
            && t.Card.UniqueIdentifier.Equals(cardUID)
            && t.BoughtDated >= beginDate
            && t.BoughtDated <= endDate
            ).OrderByDescending(p => p.BoughtDated).ToList();
        }

        public List<Ticket> GetLastTicketByCard(string id)
        {
            return _repository.FindBy(t => id.Equals(t.CardId.Value.ToString()))
                .OrderByDescending(t => t.BoughtDated).Take(5).ToList();
        }
    }
}
