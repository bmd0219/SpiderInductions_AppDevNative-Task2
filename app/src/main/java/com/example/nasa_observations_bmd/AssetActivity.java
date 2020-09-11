package com.example.nasa_observations_bmd;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssetActivity extends AppCompatActivity {

    private ImageView assetImageView;
    private ProgressBar progressBarAsset;
    private String nasaId;
    private String mediaType;
    private WebView assetWebView;
    private TextView assetNasaIdTextView;

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
        assetNasaIdTextView = findViewById(R.id.asset_nasa_id_text_view);

        assetWebView = findViewById(R.id.asset_web_view);
        assetWebView.setWebViewClient(new Browser_Home());
        assetWebView.setWebChromeClient(new ChromeClient());


        WebSettings webSettings = assetWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);

        Intent intent = getIntent();
        nasaId = intent.getStringExtra("nasa_id");
        mediaType = intent.getStringExtra("media_type");
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
        if(mediaType.equals("image")) {
            Bitmap bitmap = null;
            try {
                bitmap = Picasso.get().load(asset.getCollection().getItems().get(1).getHref()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap finalBitmap = bitmap;
            runOnUiThread(() -> {
                setTheme(R.style.AppTheme);
                assetNasaIdTextView.setVisibility(View.VISIBLE);
                progressBarAsset.setVisibility(View.GONE);
                assetImageView.setVisibility(View.VISIBLE);
                assetWebView.setVisibility(View.GONE);
                assetImageView.setImageBitmap(finalBitmap);
            });
        } else {
            runOnUiThread(() -> {
//                setTheme(R.style.AppTheme_NoActionBar);
                assetNasaIdTextView.setVisibility(View.VISIBLE);
                progressBarAsset.setVisibility(View.GONE);
                assetImageView.setVisibility(View.GONE);
                assetWebView.setVisibility(View.VISIBLE);

                for(int i = 0;i < asset.getCollection().getItems().size();i++) {
                    if(asset.getCollection().getItems().get(i).getHref().contains("mobile.mp4")) {
                        assetWebView.loadUrl(asset.getCollection().getItems().get(i).getHref());
                        return;
                    }
                }
            });
        }
    }

    private void postFailure() {
        runOnUiThread(() -> {
            progressBarAsset.setVisibility(View.GONE);
            assetImageView.setImageResource(R.drawable.ic_image_not_found);
        });
    }

    @Override
    public void onBackPressed() {
        if (assetWebView.canGoBack()) {
            assetWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private static class Browser_Home extends WebViewClient {
        Browser_Home(){}

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}