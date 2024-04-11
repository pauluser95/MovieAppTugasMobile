package com.example.movieappv22;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


class trendingCard {
    public final String id;
    public final String name;
    public final String bitmap;
    public final String media;

    public trendingCard(String id, String name, String bitmap, String media) {
        this.id = id;
        this.name = name;
        this.bitmap = bitmap;
        this.media = media;
    }

    public static ArrayList<trendingCard> createWatchlistList(ArrayList<String> id, ArrayList<String> name, ArrayList<String> bitmap, ArrayList<String> media) {
        ArrayList<trendingCard> trending = new ArrayList<>();
        for (int i = 0; i < id.size(); i++) {
            trending.add(new trendingCard(id.get(i), name.get(i), bitmap.get(i), media.get(i)));
        }
        return trending;
    }
}

class trendingAdapter extends BaseAdapter {
    private final List<trendingCard> mTrending;
    private final LayoutInflater view;

    public trendingAdapter(List<trendingCard> mTrending, Context context) {
        this.mTrending = mTrending;
        this.view = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTrending.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewed = view.inflate(R.layout.trending_card, null);
        ImageView image = viewed.findViewById(R.id.trendingImage);
        TextView text = viewed.findViewById(R.id.trendingText);
        java.util.concurrent.Executor service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        trendingCard title = mTrending.get(position);
        service.execute(() -> {
            Log.d("view", "getting image");
            Bitmap bitmap = getBitmapFromUrl(title.bitmap);
            handler.post(() -> {
                text.setText(title.name);
                image.setImageBitmap(bitmap);
            });
        });

        Log.d("view", "printed");
        return viewed;
    }

    private Bitmap getBitmapFromUrl(String src) {
        try {
            Log.d("get image", src);
            URL url = new URL("https://image.tmdb.org/t/p/original/" + src);
            java.io.InputStream connection = url.openStream();
            Bitmap image = BitmapFactory.decodeStream(connection);
            return image;
        } catch (IOException e) {
            return null;
        }
    }
}

public class homeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("view","opened");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridview = view.findViewById(R.id.trendingGridView);
        ArrayList<trendingCard> list = new ArrayList<>();
        Log.d("view","created");
        list.add(new trendingCard("test", "test", "/5zmiBoMzeeVdQ62no55JOJMY498.jpg", "tv"));
        getTrendingId(list);
        Log.d("view", list.get(0).toString());
        gridview.setAdapter(new trendingAdapter(list, getContext()));
        gridview.setOnItemClickListener((adapterView, view2, i, l) -> {
            Intent intent = new Intent(getActivity(), movieDetails.class);
            intent.putExtra("id", list.get(i).id);
            intent.putExtra("media",list.get(i).media);
            startActivity(intent);
        });
    }

    private void getTrendingId(ArrayList<trendingCard> list) {
        java.util.concurrent.Executor service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/trending/all/day?language=en-US")
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
                    try {
                        String jsondata = response.body().string();
                        JSONObject jObject = new JSONObject(jsondata);
                        int count = jObject.getJSONArray("results").length();
                        for (int i = 0; i < count; i++) {
                            if (jObject.getJSONArray("results").getJSONObject(i).has("title")) {
                                Log.d("test title", jObject.getJSONArray("results").getJSONObject(i).get("title").toString());
                                list.add(new trendingCard(jObject.getJSONArray("results").getJSONObject(i).get("id").toString(), jObject.getJSONArray("results").getJSONObject(i).get("title").toString(), jObject.getJSONArray("results").getJSONObject(i).get("poster_path").toString(), "movie"));
                            } else {
                                Log.d("test title", jObject.getJSONArray("results").getJSONObject(i).get("name").toString());
                                list.add(new trendingCard(jObject.getJSONArray("results").getJSONObject(i).get("id").toString(), jObject.getJSONArray("results").getJSONObject(i).get("name").toString(), jObject.getJSONArray("results").getJSONObject(i).get("poster_path").toString(), jObject.getJSONArray("results").getJSONObject(i).get("media_type").toString()));
                            }
                        }
                    } catch (IOException | org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}