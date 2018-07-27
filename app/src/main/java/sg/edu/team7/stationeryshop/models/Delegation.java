package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class Delegation extends HashMap<String, Object> implements Serializable {

    public Delegation(int delegationId, String recipient, String startDate, String endDate, String status) {
        put("delegationId", delegationId);
        put("recipient", recipient);
        put("startDate", startDate);
        put("endDate", endDate);
        put("status", status);
    }
}
