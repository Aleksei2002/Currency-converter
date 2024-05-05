package com.example.directcurrencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.directcurrencyconverter.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    List<String> keysList;
    Spinner toCurrency;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toCurrency = findViewById(R.id.planets_spinner);
        final EditText edtEuroValue = findViewById(R.id.editText4);
        final Button btnConvert = findViewById(R.id.button);
        textView = findViewById(R.id.textView7);

        try {
            loadConvTypes();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtEuroValue.getText().toString().isEmpty()) {
                    String toCurr = toCurrency.getSelectedItem().toString();
                    double euroValue = Double.parseDouble(edtEuroValue.getText().toString());

                    Toast.makeText(MainActivity.this, "Please Wait..", Toast.LENGTH_SHORT).show();
                    try {
                        convertCurrency(toCurr, euroValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter a Value to Convert..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadConvTypes() throws IOException {
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_UEDvHr4b2xUXznVtkLxTM77ufoDzymGh8Li2lL9c";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.e("failure Response", Objects.requireNonNull(e.getMessage()));
                runOnUiThread(() -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                assert response.body() != null;
                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject obj = new JSONObject(responseData);
                        JSONObject rates = obj.getJSONObject("rates");

                        keysList = new ArrayList<>();
                        Iterator<String> keys = rates.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            keysList.add(key);
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, keysList);
                        toCurrency.setAdapter(spinnerArrayAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void convertCurrency(final String toCurr, final double euroValue) throws IOException {
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_UEDvHr4b2xUXznVtkLxTM77ufoDzymGh8Li2lL9c";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.e("failure Response", Objects.requireNonNull(e.getMessage()));
                runOnUiThread(() -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject obj = new JSONObject(responseData);
                        JSONObject rates = obj.getJSONObject("rates");

                        String val = rates.getString(toCurr);
                        double output = euroValue * Double.parseDouble(val);

                        textView.setText(String.valueOf(output));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
