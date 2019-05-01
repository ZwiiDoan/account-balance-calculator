package per.duyd.interview.me.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import per.duyd.interview.me.model.Transaction;
import per.duyd.interview.me.model.TransactionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TransactionMatcherTest {

    @Test
    public void getRelativeAmount_shouldGetCorrectPaymentAmount() {
        String accountId = "test-accountId";
        Transaction transaction = Transaction.builder().fromAccountId(accountId).amount(BigDecimal.valueOf(1000)).build();
        assertEquals((BigDecimal.valueOf(-1000)), TransactionMatcher.getRelativeAmount(transaction, accountId));

        transaction = Transaction.builder().toAccountId(accountId).amount(BigDecimal.valueOf(1000)).build();
        assertEquals((BigDecimal.valueOf(1000)), TransactionMatcher.getRelativeAmount(transaction, accountId));
    }

    @Test
    public void matchAccountId_shouldMatchEitherFromAndToAccountId() {
        String accountId = "test-accountId";
        assertTrue(TransactionMatcher.matchAccountId(Transaction.builder().fromAccountId(accountId).build(), accountId));
        assertTrue(TransactionMatcher.matchAccountId(Transaction.builder().toAccountId(accountId).build(), accountId));
    }

    @Test
    public void matchAccountId_shouldNotMatchWrongAccountId() {
        String accountId = "test-accountId";
        assertFalse(TransactionMatcher.matchAccountId(Transaction.builder().fromAccountId(accountId).build(), "wrong-accountId"));
        assertFalse(TransactionMatcher.matchAccountId(Transaction.builder().toAccountId(accountId).build(), "wrong-accountId"));
    }

    @Test
    public void matchReversalTransaction_shouldDetectReversalTransactionsOnly() {
        assertTrue(TransactionMatcher.matchReversalTransaction(
                Transaction.builder()
                        .transactionType(TransactionType.REVERSAL.name())
                        .relatedTransaction("some-relatedTransaction")
                        .build()
        ));

        assertFalse(TransactionMatcher.matchReversalTransaction(
                Transaction.builder()
                        .transactionType(TransactionType.PAYMENT.name())
                        .build()
        ));
    }

    @Test
    public void matchPaymentTransaction_shouldDetectPaymentTransactionsOnly() {
        assertFalse(TransactionMatcher.matchPaymentTransaction(
                Transaction.builder()
                        .transactionType(TransactionType.REVERSAL.name())
                        .relatedTransaction("some-relatedTransaction")
                        .build()
        ));

        assertTrue(TransactionMatcher.matchPaymentTransaction(
                Transaction.builder()
                        .transactionType(TransactionType.PAYMENT.name())
                        .build()
        ));
    }

    @Test
    public void earlierThanOrEqualTo_shouldDetectEarlierOrEqualDatesOnly() {
        Date currentDate = new Date();
        assertTrue(TransactionMatcher.earlierThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), currentDate));
        assertTrue(TransactionMatcher.earlierThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), DateUtils.addDays(currentDate, 1)));
        assertFalse(TransactionMatcher.earlierThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), DateUtils.addDays(currentDate, -1)));
    }

    @Test
    public void laterThanOrEqualTo_shouldDetectLaterOrEqualDatesOnly() {
        Date currentDate = new Date();
        assertTrue(TransactionMatcher.laterThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), currentDate));
        assertTrue(TransactionMatcher.laterThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), DateUtils.addDays(currentDate, -1)));
        assertFalse(TransactionMatcher.laterThanOrEqualTo(Transaction.builder().createdAt(currentDate).build(), DateUtils.addDays(currentDate, 1)));
    }

    @Test
    public void isNotReversed_shouldDetectCorrectReversedTransactionsOnly() {
        HashSet<String> reversalTransactionIds = new HashSet<>();
        reversalTransactionIds.add("transactionId-1");
        reversalTransactionIds.add("transactionId-2");
        reversalTransactionIds.add("transactionId-3");

        assertTrue(TransactionMatcher.isNotReversed(reversalTransactionIds, Transaction.builder().transactionId("transactionId-11").build()));
        assertFalse(TransactionMatcher.isNotReversed(reversalTransactionIds, Transaction.builder().transactionId("transactionId-1").build()));
    }
}