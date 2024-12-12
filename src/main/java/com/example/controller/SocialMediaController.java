package com.example.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
@RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("register")
    public @ResponseBody ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            Account registeredAccount = accountService.saveAccount(account);
            return new ResponseEntity<>(registeredAccount, HttpStatus.OK);
        } catch (InvalidInputException i) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameExistsException u) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("login")
    public @ResponseBody ResponseEntity<Account> loginAccount(@RequestBody Account account) {
        try {
            Account matchedAccount = accountService.getAccount(account);
            return new ResponseEntity<>(matchedAccount, HttpStatus.OK);
        } catch(InvalidInputException i) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("messages")
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.saveMessage(message);
            return new ResponseEntity<>(createdMessage, HttpStatus.OK);
        } catch(InvalidInputException i) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("messages")
    public @ResponseBody ResponseEntity<List<Message>> getMessages() {
        List<Message> allMessages = messageService.getAllMessages();
        return new ResponseEntity<>(allMessages, HttpStatus.OK);
    }

    //localhost:8080/messages/{messageId}.
    @GetMapping("messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
            Message foundMessage = messageService.getMessageById(messageId);
            if(foundMessage == null) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(foundMessage, HttpStatus.OK);
            }
    }

    @DeleteMapping("messages/{messageId}")
    public @ResponseBody ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {
        Integer rowsUpdated = messageService.deleteMessageById(messageId);
        if(rowsUpdated == 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(rowsUpdated, HttpStatus.OK);
    }   
    






    @PatchMapping("messages/{messageId}")
    public @ResponseBody ResponseEntity<Integer> updateMessageById(@PathVariable Integer messageId, @RequestBody Message message) {
        try {
            Integer rowsUpdated = messageService.updateMessageById(messageId, message.getMessageText());
            if(rowsUpdated == 1) {
                return new ResponseEntity<>(rowsUpdated, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (InvalidInputException i) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("accounts/{accountId}/messages") 
    public @ResponseBody ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        try {
            List<Message> messagesOfAccount = messageService.getMessagesByAccountId(accountId); 
            return new ResponseEntity<>(messagesOfAccount, HttpStatus.OK);
        } catch (InvalidInputException i) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
