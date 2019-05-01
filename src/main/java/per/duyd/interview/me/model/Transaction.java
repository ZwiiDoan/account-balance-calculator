package per.duyd.interview.me.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @CsvBindByName(column = "transactionId", required = true)
    private String transactionId;

    @CsvBindByName(column = "fromAccountId", required = true)
    private String fromAccountId;

    @CsvBindByName(column = "toAccountId", required = true)
    private String toAccountId;

    @CsvBindByName(column = "createdAt", required = true)
    @CsvDate(value = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @CsvBindByName(column = "amount", required = true)
    private BigDecimal amount;

    @CsvBindByName(column = "transactionType", required = true)
    private String transactionType;

    @CsvBindByName(column = "relatedTransaction")
    private String relatedTransaction;
}
