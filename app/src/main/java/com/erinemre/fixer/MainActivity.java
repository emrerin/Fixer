package com.erinemre.fixer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;


/**
 *  Fixer is an open-source, simple, and lightweight API for current and historical foreign exchange (forex)
 *  rates published by the European Central Bank. The API updates rates daily around 4PM CET every working day.
 *  Historical rates go back to 1st January, 1999.

 *  I developed a mobile app using this API.

 *  Source : fixer.io
 *
 * @author : Emre Erin
 * @link : {https://github.com/emrerin}
 *
 * */

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText editText;
    Spinner currencies;
    int selectedPosValue;
    List<String> enrtySet;
    String[] keySet;
    String[] values;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)this.findViewById(R.id.textView);
        currencies = (Spinner)this.findViewById(R.id.spinner);
        editText = (EditText)this.findViewById(R.id.editText);
        values = this.getResources().getStringArray(R.array.currencies1);
    }

    /**
     * Rates are set for 32 European countries.
     * Quote against a different currency by setting
     * the base parameter in your request.
     *
     * "https://api.fixer.io/latest?base=XXX"
     *
     * @param : XXX -> Country Currency
     */
    public void getRates(View view) {

        DownloadData downloadData = new DownloadData();

        try {
            String e = "https://api.fixer.io/latest?base=";
            String chosenBase = editText.getText().toString();
            downloadData.execute(new String[]{e + chosenBase});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        //boolean visible = false;
        private DownloadData() {} // defaulft const. egal, ob es exist oder nicht ist ;)

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject e = new JSONObject(s);
                String base = e.getString("base");
                System.out.println("Base\'s value: " + base);

                String date = e.getString("date");
                System.out.println("Date\'s value: " + date);

                String rates = e.getString("rates");
                System.out.println("Rates\'s value: " + rates);

                JSONObject jsonObject1 = new JSONObject(rates);

                enrtySet = new ArrayList();
                keySet = new String[jsonObject1.length()];

                int i = 0;
                Iterator adapter = jsonObject1.keys();

                //for(String key = (String) adapter.next(); adapter.hasNext();)
                while(adapter.hasNext()) {
                    String key = (String)adapter.next();
                    keySet[i++] = key;
                    enrtySet.add(jsonObject1.getString(key));
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item ,
                        keySet);

                currencies.setAdapter(arrayAdapter);
                printEntrySet(enrtySet);

                //currencies.setVisibility(!(visible));

                currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedPosValue = Integer.parseInt(String.valueOf(currencies.getSelectedItemId()));
                        textView.setText(
                                "1 " +
                                editText.getText().toString() +
                                " = " +
                                enrtySet.get(selectedPosValue) +
                                " " +
                                parent.getSelectedItem() +
                                "\n" +
                                values[position]);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } catch (Exception var10) {
                var10.printStackTrace();
            }
        }

        public void printEntrySet(List<String> list) {
            Iterator iter = list.iterator();

            while(iter.hasNext()) {
                String s = (String)iter.next();
                System.out.println("Keys : " + s);
            }
        }

        protected String doInBackground(String... params) {
            String result = "";

            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream e = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(e);

                for(int data = inputStreamReader.read(); data > 0; data = inputStreamReader.read()) {
                    char character = (char)data;
                    result = result + character;
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}