package antifraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Result {
    private String result;
    private String info;
    @JsonIgnore
    private List<String> infoList = new ArrayList<>();

    /**
     * will escalate result so call with lower severity first
     * @param type
     * @param info
     */
    public void set(Type type, String info){
        if (infoList.size() > 0 && !(Type.PROHIBITED.name().equals(result))) {
            //we have a better reason so remove the old one
            infoList.remove(infoList.size() - 1);
        }
        result = type.name();
        infoList.add(info);
    }
    public enum Type {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    public static final String AMOUNT = "amount";
    public static final String IP = "ip";
    public static final String CARD_NUMBER = "card-number";
    public static final String NONE = "none";
    public static final String IP_CORRELATION = "ip-correlation";
    public static final String REGION_CORRELATION = "region-correlation";

}
