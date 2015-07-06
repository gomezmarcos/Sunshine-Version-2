package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    View rootView;

    public void updateAdapter(String args) {
        Gson gson = new Gson();
        JSONArray jsonArray;
        List<String> result = new ArrayList<>();
        try {
            JSONArray list = new JSONObject(args).getJSONArray("list");
            for (int i = 0; i < list.length(); i++){
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.add(GregorianCalendar.DATE, i);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                String date = new SimpleDateFormat("EEE dd").format(gregorianCalendar.getTime());

                if (i == 0)
                    date = "Hoy";
                if (i == 1)
                    date = "Mañana";

                String day = list.getJSONObject(0).getJSONObject("temp").getString("day");
                String weather = list.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");

                result.add( String.format("%s, %s° y el clima %s.", date,day,weather));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        result);

        ListView listView= (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public ForecastFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<String> items = getForecastList();

        new FetchWeatherTask().execute("");

        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        items);

        //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView= (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<String> getForecastList() {
        List<String> items = new ArrayList<String>();
        items.add("Today - Sunny - 88/66");
        items.add("Tomorrow - Sunny - 88/66");
        items.add("Tomorrow - Sunny - 88/66");
        items.add("Tomorrow - Sunny - 88/66");
        items.add("Tomorrow - Sunny - 88/66");
        items.add("Tomorrow - Sunny - 88/66");
        items.add("Sunday - Sunny - 50/22");
        items.add("Monday - Rainy - 88/66");
        items.add("Tuesday - Sunny - 88/66");
        items.add("Sunday - Sunny - 50/22");
        items.add("Monday - Rainy - 88/66");
        items.add("Tuesday - Sunny - 88/66");
        items.add("Sunday - Sunny - 50/22");
        items.add("Monday - Rainy - 88/66");
        items.add("Tuesday - Sunny - 88/66");
        items.add("Sunday - Sunny - 50/22");
        items.add("Monday - Rainy - 88/66");
        items.add("Tuesday - Sunny - 88/66");
        items.add("Sunday - Sunny - 50/22");
        items.add("Monday - Rainy - 88/66");
        items.add("Tuesday - Sunny - 88/66");
        return items;
    }

    private class FetchWeatherTask extends AsyncTask<String,Void, String> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            updateAdapter(s);
            Log.e(LOG_TAG,s);
        }
    }
}
