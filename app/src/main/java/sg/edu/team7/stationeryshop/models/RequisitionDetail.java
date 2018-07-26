package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class RequisitionDetail extends HashMap<String, Object> implements Serializable {
    public RequisitionDetail(String itemCode, String itemName, int quantity, String uom) {
        put("itemCode", itemCode);
        put("itemName", itemName);
        put("quantity", quantity);
        put("uom", uom);
    }
}
