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

public class Retrieval extends HashMap<String, Object> implements Serializable {

    public Retrieval(String retrievalId, String createdBy, String createdDate, String status, List<RetrievalDetailByDept> retrievalDetails) {
        put("retrievalId", retrievalId);
        put("createdBy", createdBy);
        put("createdDate", createdDate);
        put("status", status);
        put("retrievalDetails", retrievalDetails);
    }

    public static List<Retrieval> findAllRetrievals() throws JSONException {
        JSONArray data = JSONParser.getJSONArrayFromUrl(MainActivity.getContext().getString(R.string.default_hostname) + "/api/retrievals");

        List<Retrieval> retrievals = new ArrayList<>();

        if (data == null)
            return retrievals;

        // Loop through all JSON objects
        for (int i = 0; i < data.length(); i++) {
            JSONObject retrievalJson = data.getJSONObject(i);

            List<RetrievalDetailByDept> retrievalDetails = new ArrayList<>();

            for (int j = 0; j < retrievalJson.getJSONArray("RetrievalDetails").length(); j++) {
                JSONObject retrievalDetailJson = retrievalJson.getJSONArray("RetrievalDetails").getJSONObject(j);

                RetrievalDetailByDept retrievalDetail = new RetrievalDetailByDept(
                        retrievalDetailJson.getString("Department"),
                        retrievalDetailJson.getString("DepartmentCode"),
                        retrievalDetailJson.getString("ItemCode"),
                        retrievalDetailJson.getString("ItemName"),
                        retrievalDetailJson.getString("Bin"),
                        retrievalDetailJson.getString("Uom"),
                        retrievalDetailJson.getString("Status"),
                        retrievalDetailJson.getInt("PlanQuantity"),
                        retrievalDetailJson.getInt("ActualQuantity")
                );

                retrievalDetails.add(retrievalDetail);
            }

            Retrieval retrieval = new Retrieval(
                    retrievalJson.getString("RetrievalId"),
                    retrievalJson.getString("CreatedBy"),
                    retrievalJson.getString("CreatedDate"),
                    retrievalJson.getString("Status"),
                    retrievalDetails
            );

            retrievals.add(retrieval);
        }

        return retrievals;
    }
}
