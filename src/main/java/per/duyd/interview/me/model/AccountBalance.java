package per.duyd.interview.me.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalance {
    private BigDecimal relativeBalance;

    private Integer includedTransactionCount;
}
