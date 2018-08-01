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

public class StockAdjustmentRequest extends HashMap<String, Object> implements Serializable {

    public StockAdjustmentRequest(String stockAdjustmentId, String requestorName, String requestedDate, String remarks, String status, List<StockAdjustmentRequestDetail> stockAdjustmentRequestDetails) {
        put("stockAdjustmentId", stockAdjustmentId);
        put("requestorName", requestorName);
        put("requestedDate", requestedDate);
        put("remarks", remarks);
        put("status", status);
        put("stockAdjustmentRequestDetails", stockAdjustmentRequestDetails);
    }

    public static List<StockAdjustmentRequest> findAllStockAdjustments() throws JSONException {
        JSONObject emailJson = new JSONObject();
        emailJson.put("Email",
                MainActivity.getContext().getSharedPreferences(MainActivity.getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("email", ""));
        Log.i("EMAIL", emailJson.getString("Email"));
        String result = JSONParser.postStream(
                MainActivity.getContext().getString(R.string.default_hostname) + "/api/stockadjustment/supervisor",
                emailJson.toString()
        );

        JSONArray data = new JSONArray(result);

        List<StockAdjustmentRequest> stockAdjustments = new ArrayList<>();

        // Loop through all JSON objects
        for (int i = 0; i < data.length(); i++) {
            JSONObject stockAdjustmentJson = data.getJSONObject(i);

            List<StockAdjustmentRequestDetail> stockAdjustmentDetails = new ArrayList<>();

            for (int j = 0; j < stockAdjustmentJson.getJSONArray("StockAdjustmentRequestDetails").length(); j++) {
                JSONObject stockAdjustmentDetailJson = stockAdjustmentJson.getJSONArray("StockAdjustmentRequestDetails").getJSONObject(j);

                StockAdjustmentRequestDetail stockAdustmentDetail = new StockAdjustmentRequestDetail(
                        stockAdjustmentDetailJson.getString("ItemCode"),
                        stockAdjustmentDetailJson.getString("ItemName"),
                        stockAdjustmentDetailJson.getString("OriginalQuantity"),
                        stockAdjustmentDetailJson.getString("AfterQuantity"),
                        stockAdjustmentDetailJson.getString("Reason")
                );

                stockAdjustmentDetails.add(stockAdustmentDetail);
            }

            StockAdjustmentRequest stockAdjustment = new StockAdjustmentRequest(
                    stockAdjustmentJson.getString("StockAdjustmentId"),
                    stockAdjustmentJson.getString("RequestorName"),
                    stockAdjustmentJson.getString("RequestedDate"),
                    stockAdjustmentJson.getString("Remarks"),
                    stockAdjustmentJson.getString("Status"),
                    stockAdjustmentDetails
            );

            stockAdjustments.add(stockAdjustment);
        }

        return stockAdjustments;
    }
}
