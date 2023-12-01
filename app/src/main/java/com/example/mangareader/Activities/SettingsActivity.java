package com.example.mangareader.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.example.mangareader.R;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.Settings.ExporterImporter.SharedprefsUpdater;
import com.example.mangareader.Sources.Mangadex;
import com.example.mangareader.Sources.Mangakakalot;
import com.example.mangareader.Sources.webtoons.Webtoons;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        overridePendingTransition(0, 0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment)
                    .commit();
        }

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
        navigation.itemClickSetup(this, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Its kinda important we do this
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreference preference_merge_manga_favourites = getPreferenceScreen()
                    .findPreference("preference_merge_manga_favourites");
            preference_merge_manga_favourites.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_merge_manga_favourites.setChecked(!preference_merge_manga_favourites.isChecked());
                return false;
            });

            SwitchPreference preference_ServerMangakakalot = getPreferenceScreen()
                    .findPreference("preference_ServerMangakakalot");
            preference_ServerMangakakalot.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_ServerMangakakalot.setChecked(!preference_ServerMangakakalot.isChecked());
                return false;
            });

            SwitchPreference preference_mangakakalot_showButon = getPreferenceScreen()
                    .findPreference("preference_mangakakalot_showButon");
            preference_mangakakalot_showButon.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_mangakakalot_showButon.setChecked(!preference_mangakakalot_showButon.isChecked());
                return false;
            });

            SwitchPreference preference_cache = getPreferenceScreen().findPreference("preference_Cache");
            preference_cache.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_cache.setChecked(!preference_cache.isChecked());

                return false;
            });

            // -------------- Sources -----------------
            Settings settings = new Settings();
            CheckBoxPreference mangakakalot = getPreferenceScreen().findPreference("preference_source_mangakakalot");
            CheckBoxPreference mangadex = getPreferenceScreen().findPreference("preference_source_mangadex");
            CheckBoxPreference webtoons = getPreferenceScreen().findPreference("preference_source_webtoons");

            // This makes sure the right preference is checked
            // I am aware that the SettingsActivity keeps track of this by default however we need to add this
            // Because the favouritesActivity may change these sources without notifying the SettingsActivity
            String src = settings.returnValueString(this.getActivity(), "source", "mangakakalot");
            switch (src) {

                case "mangadex":
                    mangadex.setChecked(true);
                    mangakakalot.setChecked(false);
                    webtoons.setChecked(false);
                    break;

                case "webtoons":
                    mangadex.setChecked(false);
                    mangakakalot.setChecked(false);
                    webtoons.setChecked(true);
                    break;

                default:
                    mangadex.setChecked(false);
                    mangakakalot.setChecked(true);
                    webtoons.setChecked(false);
                    break;
            }


            webtoons.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(false);
                mangakakalot.setChecked(false);
                webtoons.setChecked(true);
                SourceObjectHolder.changeSource(new Webtoons(), this.getActivity());

                return false;
            });

            mangakakalot.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(false);
                mangakakalot.setChecked(true);
                webtoons.setChecked(false);
                SourceObjectHolder.changeSource(new Mangakakalot(), this.getActivity());


                return false;
            });

            mangadex.setOnPreferenceClickListener(preference -> {
                mangadex.setChecked(true);
                mangakakalot.setChecked(false);
                webtoons.setChecked(false);
                SourceObjectHolder.changeSource(new Mangadex(), this.getActivity());

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

                settings.assignValueString(this.getActivity(), "theme", "default");

                return true;
            });

            darkTheme.setOnPreferenceClickListener(preference -> {
                defaultTheme.setChecked(false);
                lightTheme.setChecked(false);
                darkTheme.setChecked(true);

                settings.assignValueString(this.getActivity(), "theme", "dark");

                return true;
            });

            lightTheme.setOnPreferenceClickListener(preference -> {
                defaultTheme.setChecked(false);
                lightTheme.setChecked(true);
                darkTheme.setChecked(false);

                settings.assignValueString(this.getActivity(), "theme", "light");

                return true;

            });

            // ------------------------------- MANGADEX LANGUAGES
            // ---------------------------------
            SwitchPreference mangadex_preference_languages = getPreferenceScreen()
                    .findPreference("mangadex_preference_languages");
            mangadex_preference_languages.setOnPreferenceChangeListener((preference, newValue) -> {
                mangadex_preference_languages.setChecked(!mangadex_preference_languages.isChecked());

                return false;

            });

            // ----------------------------------------------- READ MODE
            // --------------------------------------
            CheckBoxPreference click = getPreferenceScreen().findPreference("preference_readmode_click");
            CheckBoxPreference scroll = getPreferenceScreen().findPreference("preference_readmode_scroll");
            click.setOnPreferenceClickListener(preference -> {
                scroll.setChecked(false);
                click.setChecked(true);
                settings.assignValueString(this.getActivity(), "read_mode", "click");
                return false;
            });

            scroll.setOnPreferenceClickListener(preference -> {
                scroll.setChecked(true);
                click.setChecked(false);
                settings.assignValueString(this.getActivity(), "read_mode", "scroll");

                return false;
            });

            SwitchPreference preference_hardware_acceleration = getPreferenceScreen()
                    .findPreference("preference_hardware_acceleration");
            preference_hardware_acceleration.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_hardware_acceleration.setChecked(!preference_hardware_acceleration.isChecked());
                return false;
            });

            // -----------------------------------------------------------------
            // FAVOURITES SORTING
            CheckBoxPreference alphabet = getPreferenceScreen().findPreference("preference_favourites_sort_alphabet");
            CheckBoxPreference preference_favourites_sort_date_down = getPreferenceScreen()
                    .findPreference("preference_favourites_sort_date_down");
            CheckBoxPreference preference_favourites_sort_date_up = getPreferenceScreen()
                    .findPreference("preference_favourites_sort_date_up");

            alphabet.setOnPreferenceClickListener(preference -> {
                preference_favourites_sort_date_down.setChecked(false);
                preference_favourites_sort_date_up.setChecked(false);
                alphabet.setChecked(true);

                settings.assignValueString(this.getActivity(), "preference_favourites_sort",
                        "preference_favourites_sort_alphabet");
                return false;
            });

            preference_favourites_sort_date_down.setOnPreferenceClickListener(preference -> {
                preference_favourites_sort_date_down.setChecked(true);
                preference_favourites_sort_date_up.setChecked(false);
                alphabet.setChecked(false);
                settings.assignValueString(this.getActivity(), "preference_favourites_sort",
                        "preference_favourites_sort_date_down");

                return false;
            });

            preference_favourites_sort_date_up.setOnPreferenceClickListener(preference -> {
                preference_favourites_sort_date_up.setChecked(true);
                alphabet.setChecked(false);
                preference_favourites_sort_date_down.setChecked(false);

                settings.assignValueString(this.getActivity(), "preference_favourites_sort",
                        "preference_favourites_sort_date_up");
                return false;
            });

            // ----------------------------------------------------------------------------------------
            // preference_image_size
            SwitchPreference preference_image_size = getPreferenceScreen()
                    .findPreference("preference_hardware_acceleration");
            preference_image_size.setOnPreferenceChangeListener((preference, newValue) -> {
                preference_image_size.setChecked(!preference_image_size.isChecked());
                return false;
            });



            // ----------------------------------------------------------------------------------------
            // Export/import saves
            SwitchPreference preference_export = getPreferenceScreen()
                    .findPreference("preference_export");
            SwitchPreference preference_import = getPreferenceScreen()
                    .findPreference("preference_import");

            SharedprefsUpdater sharedprefsUpdater = new SharedprefsUpdater(this.getActivity());
            preference_export.setOnPreferenceClickListener(preference -> {
                preference_export.setChecked(false);
                sharedprefsUpdater.exportSettings();
                return false;
            });

            preference_import.setOnPreferenceClickListener(preference -> {
                preference_import.setChecked(false);
                sharedprefsUpdater.importSettings();
                return false;
            });

        }
    }

}
