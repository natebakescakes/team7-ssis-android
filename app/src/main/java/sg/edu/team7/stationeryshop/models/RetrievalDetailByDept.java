package sg.edu.team7.stationeryshop.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.MainActivity;
import sg.edu.team7.stationeryshop.util.JSONParser;

public class RetrievalDetailByDept extends HashMap<String, Object> implements Serializable {

    public RetrievalDetailByDept(String department, String departmentCode, String itemCode, String itemName, String bin, String uom, String status, String retrievalStatus, int planQuantity, int actualQuantity) {
        put("department", department);
        put("departmentCode", departmentCode);
        put("itemCode", itemCode);
        put("itemName", itemName);
        put("bin", bin);
        put("uom", uom);
        put("status", status);
        put("retrievalStatus", retrievalStatus);
        put("planQuantity", planQuantity);
        put("actualQuantity", actualQuantity);
    }

    public static List<RetrievalDetailByDept> findRetrieval(String retrievalId) throws JSONException {
        JSONObject retrievalIdJson = new JSONObject();
        retrievalIdJson.put("RetrievalId", retrievalId);

        String result = JSONParser.postStream(
                MainActivity.getContext().getString(R.string.default_hostname) + "/api/retrieval/individual",
                retrievalIdJson.toString()
        );

        JSONArray data = new JSONArray(result);

        List<RetrievalDetailByDept> retrievalDetailByDepts = new ArrayList<>();

        // Loop through all JSON objects
        for (int i = 0; i < data.length(); i++) {
            JSONObject retrievalDetailByDeptJson = data.getJSONObject(i);

            RetrievalDetailByDept retrievalDetailByDept = new RetrievalDetailByDept(
                    retrievalDetailByDeptJson.getString("Department"),
                    retrievalDetailByDeptJson.getString("DepartmentCode"),
                    retrievalDetailByDeptJson.getString("ItemCode"),
                    retrievalDetailByDeptJson.getString("ItemName"),
                    retrievalDetailByDeptJson.getString("Bin"),
                    retrievalDetailByDeptJson.getString("Uom"),
                    retrievalDetailByDeptJson.getString("Status"),
                    retrievalDetailByDeptJson.getString("RetrievalStatus"),
                    retrievalDetailByDeptJson.getInt("PlanQuantity"),
                    retrievalDetailByDeptJson.getInt("ActualQuantity")
            );

            retrievalDetailByDepts.add(retrievalDetailByDept);
        }

        return retrievalDetailByDepts;
    }
}
