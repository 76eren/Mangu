package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.*;
import com.example.mangareader.R;
import com.example.mangareader.Settings;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;


public class SettingsActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        overridePendingTransition(0,0);

        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.activity = this;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment)
                    .commit();
        }

        // LOL THIS CODE IS FUCKED
        // PRESSING THE TOGGLE BUTTON DOESNT WORK
        // BUT SWIPING FROM LEFT TO RIGHT DOES
        // IDK WHAT IS HAPPENING BUT THEN AGAIN WHEN DO I KNOW WHAT IS HAPPENING
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Navigation navigation = new Navigation();
        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.ItemClickSetup(this, menu);

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        Activity activity;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreference preference_ServerMangakakalot = getPreferenceScreen().findPreference("preference_ServerMangakakalot");
            SwitchPreference preference_cache = getPreferenceScreen().findPreference("preference_Cache");

            CheckBoxPreference mangakakalot = getPreferenceScreen().findPreference("preference_source_mangakakalot");
            CheckBoxPreference mangadex = getPreferenceScreen().findPreference("preference_source_mangadex");


            preference_ServerMangakakalot.setOnPreferenceChangeListener((preference, newValue) -> {
                if (preference_ServerMangakakalot.isChecked()){
                    preference_ServerMangakakalot.setChecked(false);
                }
                else  {
                    preference_ServerMangakakalot.setChecked(true);
                }
                return false;
            });



            preference_cache.setOnPreferenceChangeListener((preference, newValue) -> {
                if (preference_cache.isChecked()){
                    preference_cache.setChecked(false);
                }
                else  {
                    preference_cache.setChecked(true);
                }

                final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(activity);
                Boolean value= (mSharedPreference.getBoolean("preference_Cache", false));
                Log.d("lol", value.toString());


                return false;
            });

            // -------------- Sources -----------------
            // Surely there is a better way of doing this
            Settings settings = new Settings();
            mangakakalot.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(false);
                mangakakalot.setChecked(true);
                settings.AssignValueString(activity, "source", "mangakakalot");

                return false;
            });

            mangadex.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(true);
                mangakakalot.setChecked(false);
                settings.AssignValueString(activity, "source", "mangadex");


                return false;
            });


        }
    }

    // Its kinda important we do this
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}