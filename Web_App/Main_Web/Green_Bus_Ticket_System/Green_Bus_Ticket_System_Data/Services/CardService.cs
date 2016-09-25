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
    public interface ICardService : IEntityService<Card>
    {
        Card GetCard(string cardId);
        Card AddCard(string cardId, int balance);
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

        public Card AddCard(string cardId, int balance)
        {
            Card card = new Card();
            card.CardId = cardId;
            card.Balance = balance;
            card.Status = (int)StatusReference.CardStatus.NON_ACTIVATED;
            card.RegistrationDate = DateTime.Now;
            _repository.Add(card);

            return card;
        }
    }
}
