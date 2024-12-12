package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.InvalidInputException;
import com.example.repository.MessageRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.example.repository.AccountRepository;
 

@Service
//@AllArgsConstructor
public class MessageService {

    //private final MessageRepository messageRepository;
    //private final AccountRepository accountRepository;

    private MessageRepository messageRepository;
    private AccountRepository accountRepository;

    @Autowired
    private MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }
    //you shouldn't need to autowire messagerepository if you use @allargscontrustor.

    //requirement asks to return message with generated id
    //will using .save(message) to return message instance return message with generated messageId?
    public Message saveMessage(Message message) throws InvalidInputException {
        //Account existingAccount = accountRepository.getById(message.getPostedBy());
        if(accountRepository.existsById(message.getPostedBy())
        && message.getMessageText().length() <255 
        && !message.getMessageText().isEmpty()) {
            return messageRepository.save(message);
        } else {
            throw new InvalidInputException("You Must Have Account to post Message. Message must be less than 255 characters.");
        }
    }
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(Integer messageId) {
        Optional<Message> foundMessageOps;
        Message foundMessage;
        if(!messageRepository.existsById(messageId)) {
            return null;
        }
        foundMessageOps = messageRepository.findById(messageId);
        foundMessage = foundMessageOps.get();
        return foundMessage;
    }

    //is my method of returning 1 for rows updated valid??
    //after deleteById should i use getMessageById to check if message is null;
    public Integer deleteMessageById(Integer messageId){
        if(messageRepository.existsById(messageId)) {
            return messageRepository.deleteByIdAndReturnCount(messageId);
        }
        return 0;
    }

    //?
    // public Integer updateMessageById(Integer messageId, String message_text) throws InvalidInputException {
    //     Message messageToUpdate;
    //     if(!messageRepository.existsById(messageId)) {
    //         messageToUpdate = messageRepository.getById(messageId);
    //         if(!message_text.trim().isEmpty() && message_text.length()< 255){
    //         messageToUpdate.setMessageText(message_text);
    //         messageRepository.save(messageToUpdate);
    //         return 1;
    //         }
    //         else {
    //             throw new InvalidInputException("MessageId must exist for update and message text can not be empty or over 255 characters");
    //         }
    //     } else {
    //         throw new InvalidInputException("MessageId must exist for update and message text can not be empty or over 255 characters");
    //     }
    // }

    public Integer updateMessageById(Integer messageId, String message_text) throws InvalidInputException {
        Message messageToUpdate;
        if(!messageRepository.existsById(messageId)) {
            throw new InvalidInputException("MessageId must exist for update.");
        } 
        if(message_text == null ||message_text.trim().isEmpty()){
            throw new InvalidInputException("Message text can not be empty or over 255 characters");
        } else if(message_text.length()> 255) {
            throw new InvalidInputException("Message text can not be empty or over 255 characters");
        }
        messageToUpdate = messageRepository.getById(messageId);
        messageToUpdate.setMessageText(message_text);
        messageRepository.save(messageToUpdate);
        return 1;
    }



    public List<Message> getMessagesByAccountId(Integer accountId) throws InvalidInputException {
        if(accountRepository.existsById(accountId)) {
            return messageRepository.getAllMessagesByAccountId(accountId);
        } else {
            throw new InvalidInputException("Account Does Not Exist.");
        }
    }
}

