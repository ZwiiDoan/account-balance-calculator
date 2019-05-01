package per.duyd.interview.me.util;

import org.apache.commons.lang3.StringUtils;
import per.duyd.interview.me.model.Transaction;
import per.duyd.interview.me.model.TransactionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

public class TransactionMatcher {
    public static BigDecimal getRelativeAmount(Transaction transaction, String accountId) {
        return StringUtils.endsWithIgnoreCase(transaction.getFromAccountId(), accountId) ?
                transaction.getAmount().multiply(BigDecimal.valueOf(-1)) : transaction.getAmount();
    }

    public static boolean matchAccountId(Transaction transaction, String accountId) {
        return StringUtils.endsWithIgnoreCase(transaction.getFromAccountId(), accountId) ||
                StringUtils.endsWithIgnoreCase(transaction.getToAccountId(), accountId);
    }

    public static boolean matchReversalTransaction(Transaction transaction) {
        return StringUtils.isNotBlank(transaction.getRelatedTransaction()) &&
                StringUtils.endsWithIgnoreCase(TransactionType.REVERSAL.name(), transaction.getTransactionType());
    }

    public static boolean matchPaymentTransaction(Transaction transaction) {
        return StringUtils.isBlank(transaction.getRelatedTransaction()) &&
                StringUtils.endsWithIgnoreCase(TransactionType.PAYMENT.name(), transaction.getTransactionType());
    }

    public static boolean earlierThanOrEqualTo(Transaction transaction, Date to) {
        return transaction.getCreatedAt().compareTo(to) <= 0;
    }

    public static boolean laterThanOrEqualTo(Transaction transaction, Date from) {
        return transaction.getCreatedAt().compareTo(from) >= 0;
    }

    public static boolean isNotReversed(HashSet<String> reversalTransactionIds, Transaction transaction) {
        return !reversalTransactionIds.contains(transaction.getTransactionId());
    }
}
