package antifraud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString
@Data
@NoArgsConstructor
public class TxLimit {
    @Id
    String number;
    long maxAllowed = 200;
    long maxManual = 1500;

    public TxLimit(String number){
        this.number = number;
    }


    public void lowerMaxAllowed(long amount) {
        maxAllowed = decrease(maxAllowed, amount);
    }

    public void lowerBoth(long amount) {
        maxAllowed = decrease(maxAllowed, amount);
        maxManual = decrease(maxManual, amount);
    }


    private long decrease(long maxAllowed, long amount) {
        return (long) Math.ceil(0.8 * maxAllowed - 0.2 * amount);
    }
    private long increase(long maxAllowed, long amount) {
        return (long) Math.ceil(0.8 * maxAllowed + 0.2 * amount);
    }

    public void raiseMaxAllowed(long amount) {
        maxAllowed = increase(maxAllowed, amount);
    }

    public void lowerMaxManual(long amount) {
        maxManual = decrease(maxManual, amount);
    }

    public void raiseBoth(long amount) {
        maxAllowed = increase(maxAllowed, amount);
        maxManual = increase(maxManual, amount);
    }

    public void raiseMaxManual(long amount) {
        maxManual = increase(maxManual, amount);
    }
}
