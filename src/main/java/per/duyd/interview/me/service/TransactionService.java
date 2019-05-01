package per.duyd.interview.me.service;

import per.duyd.interview.me.exception.LoadTransactionException;
import per.duyd.interview.me.model.AccountBalance;

import java.util.Date;

public interface TransactionService {

    void loadTransactions(String transactionFile) throws LoadTransactionException;

    AccountBalance getRelativeAccountBalance(String accountId, Date from, Date to) throws LoadTransactionException;
}
