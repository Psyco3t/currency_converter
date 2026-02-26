package com.example.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Requests requests = new Requests(this);
    SharedPreferences prefs;
    HashMap<String, Double> conversionHash;
    double currentRateFrom;
    double currentRateTo;
    double textNumber;
    int positionFrom;
    int positionTo;
    EditText editTextFrom;
    EditText editTextTo;
    Spinner convertFrom;
    Spinner convertTo;
    String apiKey = BuildConfig.exchangeRate_API_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        prefs = getPreferences(Context.MODE_PRIVATE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JSONObject rates = requests.AllRates(this);
        List<String> enrichedList = new ArrayList<>();
        try {
            enrichedList = enrichedNames();
        } catch (IOException e) {
            System.err.print(e.getClass());
        }
        try {
            conversionHash = getConversionHash(rates,enrichedList);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        convertFrom = (Spinner) findViewById(R.id.convertFrom);
        convertFrom.setOnItemSelectedListener(this);
        fillSpinner(convertFrom,enrichedList);

        convertTo = (Spinner) findViewById(R.id.convertTo);
        convertTo.setOnItemSelectedListener(this);
        fillSpinner(convertTo,enrichedList);

        editTextFrom = (EditText) findViewById(R.id.editTextNumberDecimal);
        editTextTo = (EditText) findViewById(R.id.editTextNumberDecimal2);


        editTextFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                setConversion();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    public void exitBtn(MenuItem item) throws JSONException {
        System.exit(0);
    }

    public void helpBtn(MenuItem item) {

        startActivity(new Intent(MainActivity.this,Pop.class));
    }

    public void refreshBtn(MenuItem item) throws IOException {
        requests.GetLatestRate(
                "https://v6.exchangerate-api.com/v6/"+apiKey+"/latest/EUR");
        requests.AllRates(this);
    }

    public void favoriteBtn(View view) {
        String textFrom = convertFrom.getSelectedItem().toString();
        String textTo = convertTo.getSelectedItem().toString();
        String title = textFrom+ "-" + textTo;
        Gson gson = new Gson();
        favoriteModel favorite1 = new favoriteModel(title,
                positionTo,positionFrom);
        String objectString = gson.toJson(favorite1);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("favoriteItem1",objectString);
        editor.apply();
    }
    public void useFavoriteBtn(View view) {
        String jsonString = prefs.getString("favoriteItem1","");
        try {
            JSONObject favoriteItem = new JSONObject(jsonString);
            convertFrom.setSelection(favoriteItem.getInt("positionFrom"));
            convertTo.setSelection(favoriteItem.getInt("positionTo"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchBtn(View view) {
        String storage = editTextFrom.getText().toString();

        editTextFrom.setText(editTextTo.getText().toString());
        editTextTo.setText(storage);

        convertFrom.setSelection(positionTo);
        convertTo.setSelection(positionFrom);
    }

    public double Conversion(double inputValue, double convertRateFrom, double convertRateTo)
    {
        System.out.println(inputValue % convertRateFrom * convertRateTo);
        return (inputValue / convertRateFrom) * convertRateTo;
    }

    public HashMap<String, Double> getConversionHash
            (JSONObject jObject, List<String> enrichedNames) throws JSONException {

        int itemNum = 0;
        Iterator<String> keys = jObject.keys();
        HashMap<String, Double> conversionHash = new HashMap<>();
        while(keys.hasNext())
        {
            String key = keys.next();
            Object value = jObject.get(key);
            double item = Double.parseDouble(value.toString());
            conversionHash.put(enrichedNames.get(itemNum), item);
            itemNum++;
        }
        return conversionHash;
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        if(parent.getId()==R.id.convertFrom)
        {
            Object selectedItem = parent.getItemAtPosition(pos);
            positionFrom = pos;
            String text = selectedItem.toString();
            currentRateFrom = conversionHash.get(text);
            setConversion();
        }else if(parent.getId() == R.id.convertTo)
        {
            Object selectedItem = parent.getItemAtPosition(pos);
            positionTo = pos;
            String text = selectedItem.toString();
            currentRateTo = conversionHash.get(text);
            setConversion();
        }

    }
    public void onNothingSelected(AdapterView<?> parent){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void setConversion()
    {
        try {
            String textContent = editTextFrom.getText().toString();
            textNumber = Double.parseDouble(textContent);
            //System.out.println("numbers are: " + textNumber);
            String converted = String.valueOf(Conversion(textNumber,
                    currentRateFrom,
                    currentRateTo));
            BigDecimal bd = new BigDecimal(String.valueOf(converted));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            editTextTo.setText(bd.toString());
        }catch(NumberFormatException e){
            e.printStackTrace();
            editTextTo.setText("");
        }
    }

    public void fillSpinner(Spinner spinner, List<String> nameList)
    {
        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(
                this,android.R.layout.simple_selectable_list_item,nameList
        );
        spinner.setAdapter(adapterFrom);
    }

    public List<String> enrichedNames() throws IOException {
        InputStream jsonStream = getAssets().open("names.json");
        InputStreamReader streamReader =
                new InputStreamReader(jsonStream, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        List<String> currencyNames = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(streamReader))
        {
            String line = reader.readLine();
            while (line != null)
            {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            String contents = stringBuilder.toString();
            JSONObject enrichedJson = new JSONObject(contents);
            JSONArray jsonArray = enrichedJson.getJSONArray("supported_codes");
            for(int item = 0; jsonArray.length()> item; item++)
            {
                JSONArray itemArray = jsonArray.getJSONArray(item);
                String arrayItem = itemArray.getString(1);
                //Log.d("item",arrayItem);
                currencyNames.add(arrayItem);
            }

        } catch (JSONException e) {
            System.err.print(e.getMessage());
        }
        return currencyNames;
    }

}