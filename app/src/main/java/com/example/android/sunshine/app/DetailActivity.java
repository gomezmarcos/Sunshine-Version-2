/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new PlaceholderFragment())
                    .commit();
        }

    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        ShareActionProvider mShareActionProvider;
        String mForcastString;
        int DETAIL_LOADER = 1;

        private static final String[] FORECAST_COLUMNS = {
                                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                                WeatherContract.WeatherEntry.COLUMN_DATE,
                                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                        };

                        // these constants correspond to the projection defined above, and must change if the
                        // projection changes
                        private static final int COL_WEATHER_ID = 0;
                private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
                private static final int COL_WEATHER_MAX_TEMP = 3;
                private static final int COL_WEATHER_MIN_TEMP = 4;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail, menu);
            //super.onCreateOptionsMenu(menu, inflater);
            MenuItem item = menu.findItem(R.id.share);
            //ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (mForcastString != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.share){
                Toast.makeText(getActivity(), "Sharear!!", Toast.LENGTH_SHORT).show();
            }


            return super.onOptionsItemSelected(item);
        }

        private Intent createShareForecastIntent() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mForcastString + " #compartiendo");
            return intent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER,null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent==null) return  null;

            return new CursorLoader(getActivity(),
                    intent.getData(),
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) { return; }
            boolean isMetric = Utility.isMetric(getActivity());

            String date = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
            String desc = data.getString(COL_WEATHER_DESC);
            String max = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String min = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            mForcastString = String.format("%s - %s - %s/%s", date, desc, max, min);

            TextView txt = (TextView) getView().findViewById(R.id.weatherDetail);
            txt.setText(mForcastString);

            if (mForcastString!=null)
                mShareActionProvider.setShareIntent(createShareForecastIntent());


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

