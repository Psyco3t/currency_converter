package com.example.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.widget.Toast;

import com.example.currencyconverter.Requests;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreen extends AppCompatActivity {
    String apiKey = BuildConfig.exchangeRate_API_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Handler handler = new Handler();
        Requests requests = new Requests(this);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExecuteAsyncTask(requests,executorService);
            }
        },1000);
    }

    private void ExecuteAsyncTask(Requests requests, ExecutorService executorService)
    {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    requests.GetLatestRate(
                            "https://v6.exchangerate-api.com/v6/" +
                                    ""+apiKey +"/latest/EUR");
                } catch (IOException e) {
                    System.out.println("couldnt retrieve rates");
                    executorService.shutdown();
                }
                startActivity(new Intent(SplashScreen.this,
                        MainActivity.class));
            }
        });
    }
}