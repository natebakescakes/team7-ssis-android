package sg.edu.team7.stationeryshop.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import sg.edu.team7.stationeryshop.R;

public class DepartmentUsageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_usage);

        // Set Title
        getSupportActionBar().setTitle("Department Usage Statistics");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize WebView
        WebView webView = findViewById(R.id.department_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String u) {
                v.loadUrl(u);
                return false;
            }
        });

        webView.loadUrl(getString(R.string.default_hostname) + "/Report/DepartmentUsageMobile");
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
