package com.sommerengineering.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity with superclass constructor
        super.onCreate(savedInstanceState);

        // layout has a single <fragment> tag
        setContentView(R.layout.activity_settings);
    }

    public static class MoviePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            // initialize fragment with superclass constructor
            super.onCreate(savedInstanceState);

            // layout has a root PreferenceScreen with child ListPreference
            addPreferencesFromResource(R.xml.settings_main);

            // get preference key String
            String orderByKey = getString(R.string.settings_order_by_key);

            // get the preference Object associated with this key
            Preference orderBy = findPreference(orderByKey);

            // set listener on this preference and update it with the user choice
            bindPreferenceSummaryToValue(orderBy);

        }

        // set a listener on this app level preference and update
        private void bindPreferenceSummaryToValue(Preference preference) {

            // set listener on the preference
            preference.setOnPreferenceChangeListener(this);

            // get reference to the devices's shared preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // get the String key for the preference
            String preferenceKey = sharedPreferences.getString(preference.getKey(), "");

            // update preferences with the values chosen by the user
            onPreferenceChange(preference, preferenceKey);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            // preference value is of type String
            String stringValue = value.toString();

            // cast preference to the appropriate type
            ListPreference listPreference = (ListPreference) preference;

            // get index
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            // simple check that we are indeed in a valid list
            if (prefIndex >= 0) {

                // get String arrays defined in arrays.xml
                CharSequence[] labels = listPreference.getEntries();

                // setSummary updates the device's storage for app preferences
                preference.setSummary(labels[prefIndex]);
            }

            return true;

        }

    }

}
