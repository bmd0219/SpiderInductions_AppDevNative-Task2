package com.example.nasa_observations_bmd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APODActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String API_KEY = "fdfBo3nzaBdFIcZn1is4hjfn1B2FXPnaDSRMsHKb";

    private ImageView imageView;
    private ImageView imageViewVideo;
    private TextView textView;
    private TextView dateTextView;
    private NasaAPI nasaAPI;
    private ProgressBar progressBar;

    class ApodNetworkThread extends Thread {

        private NasaAPI nasaAPI;
        private String date;

        ApodNetworkThread(NasaAPI nasaAPI, String date) {
            this.nasaAPI = nasaAPI;
            this.date = date;
        }

        @Override
        public void run() {
            Call<APOD> call = nasaAPI.getAPOD("apod", API_KEY, date);
            try {
                Response<APOD> response = call.execute();
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
        setContentView(R.layout.activity_apod);

        imageView = findViewById(R.id.image_view_apod);
        imageViewVideo = findViewById(R.id.image_view_video);
        textView = findViewById(R.id.text_view);
        dateTextView = findViewById(R.id.date_text_view);
        progressBar = findViewById(R.id.progress_bar_apod);
        Button datePickerButton = findViewById(R.id.date_picker_button);

        generateApi();

        datePickerButton.setOnClickListener(view -> {
            DialogFragment dialogFragment = new DatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), "DatePickerDialog");
        });
    }

    private void generateApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/planetary/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nasaAPI = retrofit.create(NasaAPI.class);
    }

    private void postResults(APOD apod) {
        boolean video = false;
        Bitmap bitmap = null;
        String temp = null;
        if (apod.getMedia_type().equals("image")) {
            try {
                bitmap = Picasso.get().load(apod.getUrl()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            video = true;
            String url = apod.getUrl();
            int pos = url.indexOf("?");
            if (pos != -1) {
                temp = url.substring(30, pos);
            } else {
                temp = url.substring(30);
            }
            url = "https://img.youtube.com/vi/" + temp + "/0.jpg";
            Log.println(Log.ASSERT, "AA", url);
            try {
                bitmap = Picasso.get().load(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap finalBitmap = bitmap;
        boolean finalVideo = video;
        String finalTemp = temp;
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(finalBitmap);
            textView.setText("Description:\n\n" + apod.getExplanation());
            if(finalVideo) {
                imageViewVideo.setVisibility(View.VISIBLE);
                imageViewVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://m.youtube.com/watch?feature=youtu.be&v=" + finalTemp));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void postFailure() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            textView.setText("COMING SOON!!!");
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String date = i + "-" + (++i1) + "-" + i2;
        Log.println(Log.ASSERT, "MA", String.valueOf(i) + i1 + i2);
        dateTextView.setText(i2 + "-" + (i1) + "-" + i);
        ApodNetworkThread apodNetworkThread = new ApodNetworkThread(nasaAPI, date);
        apodNetworkThread.start();
        runOnUiThread(() -> {
            imageViewVideo.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(null);
            textView.setText(null);
        });
    }
}