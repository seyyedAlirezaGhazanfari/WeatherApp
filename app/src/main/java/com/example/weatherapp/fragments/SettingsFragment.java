package com.example.weatherapp.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.weatherapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private String summaryText;
    private int icon;
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreference darkModeSwitch = (SwitchPreference) findPreference(getString(R.string.darkModeSwitchKey));
        assert darkModeSwitch != null;
        darkModeSwitch.setSummary(
                PreferenceManager.getDefaultSharedPreferences(
                        requireContext()
                ).getString(
                        getString(R.string.summaryID),
                        getString(R.string.summary)
                )
        );
        icon = PreferenceManager.getDefaultSharedPreferences(
                requireContext()
        ).getInt(
                getString(R.string.iconID),
                R.drawable.ic_light_mode
        );
        darkModeSwitch.setIcon(
                icon
        );
        summaryText = (String) darkModeSwitch.getSummary();
        darkModeSwitch.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    boolean isDark = (boolean) newValue;
                    String summary;
                    int iconId;
                    int mode;
                    if (isDark) {
                        summary = getString(R.string.darkSummary);
                        iconId = R.drawable.ic_dark_mode;
                        mode = AppCompatDelegate.MODE_NIGHT_YES;
                    } else {
                        summary = getString(R.string.summary);
                        iconId = R.drawable.ic_light_mode;
                        mode = AppCompatDelegate.MODE_NIGHT_NO;
                    }
                    preference.setIcon(iconId);
                    preference.setSummary(summary);
                    summaryText = (String) preference.getSummary();
                    icon = iconId;
                    AppCompatDelegate.setDefaultNightMode(mode);
                    return true;
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                requireContext()
        ).edit();
        editor.putString(getString(R.string.summaryID), summaryText);
        editor.putInt(getString(R.string.iconID), icon);
        editor.apply();
    }
}
