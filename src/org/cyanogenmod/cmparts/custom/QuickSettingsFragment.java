package org.cyanogenmod.cmparts.custom;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.cyanogenmod.cmparts.utils.Utils;
import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.widget.SeekBarPreferenceCham;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class QuickSettingsFragment extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "QuickSettings";

    private static final String BATTERY_TILE_STYLE = "battery_tile_style";
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String PREF_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String PREF_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String PREF_COLUMNS_PORTRAIT = "qs_columns_portrait";
    private static final String PREF_COLUMNS_LANDSCAPE = "qs_columns_landscape";

    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mRowsPortrait;
    private ListPreference mRowsLandscape;
    private ListPreference mQsColumnsPortrait;
    private ListPreference mQsColumnsLandscape;
    private ListPreference mBatteryTileStyle;
    private int mBatteryTileStyleValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.qs_layout);

        PreferenceScreen prefSet = getPreferenceScreen();
        Activity activity = getActivity();
        final ContentResolver resolver = getActivity().getContentResolver();

        int defaultValue;

        // QS tile animation
        mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimationStyle = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
        updateTileAnimationStyleSummary(tileAnimationStyle);
        updateAnimTileStyle(tileAnimationStyle);
        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_DURATION, 1500,
                UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        updateTileAnimationDurationSummary(tileAnimationDuration);
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        mTileAnimationInterpolator = (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
        int tileAnimationInterpolator = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_INTERPOLATOR, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
        updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        mRowsPortrait = (ListPreference) findPreference(PREF_ROWS_PORTRAIT);
        int rowsPortrait = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_ROWS_PORTRAIT, 3);
        mRowsPortrait.setValue(String.valueOf(rowsPortrait));
        mRowsPortrait.setSummary(mRowsPortrait.getEntry());
        mRowsPortrait.setOnPreferenceChangeListener(this);

        defaultValue = getResources().getInteger(com.android.internal.R.integer.config_qs_num_rows_landscape_default);
        mRowsLandscape = (ListPreference) findPreference(PREF_ROWS_LANDSCAPE);
        int rowsLandscape = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_ROWS_LANDSCAPE, defaultValue);
        mRowsLandscape.setValue(String.valueOf(rowsLandscape));
        mRowsLandscape.setSummary(mRowsLandscape.getEntry());
        mRowsLandscape.setOnPreferenceChangeListener(this);

        mQsColumnsPortrait = (ListPreference) findPreference(PREF_COLUMNS_PORTRAIT);
        int columnsQsPortrait = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_COLUMNS_PORTRAIT, 3);
        mQsColumnsPortrait.setValue(String.valueOf(columnsQsPortrait));
        mQsColumnsPortrait.setSummary(mQsColumnsPortrait.getEntry());
        mQsColumnsPortrait.setOnPreferenceChangeListener(this);

        mQsColumnsLandscape = (ListPreference) findPreference(PREF_COLUMNS_LANDSCAPE);
        int columnsQsLandscape = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_COLUMNS_LANDSCAPE, 3);
        mQsColumnsLandscape.setValue(String.valueOf(columnsQsLandscape));
        mQsColumnsLandscape.setSummary(mQsColumnsLandscape.getEntry());
        mQsColumnsLandscape.setOnPreferenceChangeListener(this);

        mBatteryTileStyle = (ListPreference) findPreference(BATTERY_TILE_STYLE);
        mBatteryTileStyleValue = Settings.Secure.getInt(resolver,
                Settings.Secure.BATTERY_TILE_STYLE, 0);
        mBatteryTileStyle.setValue(Integer.toString(mBatteryTileStyleValue));
        mBatteryTileStyle.setSummary(mBatteryTileStyle.getEntry());
        mBatteryTileStyle.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        int intValue;
        int index;
        if (preference == mTileAnimationStyle) {
            int tileAnimationStyle = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_STYLE,
                    tileAnimationStyle, UserHandle.USER_CURRENT);
            updateTileAnimationStyleSummary(tileAnimationStyle);
            updateAnimTileStyle(tileAnimationStyle);
            return true;
        } else if (preference == mTileAnimationDuration) {
            int tileAnimationDuration = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_DURATION,
                    tileAnimationDuration, UserHandle.USER_CURRENT);
            updateTileAnimationDurationSummary(tileAnimationDuration);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            int tileAnimationInterpolator = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_INTERPOLATOR,
                    tileAnimationInterpolator, UserHandle.USER_CURRENT);
            updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
            return true;
        } else if (preference == mRowsPortrait) {
            intValue = Integer.parseInt((String) newValue);
            index = mRowsPortrait.findIndexOfValue((String) newValue);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_PORTRAIT, intValue);
            preference.setSummary(mRowsPortrait.getEntries()[index]);
            return true;
        } else if (preference == mRowsLandscape) {
            intValue = Integer.parseInt((String) newValue);
            index = mRowsLandscape.findIndexOfValue((String) newValue);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_LANDSCAPE, intValue);
            preference.setSummary(mRowsLandscape.getEntries()[index]);
            return true;
        } else if (preference == mQsColumnsPortrait) {
            intValue = Integer.parseInt((String) newValue);
            index = mQsColumnsPortrait.findIndexOfValue((String) newValue);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_COLUMNS_PORTRAIT, intValue);
            preference.setSummary(mQsColumnsPortrait.getEntries()[index]);
            return true;
        } else if (preference == mQsColumnsLandscape) {
            intValue = Integer.parseInt((String) newValue);
            index = mQsColumnsLandscape.findIndexOfValue((String) newValue);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_COLUMNS_LANDSCAPE, intValue);
            preference.setSummary(mQsColumnsLandscape.getEntries()[index]);
            return true;
        }  else if (preference == mBatteryTileStyle) {
            mBatteryTileStyleValue = Integer.valueOf((String) newValue);
            index = mBatteryTileStyle.findIndexOfValue((String) newValue);
            mBatteryTileStyle.setSummary(
                    mBatteryTileStyle.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.BATTERY_TILE_STYLE, mBatteryTileStyleValue);
            return true;
        }
        return false;
    }

    private void updateTileAnimationStyleSummary(int tileAnimationStyle) {
        String prefix = (String) mTileAnimationStyle.getEntries()[mTileAnimationStyle.findIndexOfValue(String
                .valueOf(tileAnimationStyle))];
        mTileAnimationStyle.setSummary(getResources().getString(R.string.qs_set_animation_style, prefix));
    }

    private void updateTileAnimationDurationSummary(int tileAnimationDuration) {
        String prefix = (String) mTileAnimationDuration.getEntries()[mTileAnimationDuration.findIndexOfValue(String
                .valueOf(tileAnimationDuration))];
        mTileAnimationDuration.setSummary(getResources().getString(R.string.qs_set_animation_duration, prefix));
    }

    private void updateTileAnimationInterpolatorSummary(int tileAnimationInterpolator) {
        String prefix = (String) mTileAnimationInterpolator.getEntries()[mTileAnimationInterpolator.findIndexOfValue(String
                .valueOf(tileAnimationInterpolator))];
        mTileAnimationInterpolator.setSummary(getResources().getString(R.string.qs_set_animation_interpolator, prefix));
    }

    private void updateAnimTileStyle(int tileAnimationStyle) {
        if (mTileAnimationDuration != null) {
            if (tileAnimationStyle == 0) {
                mTileAnimationDuration.setSelectable(false);
                mTileAnimationInterpolator.setSelectable(false);
            } else {
                mTileAnimationDuration.setSelectable(true);
                mTileAnimationInterpolator.setSelectable(true);
            }
        }
    }
}
