package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class StockAdjustmentRequestDetail extends HashMap<String, String> implements Serializable {

    public StockAdjustmentRequestDetail(String itemCode, String itemName, String originalQuantity, String afterQuantity, String reason) {
        put("itemCode", itemCode);
        put("itemName", itemName);
        put("originalQuantity", originalQuantity);
        put("afterQuantity", afterQuantity);
        put("reason", reason);

    }

    public StockAdjustmentRequestDetail(String itemCode, String itemName, String originalQuantity, String afterQuantity, String reason, String email) {
        put("ItemCode", itemCode);
        put("ItemName", itemName);
        put("OriginalQuantity", originalQuantity);
        put("AfterQuantity", afterQuantity);
        put("Reason", reason);
        put("UserName",email);

    }
}
