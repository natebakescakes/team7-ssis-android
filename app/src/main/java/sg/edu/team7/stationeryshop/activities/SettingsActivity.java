package sg.edu.team7.stationeryshop.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import sg.edu.team7.stationeryshop.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}