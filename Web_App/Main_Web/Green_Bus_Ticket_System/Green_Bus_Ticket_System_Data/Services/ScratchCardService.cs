using Green_Bus_Ticket_System_Data.Model;
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
    public interface IScratchCardService : IEntityService<ScratchCard>
    {
        ScratchCard GetScratchCard(int id);
        ScratchCard GetScratchCardByCode(string code);

    }

    public class ScratchCardService : EntityService<ScratchCard>, IScratchCardService
    {
        IUnitOfWork _unitOfWork;
        IScratchCardRepository _repository;

        public ScratchCardService(IUnitOfWork unitOfWork, IScratchCardRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public ScratchCard GetScratchCard(int id)
        {
            return _repository.FindBy(u => u.Id == id).FirstOrDefault();
        }

        public ScratchCard GetScratchCardByCode(string code)
        {
            return _repository.FindBy(u => u.Code.ToLower().Equals(code.ToLower())).FirstOrDefault();
        }
    }
}
