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
    public interface ICardService : IEntityService<Card>
    {
        Card GetCard(string cardId);
    }

    public class CardService : EntityService<Card>, ICardService
    {
        IUnitOfWork _unitOfWork;
        ICardRepository _repository;

        public CardService(IUnitOfWork unitOfWork, ICardRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public Card GetCard(string cardId)
        {
            return _repository.FindBy(obj => obj.CardId.Equals(cardId)).FirstOrDefault();
        }
    }
}
