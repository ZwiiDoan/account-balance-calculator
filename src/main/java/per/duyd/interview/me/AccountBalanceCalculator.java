package per.duyd.interview.me;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import per.duyd.interview.me.exception.LoadTransactionException;
import per.duyd.interview.me.model.AccountBalance;
import per.duyd.interview.me.service.CsvTransactionService;
import per.duyd.interview.me.service.TransactionService;
import per.duyd.interview.me.util.LoggerHelper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

@Slf4j
public class AccountBalanceCalculator {

    private TransactionService transactionService;

    private static final String DEFAULT_TRANSACTION_FILE = "src/main/resources/transactions.csv";

    public AccountBalanceCalculator(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        String loop = null;


        try {
            System.out.println();
            System.out.println();
            System.out.println("======================================");
            System.out.println("===== Account Balance Calculator =====");
            System.out.println("======================================");
            System.out.println("Transaction file (Enter to use default '" + DEFAULT_TRANSACTION_FILE + "'):");
            String transactionFile = scanner.nextLine();
            transactionService.loadTransactions(StringUtils.isNotBlank(transactionFile) ? transactionFile : DEFAULT_TRANSACTION_FILE);


            do {
                try {
                    System.out.println("accountId:");
                    String accountId = scanner.nextLine();
                    if (StringUtils.isBlank(accountId)) {
                        log.error("accountId must not be empty");
                        continue;
                    }

                    System.out.println("from:");
                    Date from = DateUtils.parseDate(scanner.nextLine(), "dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss");

                    System.out.println("to:");
                    Date to = DateUtils.parseDate(scanner.nextLine(), "dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss");

                    AccountBalance accountBalance = transactionService.getRelativeAccountBalance(accountId, from, to);
                    System.out.println("Relative balance for the period is: " + formatRelativeAccountBalance(accountBalance.getRelativeBalance()));
                    System.out.println("Number of transactions included is: " + accountBalance.getIncludedTransactionCount());

                    System.out.println("Continue? (y/n):");
                    loop = scanner.nextLine();
                } catch (ParseException ex) {
                    LoggerHelper.logError(log, "Invalid date format. Expected: dd/MM/yyyy [HH:mm:ss] (i.e. '20/10/2018' or '20/10/2018 12:00:00')", ex);
                }
            } while (!"n".equalsIgnoreCase(loop));

        } catch (LoadTransactionException ex) {
            LoggerHelper.logError(log, "Invalid transaction file - " + ex.getMessage() + ". See sample file at '" + DEFAULT_TRANSACTION_FILE + "'", ex);
        }

    }

    private String formatRelativeAccountBalance(BigDecimal balance) {
        return (balance.compareTo(BigDecimal.valueOf(0)) < 0 ? "-" : "") + "$" + balance.abs();
    }

    public static void main(String[] args) {
        new AccountBalanceCalculator(new CsvTransactionService()).execute();
    }

}
