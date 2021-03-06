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
    public interface ICardService : IEntityService<Card>
    {
        Card GetCardByUID(string uid);
        Card AddCard(string cardId, int balance);
        IEnumerable<Card> GetCardsByUser(int userId);
        bool IsCardExist(string cardId);
        int GetNewActivateCardNum(DateTime beginDate, DateTime endDate);
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

        public Card GetCardByUID(string uid)
        {
            return _repository.FindBy(obj => obj.UniqueIdentifier.Equals(uid)).FirstOrDefault();
        }

        public Card AddCard(string cardId, int balance)
        {
            Card card = new Card();
            card.UniqueIdentifier = cardId;
            card.Balance = balance;
            card.Status = (int)StatusReference.CardStatus.NON_ACTIVATED;
            card.RegistrationDate = DateTime.Now;
            _repository.Add(card);

            return card;
        }

        public IEnumerable<Card> GetCardsByUser(int userId)
        {
            return _repository.FindBy(c => c.User.UserId == userId).ToList();
        }

        public bool IsCardExist(string cardId)
        {
            return GetCardByUID(cardId) != null;
        }

        public int GetNewActivateCardNum(DateTime beginDate, DateTime endDate)
        {
            return _repository.FindBy(c =>
            c.Status != (int)StatusReference.CardStatus.NON_ACTIVATED
            && c.RegistrationDate >= beginDate
            && c.RegistrationDate <= endDate
            ).ToList().Count;
        }
    }
}
