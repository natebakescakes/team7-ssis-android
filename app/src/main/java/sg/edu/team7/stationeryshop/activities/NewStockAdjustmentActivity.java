package sg.edu.team7.stationeryshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequestDetail;
import sg.edu.team7.stationeryshop.util.StockAdjustmentRequestDetailAdapter;

public class NewStockAdjustmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock_adjustment);

        List<StockAdjustmentRequestDetail> saDetails = new ArrayList<>();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

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
        mRecyclerView.setAdapter(new StockAdjustmentRequestDetailAdapter(saDetails, this));

        TextView textView = findViewById(R.id.sa_date);
        textView.setText(currentDate);

        //set onClickListener
        add_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddStockAdjustmentDetailActivity.class);
                startActivity(intent);
            }
        });


    }

}
