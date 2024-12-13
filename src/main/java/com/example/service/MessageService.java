package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import java.util.List;
import java.util.Optional;
import com.example.repository.AccountRepository;
 

@Service
public class MessageService {
    /**Fields*/
    private MessageRepository messageRepository;
    private AccountRepository accountRepository;

    /**All Args Constructor */
    @Autowired
    private MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * This is a service layer method to save a message in database.
     * @param message
     * @return An instance of a message that has been saved to database.
     */
    public Message saveMessage(Message message){
        return messageRepository.save(message);   
    }
    
    /**
     * This is a service layer method to retrieve all messages.
     * @return A list of all messages.
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * This is a service layer method to retrieve a message by Id.
     * @param messageId
     * @return An instance of the method if found.
     */
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

    /**
     * This is a service layer method to delete a message from database by Id.
     * @param messageId
     * @return An integer value of 1 if message was deleted indicating a row was updated or 0 if message not found.
     */
    public Integer deleteMessageById(Integer messageId){
        if(messageRepository.existsById(messageId)) {
            return messageRepository.deleteByIdAndReturnCount(messageId);
        }
        return 0;
    }

    /**
     * This is a service layer method to update a message by Id.
     * @param messageId
     * @param message_text
     * @return Integer value of 1 if message was successfully updated indicating a row was updated.
     */
    public Integer updateMessageById(Integer messageId, String message_text) {
        Message messageToUpdate;
        messageToUpdate = messageRepository.getById(messageId);
        messageToUpdate.setMessageText(message_text);
        messageRepository.save(messageToUpdate);
        return 1;
    }

    /**
     * This is a service layer method to get all messages of an account.
     * @param accountId
     * @return A list of messages from an account if provided accountId exists.
     */
    public List<Message> getMessagesByAccountId(Integer accountId){
        if(accountRepository.existsById(accountId)) {
            return messageRepository.getAllMessagesByAccountId(accountId);
        } else {
            return null;
        }
    }

   /**
    * This is a service layer method to check if a messageId exists in database.
     * @param messageid
     * @return Boolean true if messageId exists, otherwise will return false.
     */
     public Boolean existsById(Integer messageid) {
        return messageRepository.existsById(messageid);
    }
}

