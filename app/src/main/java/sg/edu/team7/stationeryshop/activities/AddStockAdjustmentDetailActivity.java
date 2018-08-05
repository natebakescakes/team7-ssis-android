package sg.edu.team7.stationeryshop.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.util.JSONParser;


public class AddStockAdjustmentDetailActivity extends AppCompatActivity {
    static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjustment_new_detail);

        // Set Title
        getSupportActionBar().setTitle("Add Item");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialize button
        Button submit = findViewById(R.id.add_btn);
        Button cancel = findViewById(R.id.cancel_butn);
        Button minus = findViewById(R.id.minus);
        Button plus = findViewById(R.id.plus);


        //Get texts
        TextInputEditText itemcode = findViewById(R.id.add_itemcode);
        TextInputEditText adj_qty = findViewById(R.id.adj_qty);
        TextInputEditText reason = findViewById(R.id.add_reason);
        adj_qty.setText(String.valueOf(counter));
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = Integer.parseInt(adj_qty.getText().toString());
                counter = counter + 1;
                adj_qty.setText(String.valueOf(counter));

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = Integer.parseInt(adj_qty.getText().toString());
                counter = counter - 1;
                adj_qty.setText(String.valueOf(counter));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            boolean flag;

            @Override
            public void onClick(View view) {

                flag = true;
                final String[] bef_qty = new String[1];
                if (itemcode.getText().toString().equals("")) flag = false;
                if (reason.getText().toString().equals("")) flag = false;


                if (flag == true) {

                    Intent intent = new Intent(getApplicationContext(), NewStockAdjustmentActivity.class);

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            JSONObject msg = new JSONObject();
                            String url = getApplicationContext().getString(R.string.default_hostname) + "/api/manage/quantity/" + itemcode.getText().toString();
                            Log.i("URL for Quantity", url);

                            try {

                                JSONObject message = JSONParser.getJSONFromUrl(url);

                                Log.i("API response", message.getString("Message"));

                                bef_qty[0] = message.getString("Message");

                                return message.getString("Message");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            return "Unknown error";
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                        }

                        @Override
                        protected void onPostExecute(String message) {
                            super.onPostExecute(message);
                            intent.putExtra("itemcode", itemcode.getText().toString());
                            intent.putExtra("itemname", ""); //this is intentional
                            intent.putExtra("bef_qty", message);
                            intent.putExtra("aft_qty", String.valueOf(Integer.parseInt(message) + Integer.parseInt(adj_qty.getText().toString())));
                            intent.putExtra("reason", reason.getText().toString());

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in ALL details", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return true;
        }
    }

}
