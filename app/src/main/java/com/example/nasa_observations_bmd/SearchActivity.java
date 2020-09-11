package com.example.nasa_observations_bmd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private TextView searchErrorTextView;
    private NasaAPI nasaAPI;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBarSearch;
    private CoordinatorLayout coordinatorLayout;

    class SearchNetworkThread extends Thread {

        private NasaAPI nasaAPI;
        private String keyword;

        SearchNetworkThread(NasaAPI nasaAPI, String keyword) {
            this.nasaAPI = nasaAPI;
            this.keyword = keyword;
        }

        @Override
        public void run() {
            Call<SearchResult> call = nasaAPI.getSearchResults("search", keyword);
            try {
                SearchResult searchResult = call.execute().body();
                assert searchResult != null;
                if (searchResult.getCollection().getMetadata().getTotal_hits() != 0) {
                    ArrayList<SearchResult.Collection.Item> items = new ArrayList<>(searchResult.getCollection().getItems());
                    generateViews(items);
                } else {
                    postNoSearchResults();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = findViewById(R.id.search_keyword_button);
        searchEditText = findViewById(R.id.search_bar_edit_text);
        progressBarSearch = findViewById(R.id.progress_bar_search);
        searchErrorTextView = findViewById(R.id.search_failure_display);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        generateApi();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchErrorTextView.setVisibility(View.GONE);
                recyclerView.setAdapter(new RecyclerViewAdapter(new ArrayList<>()));
                if (!searchEditText.getText().toString().equals("")) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    startRequest(nasaAPI, searchEditText.getText().toString());
                    progressBarSearch.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "Enter keyword to search", Toast.LENGTH_SHORT).show();
                } else {
                    coordinatorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchButton.setOnClickListener(view -> {
            if (searchEditText.getText().toString().equals("")) {
                Toast.makeText(this, "Enter keyword to search", Toast.LENGTH_SHORT).show();
            } else {
                recyclerView.setAdapter(new RecyclerViewAdapter(new ArrayList<>()));
                progressBarSearch.setVisibility(View.VISIBLE);
                startRequest(nasaAPI, searchEditText.getText().toString());
            }
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        });
    }

    private void generateApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://images-api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nasaAPI = retrofit.create(NasaAPI.class);
    }

    private void startRequest(NasaAPI nasaAPI, String keyword) {
        SearchNetworkThread searchNetworkThread = new SearchNetworkThread(nasaAPI, keyword);
        searchNetworkThread.start();
    }

    private void generateViews(List<SearchResult.Collection.Item> items) {
        recyclerViewAdapter = new RecyclerViewAdapter(items);
        runOnUiThread(() -> {
            searchErrorTextView.setVisibility(View.GONE);
            progressBarSearch.setVisibility(View.GONE);
            recyclerView.setAdapter(recyclerViewAdapter);
        });
        recyclerViewAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(SearchActivity.this, AssetActivity.class);
            intent.putExtra("nasa_id", item.getData().get(0).getNasa_id());
            intent.putExtra("media_type", item.getData().get(0).getMedia_type());
            startActivity(intent);
        });
    }

    private void postNoSearchResults() {
        runOnUiThread(() -> {
            coordinatorLayout.setVisibility(View.GONE);
            searchErrorTextView.setVisibility(View.VISIBLE);
            progressBarSearch.setVisibility(View.GONE);
        });
    }
}