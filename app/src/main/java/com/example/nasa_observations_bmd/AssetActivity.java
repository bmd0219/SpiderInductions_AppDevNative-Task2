package com.example.nasa_observations_bmd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssetActivity extends AppCompatActivity {

    private ImageView assetImageView;
    private ProgressBar progressBarAsset;
    private String nasaId;

    class AssetNetworkThread extends Thread {

        private NasaAPI nasaAPI;
        private String nasaId;

        AssetNetworkThread(NasaAPI nasaAPI, String nasaId) {
            this.nasaAPI = nasaAPI;
            this.nasaId = nasaId;
        }

        @Override
        public void run() {
            Call<Asset> call = nasaAPI.getAsset(nasaId);
            try {
                Response<Asset> response = call.execute();
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    postResults(response.body());
                } else {
                    postFailure();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        assetImageView = findViewById(R.id.asset_image_view);
        progressBarAsset = findViewById(R.id.progress_bar_asset);
        TextView assetNasaIdTextView = findViewById(R.id.asset_nasa_id_text_view);

        Intent intent = getIntent();
        nasaId = intent.getStringExtra("nasa_id");
        assetNasaIdTextView.setText(nasaId);

        generateApi();
    }

    private void generateApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://images-api.nasa.gov/asset/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NasaAPI nasaAPI = retrofit.create(NasaAPI.class);

        AssetNetworkThread assetNetworkThread = new AssetNetworkThread(nasaAPI, nasaId);
        assetNetworkThread.start();
        progressBarAsset.setVisibility(View.VISIBLE);
    }

    private void postResults(Asset asset) {
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.get().load(asset.getCollection().getItems().get(1).getHref()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap finalBitmap = bitmap;
        runOnUiThread(() -> {
            progressBarAsset.setVisibility(View.GONE);
            assetImageView.setImageBitmap(finalBitmap);
        });
    }

    private void postFailure() {
        runOnUiThread(() -> {
            progressBarAsset.setVisibility(View.GONE);
            assetImageView.setImageResource(R.drawable.ic_image_not_found);
        });
    }
}