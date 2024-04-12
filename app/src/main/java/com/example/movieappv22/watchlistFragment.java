package com.example.movieappv22;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class Watchlisted {
    public String id;
    public String media;
    public boolean isWatched;

    public Watchlisted(String id, boolean isWatched, String media) {
        this.id = id;
        this.isWatched = isWatched;
        this.media=media;
    }


    public static ArrayList<Watchlisted> createWatchlistList(ArrayList<String> id, ArrayList<String>media) {
        ArrayList<Watchlisted> watchlisted = new ArrayList<>();
        for (int i = 0; i < id.size(); i++) {
            watchlisted.add(new Watchlisted(id.get(i), false, media.get(i)));
        }
        return watchlisted;
    }
}

class WatchlistedAdapter extends RecyclerView.Adapter<WatchlistedAdapter.ViewHolder> {
    private final List<Watchlisted> mWatchlisted;
    private Context context;
    public WatchlistedAdapter(List<Watchlisted> watchlisted, Context context) {

        mWatchlisted = watchlisted;
        this.context=context;

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        public final TextView nameTextView;
        public final Button watchButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.watchlist_name);
            watchButton = itemView.findViewById(R.id.watched_button);
        }
    }

    @Override
    public WatchlistedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.watchlist_item, parent, false);
        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(WatchlistedAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Watchlisted movie = mWatchlisted.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        Button button = viewHolder.watchButton;
        int id = Integer.parseInt(movie.id);
        String media= movie.media;
        String url;
        if(media.compareTo("movie")==0) {
            url = "https://api.themoviedb.org/3/movie/" + id + "&language=en-US";
        }else {
            url = "https://api.themoviedb.org/3/tv/" + id + "&language=en-US";
        }
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader(
                            "Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZmM3MWMxNzZjNzVjZTQzZDk2MWFiYTc1NWFiNWFjMSIsInN1YiI6IjY1ZTgwZTllMzQ0YThlMDE3ZDNlZmQyOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.phSeMPz4gwQ-daRBwmo21fg-HCWDQcpZfd3IWX2VXuM"
                    )
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsondata = response.body().string();
                        JSONObject jObject = null;
                        try {
                            Handler handler = new Handler(Looper.getMainLooper());
                            jObject = new JSONObject(jsondata);
                            String title;
                            if(media.compareTo("movie")==0) {
                                title = jObject.getString("title");
                            }else{
                                title = jObject.getString("name");
                            }
                            String link  = jObject.getString("homepage");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(link));
                                handler.post(() -> {
                                    textView.setText(title);
                                    button.setOnClickListener(v->{context.startActivity(intent);});
                                });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

            });
            button.setText(movie.isWatched ? "Watched" : "Watch now");
            button.setEnabled(!movie.isWatched);
    }

    @Override
    public int getItemCount() {
        return mWatchlisted.size();
    }
}
public class watchlistFragment extends Fragment {
    private ArrayList<Watchlisted> watchlists;

    public watchlistFragment() {
        super(R.layout.fragment_watchlist);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView watchlistRecycler = view.findViewById(R.id.watchlistRecycler);
        File path = getActivity().getFilesDir();
        if (!new File(path, "LET").exists()) {
            new File(path, "LET").mkdir();
        }
        File letDirectory = new File(path, "LET");
        if (!new File(letDirectory, "/watchlist.txt").exists()) {
            Log.d("readFile", "file created");
            try {
                new File(letDirectory, "/watchlist.txt").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File file = new File(letDirectory, "watchlist.txt");
        ArrayList<String> watchlist = new ArrayList<>();
        ArrayList<String> media = new ArrayList<>();
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedReader read = new BufferedReader(new InputStreamReader(fis))) {
                String line;
                while ((line = read.readLine()) != null) {
                    String[] details = line.split("#",2);
                    Log.d("readFile",details[0]);
                    watchlist.add(details[0]);
                    media.add(details[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        watchlists = Watchlisted.createWatchlistList(watchlist,media);
        WatchlistedAdapter adapter = new WatchlistedAdapter(watchlists,this.getActivity());
        watchlistRecycler.setAdapter(adapter);
        watchlistRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}