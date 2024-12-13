package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.repository.AccountRepository;
import com.example.entity.Account;
import com.example.exception.InvalidInputException;
import com.example.exception.UsernameExistsException;

@Service
public class AccountService {

    /**Fields */
    private final  AccountRepository accountRepository;

    /**All Args Constructor */
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * This is a service layer method to save a new account into database.
     * @param account
     * @return An instance of an account that has been saved in the database.
     * @throws UsernameExistsException If username already exists in the database.
     */
    public Account saveAccount(Account account) throws UsernameExistsException{
        if(accountRepository.findByUsername(account.getUsername()) != null) {
            throw new UsernameExistsException("Username Already Exists");
        } else {
            return accountRepository.save(account);
        }
    }

    /**
     * This is a service layer method to retrieve an instance of an account provided only username
     * and password which is extracted from request body in controller layer.
     * @param account
     * @return An instance of an existing account.
     */
    public Account getAccount(Account account){
        Account existingAccount;
        existingAccount = accountRepository.findByUsername(account.getUsername());
        if(existingAccount != null) {
            return existingAccount;
        } else {
            return null;
        }
    }
    
    /**
    * This is a service layer method to check if an accountId exists in database.
     * @param messageid
     * @return Boolean true if accountId exists, otherwise will return false.
     */
    public Boolean existsById(Integer accountId) {
        return accountRepository.existsById(accountId);
    }
}
