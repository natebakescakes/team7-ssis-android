package sg.edu.team7.stationeryshop.models;

import android.content.Context;
import android.util.Log;

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

public class Requisition extends HashMap<String, Object> implements Serializable {

    public Requisition(String requisitionId, String requestorName, String requestedDate, String status, List<RequisitionDetail> requisitionDetails) {
        put("requisitionId", requisitionId);
        put("requestorName", requestorName);
        put("requestedDate", requestedDate);
        put("status", status);
        put("requisitionDetails", requisitionDetails);
    }

    public static List<Requisition> findAllRequisitions() throws JSONException {
        JSONObject emailJson = new JSONObject();
        emailJson.put("Email",
                MainActivity.getContext().getSharedPreferences(MainActivity.getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("email", ""));
        Log.i("EMAIL", emailJson.getString("Email"));
        String result = JSONParser.postStream(
                MainActivity.getContext().getString(R.string.default_hostname) + "/api/requisition/department",
                emailJson.toString()
        );

        JSONArray data = new JSONArray(result);

        List<Requisition> requisitions = new ArrayList<>();

        // Loop through all JSON objects
        for (int i = 0; i < data.length(); i++) {
            JSONObject requisitionJson = data.getJSONObject(i);

            List<RequisitionDetail> requisitionDetails = new ArrayList<>();

            for (int j = 0; j < requisitionJson.getJSONArray("RequisitionDetails").length(); j++) {
                JSONObject requisitionDetailJson = requisitionJson.getJSONArray("RequisitionDetails").getJSONObject(i);

                RequisitionDetail requisitionDetail = new RequisitionDetail(
                        requisitionDetailJson.getString("ItemCode"),
                        requisitionDetailJson.getString("Description"),
                        requisitionDetailJson.getInt("Qty"),
                        requisitionDetailJson.getString("Uom")
                );

                requisitionDetails.add(requisitionDetail);
            }

            Requisition requisition = new Requisition(
                    requisitionJson.getString("RequisitionId"),
                    requisitionJson.getString("RequestorName"),
                    requisitionJson.getString("RequestedDate"),
                    requisitionJson.getString("Status"),
                    requisitionDetails
            );

            requisitions.add(requisition);
        }

        return requisitions;
    }
}

