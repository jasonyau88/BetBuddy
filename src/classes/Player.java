package classes;

import java.math.BigDecimal;

public class Player {
    private long id;
    private BigDecimal amount;

    public Player(final long id, final BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
