package sg.edu.team7.stationeryshop.models;

import android.support.v4.view.MenuItemCompat;

import java.util.HashMap;
import java.io.Serializable;

public class StockAdjustment extends HashMap implements Serializable {
    public StockAdjustment(String ItemCode,String Reason, String UserName, Integer Quantity)
    {

            put("itemCode", ItemCode);
            put("reason", Reason);
            put("userName",UserName);
            put("adjustedQuantity", Quantity);

    }

}
