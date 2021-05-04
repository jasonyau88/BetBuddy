package classes;

import java.math.BigDecimal;

public class Bet {
    private long id;
    private long playerId;
    private String type;
    private BigDecimal amount;
    private String gameName;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final long playerId) {
        this.playerId = playerId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(final String gameName) {
        this.gameName = gameName;
    }
}
