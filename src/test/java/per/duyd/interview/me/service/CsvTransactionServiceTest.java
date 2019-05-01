package per.duyd.interview.me.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import per.duyd.interview.me.exception.LoadTransactionException;
import per.duyd.interview.me.model.AccountBalance;
import per.duyd.interview.me.model.Transaction;
import per.duyd.interview.me.model.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class CsvTransactionServiceTest {
    private CsvTransactionService csvTransactionService = new CsvTransactionService();

    @Test
    public void loadTransactions_shouldLoadFileSuccessfully() throws LoadTransactionException, ParseException {
        csvTransactionService.loadTransactions("src/test/resources/transactions.csv");
        assertEquals(5, csvTransactionService.getTransactionList().size());

        assertEquals(
                Transaction.builder()
                        .transactionId("TX10001").fromAccountId("ACC334455").toAccountId("ACC778899")
                        .createdAt(DateUtils.parseDate("20/10/2018 12:47:55", "dd/MM/yyyy HH:mm:ss"))
                        .amount(BigDecimal.valueOf(25.00).setScale(2, RoundingMode.CEILING))
                        .transactionType(TransactionType.PAYMENT.name())
                        .relatedTransaction(StringUtils.EMPTY)
                        .build(),
                csvTransactionService.getTransactionList().get(0)
        );

        assertEquals(
                Transaction.builder()
                        .transactionId("TX10002").fromAccountId("ACC334455").toAccountId("ACC998877")
                        .createdAt(DateUtils.parseDate("20/10/2018 17:33:43", "dd/MM/yyyy HH:mm:ss"))
                        .amount(BigDecimal.valueOf(10.50).setScale(2, RoundingMode.CEILING))
                        .transactionType(TransactionType.PAYMENT.name())
                        .relatedTransaction(StringUtils.EMPTY)
                        .build(),
                csvTransactionService.getTransactionList().get(1)
        );

        assertEquals(
                Transaction.builder()
                        .transactionId("TX10003").fromAccountId("ACC998877").toAccountId("ACC778899")
                        .createdAt(DateUtils.parseDate("20/10/2018 18:00:00", "dd/MM/yyyy HH:mm:ss"))
                        .amount(BigDecimal.valueOf(5.00).setScale(2, RoundingMode.CEILING))
                        .transactionType(TransactionType.PAYMENT.name())
                        .relatedTransaction(StringUtils.EMPTY)
                        .build(),
                csvTransactionService.getTransactionList().get(2)
        );

        assertEquals(
                Transaction.builder()
                        .transactionId("TX10004").fromAccountId("ACC334455").toAccountId("ACC998877")
                        .createdAt(DateUtils.parseDate("20/10/2018 19:45:00", "dd/MM/yyyy HH:mm:ss"))
                        .amount(BigDecimal.valueOf(10.50).setScale(2, RoundingMode.CEILING))
                        .transactionType(TransactionType.REVERSAL.name())
                        .relatedTransaction("TX10002")
                        .build(),
                csvTransactionService.getTransactionList().get(3)
        );

        assertEquals(
                Transaction.builder()
                        .transactionId("TX10005").fromAccountId("ACC334455").toAccountId("ACC778899")
                        .createdAt(DateUtils.parseDate("21/10/2018 09:30:00", "dd/MM/yyyy HH:mm:ss"))
                        .amount(BigDecimal.valueOf(7.25).setScale(2, RoundingMode.CEILING))
                        .transactionType(TransactionType.PAYMENT.name())
                        .relatedTransaction(StringUtils.EMPTY)
                        .build(),
                csvTransactionService.getTransactionList().get(4)
        );
    }

    @Test(expected = LoadTransactionException.class)
    public void loadTransactions_shouldThrowExceptionWhenFileIsNotFound() throws LoadTransactionException {
        csvTransactionService.loadTransactions("someFile");
    }

    //Test cases for invalid input files are not required as mentioned in requirements

    @Test
    public void getRelativeAccountBalance_shouldGetCorrectRelativeAccountBalancesInSearchWindow() throws LoadTransactionException, ParseException {
        csvTransactionService.loadTransactions("src/test/resources/transactions.csv");

        assertEquals(
                AccountBalance.builder().includedTransactionCount(2)
                        .relativeBalance(BigDecimal.valueOf(12.25).setScale(2, RoundingMode.CEILING)).build(),
                csvTransactionService.getRelativeAccountBalance(
                        "ACC778899",
                        DateUtils.parseDate("20/10/2018 18:00:00", "dd/MM/yyyy HH:mm:ss"),
                        DateUtils.parseDate("21/10/2018 09:30:00", "dd/MM/yyyy HH:mm:ss"))
        );

        assertEquals(
                AccountBalance.builder().includedTransactionCount(3)
                        .relativeBalance(BigDecimal.valueOf(37.25).setScale(2, RoundingMode.CEILING)).build(),
                csvTransactionService.getRelativeAccountBalance(
                        "ACC778899",
                        DateUtils.parseDate("20/10/2018 12:47:55", "dd/MM/yyyy HH:mm:ss"),
                        DateUtils.parseDate("21/10/2018 09:30:00", "dd/MM/yyyy HH:mm:ss"))
        );

        assertEquals(
                AccountBalance.builder().includedTransactionCount(1)
                        .relativeBalance(BigDecimal.valueOf(-25.00).setScale(2, RoundingMode.CEILING)).build(),
                csvTransactionService.getRelativeAccountBalance(
                        "ACC334455",
                        DateUtils.parseDate("20/10/2018 12:47:55", "dd/MM/yyyy HH:mm:ss"),
                        DateUtils.parseDate("20/10/2018 17:33:42", "dd/MM/yyyy HH:mm:ss"))
        );
    }

    @Test
    public void getRelativeAccountBalance_shouldOmitReversedTransactionsRegardlessOfSearchWindow() throws ParseException, LoadTransactionException {
        csvTransactionService.loadTransactions("src/test/resources/transactions.csv");

        assertEquals(
                AccountBalance.builder().includedTransactionCount(1)
                        .relativeBalance(BigDecimal.valueOf(-25.00).setScale(2, RoundingMode.CEILING)).build(),
                csvTransactionService.getRelativeAccountBalance(
                        "ACC334455",
                        DateUtils.parseDate("20/10/2018 12:47:55", "dd/MM/yyyy HH:mm:ss"),
                        DateUtils.parseDate("20/10/2018 18:00:00", "dd/MM/yyyy HH:mm:ss"))
        );

        assertEquals(
                AccountBalance.builder().includedTransactionCount(1)
                        .relativeBalance(BigDecimal.valueOf(-5.00).setScale(2, RoundingMode.CEILING)).build(),
                csvTransactionService.getRelativeAccountBalance(
                        "ACC998877",
                        DateUtils.parseDate("20/10/2018 17:33:43", "dd/MM/yyyy HH:mm:ss"),
                        DateUtils.parseDate("20/10/2018 18:00:00", "dd/MM/yyyy HH:mm:ss"))
        );

        /**
         * TX10001,ACC334455,ACC778899,20/10/2018 12:47:55,25.00,PAYMENT,
         * TX10002,ACC334455,ACC998877,20/10/2018 17:33:43,10.50,PAYMENT,
         * TX10003,ACC998877,ACC778899,20/10/2018 18:00:00,5.00,PAYMENT,
         * TX10004,ACC334455,ACC998877,20/10/2018 19:45:00,10.50,REVERSAL,TX10002
         * TX10005,ACC334455,ACC778899,21/10/2018 09:30:00,7.25,PAYMENT,
         */
    }
}