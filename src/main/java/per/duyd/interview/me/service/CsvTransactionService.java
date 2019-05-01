package per.duyd.interview.me.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import per.duyd.interview.me.exception.LoadTransactionException;
import per.duyd.interview.me.model.AccountBalance;
import per.duyd.interview.me.model.Transaction;
import per.duyd.interview.me.util.LoggerHelper;

import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static per.duyd.interview.me.util.TransactionMatcher.*;

@Slf4j
@Getter
@Setter
public class CsvTransactionService implements TransactionService {

    private List<Transaction> transactionList;

    @Override
    public void loadTransactions(String transactionFile) throws LoadTransactionException {
        try (Reader reader = Files.newBufferedReader(Paths.get(transactionFile))) {
            CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder<Transaction>(reader)
                    .withType(Transaction.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            transactionList = csvToBean.parse();
        } catch (Exception ex) {
            throw new LoadTransactionException(LoggerHelper.logError(log, "Error when reading transaction file - " +
                    ex.getClass().getName() + " - " + ex.getMessage(), ex));
        }
    }

    @Override
    public AccountBalance getRelativeAccountBalance(String accountId, Date from, Date to) throws LoadTransactionException {
        if (CollectionUtils.isEmpty(transactionList)) {
            throw new LoadTransactionException(LoggerHelper.logError(log, "No Transactions have been loaded"));
        }

        HashSet<String> reversalTransactionIds = transactionList.stream()
                .filter(transaction -> matchAccountId(transaction, accountId) && matchReversalTransaction(transaction) &&
                        laterThanOrEqualTo(transaction, from))
                .map(Transaction::getRelatedTransaction)
                .collect(Collectors.toCollection(HashSet::new));

        List<BigDecimal> transactionAmounts = transactionList.stream()
                .filter(transaction -> matchAccountId(transaction, accountId) && matchPaymentTransaction(transaction) &&
                        laterThanOrEqualTo(transaction, from) && earlierThanOrEqualTo(transaction, to) &&
                        isNotReversed(reversalTransactionIds, transaction))
                .map(transaction -> getRelativeAmount(transaction, accountId))
                .collect(Collectors.toList());

        BigDecimal relativeBalance = transactionAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountBalance(relativeBalance, transactionAmounts.size());
    }

}
