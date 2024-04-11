package com.example.movieappv22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class movieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_details);
        File path = getFilesDir();
        if (!new File(path, "LET").exists()) {
            new File(path, "LET").mkdir();
        }
        File letDirectory = new File(path, "LET");
        if (!(new File(letDirectory, "/watchlist.txt").exists())) {
            Log.d("readFile", "file created");
            try {
                new File(path,"/watchlist.txt").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            Log.d("readFile","File existed");
        }
        File file = new File(letDirectory, "watchlist.txt");
        OkHttpClient client = new OkHttpClient();
        TextView name = findViewById(R.id.gridData);
        ImageView image = findViewById(R.id.gridImage);
        String id = getIntent().getStringExtra("id");
        String media = getIntent().getStringExtra("media");
        if(media.compareTo("movie")==0) {
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/" + id + "&language=en-US")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader(
                            "Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZmM3MWMxNzZjNzVjZTQzZDk2MWFiYTc1NWFiNWFjMSIsInN1YiI6IjY1ZTgwZTllMzQ0YThlMDE3ZDNlZmQyOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.phSeMPz4gwQ-daRBwmo21fg-HCWDQcpZfd3IWX2VXuM"
                    )
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        String jsondata = null;
                        try {
                            jsondata = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(jsondata);
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = getBitmapFromUrl(jObject.optString("poster_path"));
                        JSONObject finalJObject = jObject;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(finalJObject.optString("title"));
                                image.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
        }else {
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + id + "&language=en-US")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader(
                            "Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZmM3MWMxNzZjNzVjZTQzZDk2MWFiYTc1NWFiNWFjMSIsInN1YiI6IjY1ZTgwZTllMzQ0YThlMDE3ZDNlZmQyOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.phSeMPz4gwQ-daRBwmo21fg-HCWDQcpZfd3IWX2VXuM"
                    )
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        String jsondata = null;
                        try {
                            jsondata = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(jsondata);
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = getBitmapFromUrl(jObject.optString("poster_path"));
                        JSONObject finalJObject = jObject;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(finalJObject.optString("title"));
                                image.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
        }
        findViewById(R.id.addBookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write((id + "$" + media + "\n"));
                    fileWriter.close();
                    Toast.makeText(movieDetails.this, "Bookmarked", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap getBitmapFromUrl(String src) {
        try {
            Log.e("src", src);
            URL url = new URL("https://image.tmdb.org/t/p/original/" + src);
            InputStream connection = url.openStream();
            Bitmap image = BitmapFactory.decodeStream(connection);
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    private void enableEdgeToEdge() {
        // Implement edge-to-edge functionality here
    }
}

