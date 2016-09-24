using Green_Bus_Ticket_System_Data.Model;
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
    }
}
