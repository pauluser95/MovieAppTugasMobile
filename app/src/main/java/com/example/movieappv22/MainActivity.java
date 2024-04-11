package com.example.movieappv22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movieappv22.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.flFragment,homeFragment.class,null).commit();
        binding.btnHome.setOnClickListener(v -> replaceFragment(new homeFragment()));
        binding.btnWatchlist.setOnClickListener(v -> replaceFragment(new watchlistFragment()));
        binding.btnSearch.setOnClickListener(v -> replaceFragment(new searchFragment()));
        binding.btnProfile.setOnClickListener(v -> replaceFragment(new profileFragment()));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }

    public static Bitmap getBitmapFromUrl(String src) {
        try {
            Log.e("src", src);
            String url = "https://image.tmdb.org/t/p/original/" + src;
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            java.io.InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }
}