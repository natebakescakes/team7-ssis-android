package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class DisbursementDetail extends HashMap<String, Object> implements Serializable {
    public DisbursementDetail(String itemCode, String itemName, int quantity) {
        put("itemCode", itemCode);
        put("itemName", itemName);
        put("quantity", quantity);
    }
}
