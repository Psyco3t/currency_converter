package com.example.currencyconverter;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class Requests {

    OkHttpClient client = new OkHttpClient();
    private  final Context context;
    String responseBody;

    public Requests(Context context) {
        this.context = context;
    }

    public void GetLatestRate(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        CompletableFuture<String> finalResponse = new CompletableFuture<>();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                finalResponse.completeExceptionally(e);
                System.out.println("callFailed");
                if(context instanceof Activity) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Failed refresh",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    responseBody = response.body().string();
                    if(WriteRates(responseBody, context))
                    {
                        System.out.println("files written successfully");
                        if(context instanceof Activity) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Successful refresh",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                    else
                    {
                        System.out.println("unknown error");
                        Toast.makeText(context,"Refresh error",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.err.println("responseBody error" + response.code());
                    Toast.makeText(context,"Refresh error code " +
                            response.code(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean WriteRates(String contents, Context context)
    {
        try(FileOutputStream fos =
                    context.openFileOutput("names.json",Context.MODE_PRIVATE)){
            fos.write(contents.getBytes());
            return true;

        }catch (IOException e)
        {
            return false;
        }

    }

    public boolean InvokeWriteRates() //this is bad!
    {
        return WriteRates("mockRates", context);
    }

    public JSONObject AllRates(Context context)
    {
        JSONObject ratesContent=null;
        try {
            FileInputStream fis = context.openFileInput("names.json");
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(inputStreamReader)){
                String line = reader.readLine();
                while (line != null)
                {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                String contents = stringBuilder.toString();
                JSONObject jsonString = new JSONObject(contents);
                ratesContent = jsonString.getJSONObject("conversion_rates");
                return ratesContent;
            }catch (IOException e)
            {
                System.err.println("IOException" + e.fillInStackTrace());
            }finally
            {
                return ratesContent;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
