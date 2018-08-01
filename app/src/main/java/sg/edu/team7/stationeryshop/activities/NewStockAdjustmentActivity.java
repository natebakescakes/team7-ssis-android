package sg.edu.team7.stationeryshop.activities;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequestDetail;
import sg.edu.team7.stationeryshop.util.JSONParser;
import sg.edu.team7.stationeryshop.util.RetrievalDetailAdapter;
import sg.edu.team7.stationeryshop.util.StockAdjustmentRequestDetailAdapter;

public class NewStockAdjustmentActivity extends AppCompatActivity {

    private StockAdjustmentRequestDetailAdapter saAdapter;
    private static int mRequestCode = 1;
    private static String userName;
    private static String message;


    //List of Stock Adjustment Details
    List<StockAdjustmentRequestDetail> saDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock_adjustment);


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        userName = getSharedPreferences(MainActivity.getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("email", "");



        //Initialize button
        Button add_item = findViewById(R.id.addItem_btn);
        Button submit = findViewById(R.id.submit_btn);

        //initialize recycler-view
        RecyclerView mRecyclerView = findViewById(R.id.sa_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        saAdapter = new StockAdjustmentRequestDetailAdapter(saDetails, this);
        mRecyclerView.setAdapter(saAdapter);

        TextView textView = findViewById(R.id.sa_date);
        textView.setText(currentDate);

        TextView requester = findViewById(R.id.clerk_name);
        requester.setText(userName);

        //set onClickListener for add item button
        add_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddStockAdjustmentDetailActivity.class);
                startActivityForResult(intent, mRequestCode);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saDetails.size()==0){
                    Toast.makeText(getApplicationContext(), "Please add Item to this Stock Adjustment Request",
                            Toast.LENGTH_SHORT).show();

                }
                else
                    {

                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                JSONArray jsonArray = new JSONArray();


                                try{
                                    for(StockAdjustmentRequestDetail r : saDetails)
                                    {
                                        JSONObject detail = new JSONObject();
                                        detail.put("ItemCode",r.get("itemCode"));
                                        detail. put("OriginalQuantity", r.get("originalQuantity"));
                                        detail.put("AfterQuantity", r.get("afterQuantity"));
                                        detail.put("Reason",r.get("reason"));
                                        detail.put("UserName",userName);

                                        jsonArray.put(detail);
                                    }

                                   message = JSONParser.postStream(
                                            MainActivity.getContext().getString(R.string.default_hostname) + "/api/stockadjustment/mobile/save",
                                            jsonArray.toString()
                                    );



                                    return message;
                                }
                                catch (Exception e){
                                    e.printStackTrace();

                                }

                                return "Unknown error";

                                }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }


                            }.execute();

                        Toast.makeText(getApplicationContext(),"Stock Adjustment Request Submitted ", Toast.LENGTH_LONG).show();
                        finish();

                    }

                }

        });


    }

    //what to do when receiving result from AddStockAdjustmentDetail Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK){
            String itemcode = data.getStringExtra("itemcode");
            String itemname = data.getStringExtra("itemname");
            String reason = data.getStringExtra("reason");
            String bef_qty = data.getStringExtra("bef_qty");
            String aft_qty = data.getStringExtra("aft_qty");

            saDetails.add(new StockAdjustmentRequestDetail(itemcode, itemname, bef_qty, aft_qty, reason));

        }

        saAdapter.update((saDetails));

    }

}

