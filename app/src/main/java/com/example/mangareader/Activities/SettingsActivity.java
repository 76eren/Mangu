package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.*;
import com.example.mangareader.R;
import com.example.mangareader.Settings;
import com.example.mangareader.Sources.Mangadex;
import com.example.mangareader.Sources.Mangakakalot;
import com.example.mangareader.ValueHolders.ObjectHolder;
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
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Navigation navigation = new Navigation();
        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.ItemClickSetup(this, menu);



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

            SwitchPreference mangadex_preference_languages = getPreferenceScreen().findPreference("mangadex_preference_languages");

            SwitchPreference preference_mangakakalot_showButon = getPreferenceScreen().findPreference("preference_mangakakalot_showButon");

            preference_ServerMangakakalot.setOnPreferenceChangeListener((preference, newValue) -> {
                if (preference_ServerMangakakalot.isChecked()){
                    preference_ServerMangakakalot.setChecked(false);
                }
                else  {
                    preference_ServerMangakakalot.setChecked(true);
                }
                return false;
            });

            preference_mangakakalot_showButon.setOnPreferenceChangeListener((preference, newValue) -> {
                if (preference_mangakakalot_showButon.isChecked()){
                    preference_mangakakalot_showButon.setChecked(false);
                }
                else  {
                    preference_mangakakalot_showButon.setChecked(true);
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



                return false;
            });

            // -------------- Sources -----------------
            // Surely there is a better way of doing this
            Settings settings = new Settings();
            mangakakalot.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(false);
                mangakakalot.setChecked(true);
                settings.AssignValueString(activity, "source", "mangakakalot");


                ObjectHolder.sources = new Mangakakalot();

                return false;
            });

            mangadex.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(true);
                mangakakalot.setChecked(false);
                settings.AssignValueString(activity, "source", "mangadex");


                ObjectHolder.sources = new Mangadex();
                return false;
            });


            // ----------------------- THEME -------------------------
            CheckBoxPreference defaultTheme = getPreferenceScreen().findPreference("preference_theme_default");
            CheckBoxPreference darkTheme = getPreferenceScreen().findPreference("preference_theme_dark");
            CheckBoxPreference lightTheme = getPreferenceScreen().findPreference("preference_theme_light");


            defaultTheme.setOnPreferenceClickListener(preference -> {
                defaultTheme.setChecked(true);
                lightTheme.setChecked(false);
                darkTheme.setChecked(false);

                settings.AssignValueString(this.activity, "theme", "default");


                return true;
            });

            darkTheme.setOnPreferenceClickListener(preference -> {
                defaultTheme.setChecked(false);
                lightTheme.setChecked(false);
                darkTheme.setChecked(true);

                settings.AssignValueString(this.activity, "theme", "dark");


                return true;
            });

            lightTheme.setOnPreferenceClickListener(preference -> {
                defaultTheme.setChecked(false);
                lightTheme.setChecked(true);
                darkTheme.setChecked(false);

                settings.AssignValueString(this.activity, "theme", "light");

                return true;


            });


            // ------------------------------- MANGADEX LANGUAGES ---------------------------------
            mangadex_preference_languages.setOnPreferenceChangeListener((preference, newValue) -> {
                if (mangadex_preference_languages.isChecked()){
                    mangadex_preference_languages.setChecked(false);
                }
                else  {
                    mangadex_preference_languages.setChecked(true);
                }


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