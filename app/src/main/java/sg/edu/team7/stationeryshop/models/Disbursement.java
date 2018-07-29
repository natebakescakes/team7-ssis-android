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

public class Disbursement extends HashMap<String, Object> implements Serializable {

    public Disbursement(String disbursementId, String department, String collectionPoint, String createdDate, String status, List<DisbursementDetail> disbursementDetails) {
        put("disbursementId", disbursementId);
        put("department", department);
        put("collectionPoint", collectionPoint);
        put("createdDate", createdDate);
        put("status", status);
        put("disbursementDetails", disbursementDetails);
    }

    public static List<Disbursement> findAllDisbursements() throws JSONException {
        JSONArray data = JSONParser.getJSONArrayFromUrl(MainActivity.getContext().getString(R.string.default_hostname) + "/api/disbursement");
        List<Disbursement> disbursements = new ArrayList<>();

        if (data == null)
            return new ArrayList<>();

        // Loop through all JSON objects
        for (int i = 0; i < data.length(); i++) {
            JSONObject disbursementJson = data.getJSONObject(i);

            List<DisbursementDetail> disbursementDetails = new ArrayList<>();

            for (int j = 0; j < disbursementJson.getJSONArray("DisbursementDetails").length(); j++) {
                JSONObject disbursementDetailJson = disbursementJson.getJSONArray("DisbursementDetails").getJSONObject(i);

                DisbursementDetail disbursementDetail = new DisbursementDetail(
                        disbursementDetailJson.getString("ItemCode"),
                        disbursementDetailJson.getString("Description"),
                        disbursementDetailJson.getInt("Qty"),
                        disbursementDetailJson.getString("Uom")
                );

                disbursementDetails.add(disbursementDetail);
            }

            Disbursement disbursement = new Disbursement(
                    disbursementJson.getString("DisbursementId"),
                    disbursementJson.getString("Department"),
                    disbursementJson.getString("CollectionPoint"),
                    disbursementJson.getString("CreatedDate"),
                    disbursementJson.getString("Status"),
                    disbursementDetails
            );

            disbursements.add(disbursement);
        }

        return disbursements;
    }
}

