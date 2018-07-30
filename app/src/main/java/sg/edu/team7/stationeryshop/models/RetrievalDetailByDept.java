package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class RetrievalDetailByDept extends HashMap<String, Object> implements Serializable {

    public RetrievalDetailByDept(String department, String departmentCode, String itemCode, String itemName, String bin, String uom, String status, int planQuantity, int actualQuantity) {
        put("department", department);
        put("departmentCode", departmentCode);
        put("itemCode", itemCode);
        put("itemName", itemName);
        put("bin", bin);
        put("uom", uom);
        put("status", status);
        put("planQuantity", planQuantity);
        put("actualQuantity", actualQuantity);
    }
}
