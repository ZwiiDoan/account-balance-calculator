# account-balance-calculator
The system will be initialised with an input file in CSV format containing a list of
transaction records.

Once initialised it should be able to print the relative account balance (positive or
negative) in a given time frame.
The relative account balance is the sum of funds that were transferred to / from an
account in a given time frame, it does not account for funds that were in that account
prior to the timeframe.

Another requirement is that, if a transaction has a reversing transaction, this
transaction should be omitted from the calculation, even if the reversing transaction is
outside the given time frame.

## How to build and run
#### Setup [Java](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html) to build and run the application
To build the application in Linux Bash or Windows PowerShell 
```
$ cd {your_path}/account-balance-calculator
$ ./gradlew clean build
```
To run the application
```
$ java -jar ./build/libs/account-balance-calculator-0.1.0.jar
```
You may have to give permission to gradlew by running 
```
$ chmod +x gradlew
```
The application will ask for a transaction csv file to initialize. Default file can be found in `src/main/resources/transactions.csv`

Entering the following input arguments:
```
accountId: ACC334455
from: 20/10/2018 12:00:00
to: 20/10/2018 19:00:00
```

The output should be:
```
Relative balance for the period is: -$25.00
Number of transactions included is: 1
```

## Design
* Core model entity of the app is Transaction.java which based on input csv. Note: BigDecimal is used for `amount` to ensure correctness of big number calculations.
* The application allows user to load different csv files on start-up, if user does not input any csv file, default one will be loaded from `src/main/resources/transactions.csv`
* Once the csv file is loaded, user can search for different accountId and date range
* CsvTransactionService.java executes the core logic of searching `accountId`, `from`, `to` by:
    1. Create a set of reserved transaction ids related to the `accountId` on or after `from` date.
    2. Get a list of transactions related to the `accountId` in the date range `from` and `to` that match below criteria:
        1. Transaction has not been reversed (Transaction id does not exist in the set of et of reserved transaction ids above)
        2. Transaction type is PAYMENT
    3. We some the amounts of the transaction list above to get account balance. Each amount is negative if `fromAccountId` = `accountId` and positive if `toAccountId` = `accountId`
    4. We also print out the size of the transaction list which is the number of transactions included.