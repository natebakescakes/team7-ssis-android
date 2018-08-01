package sg.edu.team7.stationeryshop.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sg.edu.team7.stationeryshop.R;


public class AddStockAdjustmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjustment_new_detail);

        //Initialize button
        Button submit = findViewById(R.id.add_btn);

        //Get texts
        EditText itemcode = (EditText)findViewById(R.id.add_itemcode);
        EditText itemname = (EditText) findViewById(R.id.add_itemname);
        EditText bef_qty = (EditText)findViewById(R.id.add_before);
        EditText aft_qty = (EditText)findViewById(R.id.add_after);
        EditText reason = (EditText) findViewById(R.id.add_reason);

        submit.setOnClickListener(new View.OnClickListener() {
            boolean flag;
            @Override
            public void onClick(View view) {

                flag = true;
                    if (itemcode.getText().toString().equals("")) flag = false;
                    if (itemname.getText().toString().equals("")) flag = false;
                    if (bef_qty.getText().toString().equals("")) flag = false;
                    if (aft_qty.getText().toString().equals("")) flag = false;
                    if (reason.getText().toString().equals("")) flag = false;



                if(flag == true) {

                    if(bef_qty.getText().toString().equals(aft_qty.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(),"Before and After Quantities cannot be the same",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), NewStockAdjustmentActivity.class);

                        intent.putExtra("itemcode", itemcode.getText().toString());
                        intent.putExtra("itemname", itemname.getText().toString());
                        intent.putExtra("bef_qty", bef_qty.getText().toString());
                        intent.putExtra("aft_qty", aft_qty.getText().toString());
                        intent.putExtra("reason", reason.getText().toString());

                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please fill in ALL details",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
