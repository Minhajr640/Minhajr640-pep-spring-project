package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.repository.AccountRepository;
import com.example.entity.Account;
import com.example.exception.InvalidInputException;
import com.example.exception.UsernameExistsException;

@Service
public class AccountService {

    private final  AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(Account account) throws InvalidInputException, UsernameExistsException{
        if(account.getUsername() == null || account.getPassword() == null || account.getPassword().length() < 4) {
            throw new InvalidInputException("Invalid Inputs. Try Again");
        } else if(accountRepository.findByUsername(account.getUsername()) != null) {
            throw new UsernameExistsException("Username Already Exists");
        } else {
            return accountRepository.save(account);
        }
    }

    public Account getAccount(Account account) throws InvalidInputException {
        Account existingAccount;
        if(accountRepository.existsByUsername(account.getUsername())) {
            existingAccount = accountRepository.findByUsername(account.getUsername());
            if(existingAccount.getUsername().equals(account.getUsername()) &&
            existingAccount.getPassword().equals(account.getPassword())) {
                return existingAccount;
            } else {
                throw new InvalidInputException("null");
            }
        } else {
            throw new InvalidInputException("null");
        }
    }    
}
