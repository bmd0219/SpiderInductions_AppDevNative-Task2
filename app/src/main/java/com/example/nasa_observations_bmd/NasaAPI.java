package com.example.nasa_observations_bmd;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NasaAPI {

    @GET("{path}")
    Call<APOD> getAPOD(@Path("path") String path, @Query("api_key") String apiKey, @Query("date") String date);

    @GET("{path}")
    Call<SearchResult> getSearchResults(@Path("path") String path, @Query("q") String query);

    @GET("{path}")
    Call<Asset> getAsset(@Path("path") String nasaId);
}
