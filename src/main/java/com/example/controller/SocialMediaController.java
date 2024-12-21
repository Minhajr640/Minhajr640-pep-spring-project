package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.InvalidInputException;
import com.example.exception.UsernameExistsException;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */


/** 
 * Exceptions are used throughout the controller layer instead of null return values since this is typically
 * a good way to handle unexpected events.
*/
@RestController
public class SocialMediaController {

    /**Fields */
    private final AccountService accountService;
    private final MessageService messageService;

    /**All Args Constructor */
    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * This is a handler method to process POST requests to "register" endpoint.
     * The UsernameExistsException is handled within the method instead of using @ExceptionHandler since this is 
     * the only method where this exception can occur and centralizing the exception would be redundant.
     * @param account
     * @return A JSON representation of a new account persisted to database if criterias are met along with 
     * HTTP status code 400.
     * Will return HTTP status code 409 if username already exists to avoid duplicate usernames.
     * @throws InvalidInputException If criterias for username and password are not met exception is thrown 
     * and HTTP status code 400 is returned.
     */
    @PostMapping("register")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) throws InvalidInputException {
        if(account.getUsername() == null || account.getPassword() == null || account.getPassword().length() < 4) {
            throw new InvalidInputException("Invalid Inputs.");
        } try {
            Account registeredAccount = accountService.saveAccount(account);
            return new ResponseEntity<>(registeredAccount, HttpStatus.OK);
        } catch (UsernameExistsException u) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * This is a handler method to process POST requests to "login".
     * @param account
     * @return A JSON representation of existing account along with HTTP status code 200 if credentials are validated. 
     * Otherwise, will return HTTP status code 401(UNAUTHORIZED).
     */
    @PostMapping("login")
    public ResponseEntity<Account> loginAccount(@RequestBody Account account) {
            Account matchedAccount = accountService.getAccount(account);
            if(matchedAccount == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if(matchedAccount.getUsername().equals(account.getUsername()) &&
            matchedAccount.getPassword().equals(account.getPassword())) {
                return new ResponseEntity<>(matchedAccount, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
    }

    /**
     * This is a handler method to process POST requests to "messages".
     * @param message
     * @return A JSON representation of an Message object that has been persisted to database and HTTP status code 200.
     * @throws InvalidInputException If user does not have account or messageText value does not meet requirements and 
     * HTTP status code 400 is returned.
     */
    @PostMapping("messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) throws InvalidInputException {
        if(accountService.existsById(message.getPostedBy())
        && message.getMessageText().length() <255
        && !message.getMessageText().isEmpty()) {
            Message createdMessage = messageService.saveMessage(message);
            return new ResponseEntity<>(createdMessage, HttpStatus.OK);
        } 
        throw new InvalidInputException("Account Required.");
    }

    /**
     * This is a handler method to process GET requests to "messages".
     * @return A JSON representation of all messages with HTTP status 200.
     */
    @GetMapping("messages")
    public ResponseEntity<List<Message>> getMessages() {
        List<Message> allMessages = messageService.getAllMessages();
        return new ResponseEntity<>(allMessages, HttpStatus.OK);
    }

    /**
     * This is a handler method to process GET request to "messages/{messageId}" provided messageId.
     * @param messageId
     * @return A JSON representation of a Message object and HTTP status 200 if message found or not found.
     */
    @GetMapping("messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Message foundMessage = messageService.getMessageById(messageId);
        if(foundMessage == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(foundMessage, HttpStatus.OK);
        }
    }

    /**
     * This is a handler method to process DELETE requests to "messages/{messageId}" provided messageId.
     * @param messageId
     * @return If messageId exists, will return Integer value of 1 indicating 1 row was updated 
     * and will return HTTP status 200 in both cases. 
     */
    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {
        Integer rowsUpdated = messageService.deleteMessageById(messageId);
        if(rowsUpdated == 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(rowsUpdated, HttpStatus.OK);
    }   
    
    /**
     * This is a handler method to process PATCH requests "messages/{messageId}" provided messageId and new messageText.
     * @param messageId
     * @param message
     * @return Integer value of 1 indicating a row was updated if operation is successful and HTTP status 200.
     * @throws InvalidInputException if messageId does not exist or messageText does not meet requirements. 
     * Will return HTTP status 400 if exception is thrown.
     */
    @PatchMapping("messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable Integer messageId, @RequestBody Message message) throws InvalidInputException{
        if(!messageService.existsById(messageId)) {
            throw new InvalidInputException("Message Id not found.");
        }
        if(message.getMessageText() == null || message.getMessageText().trim().isEmpty() || message.getMessageText().length() > 255) {
            throw new InvalidInputException("Message text does not meet requirements.");
        }
        Integer rowsUpdated = messageService.updateMessageById(messageId, message.getMessageText());
        if(rowsUpdated == 1) {
            return new ResponseEntity<>(rowsUpdated, HttpStatus.OK);
        } else {
            throw new InvalidInputException("");
        }
    }

    /**
     * This is a handler method to process GET requests to "accounts/{accountId}/messages" provided accountID.
     * @param accountId
     * @return A JSON representation of a list of messages from provided accountId if exists any and HTTP status 200 in either case.
     */
    @GetMapping("accounts/{accountId}/messages") 
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<Message> messagesOfAccount = messageService.getMessagesByAccountId(accountId);
        if(messagesOfAccount != null) {
            return new ResponseEntity<>(messagesOfAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    
    /**
     * This is a method to handle all instances of an InvalidInputException.
     * @param ex
     * @return HTTP status code 400 and a message.
     */
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidInput(InvalidInputException ex) {
        return ex.getMessage();
    }
}
