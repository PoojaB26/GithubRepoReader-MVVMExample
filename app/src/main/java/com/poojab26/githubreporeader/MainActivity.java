package com.poojab26.githubreporeader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new GsonBuilder()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GithubService githubClient = retrofit.create(GithubService.class);

        // Fetch a list of the Github repositories.
        Call<List<Repo>> call = githubClient.reposForUser("square");

        // Execute the call asynchronously.
        call.enqueue(new Callback<List<Repo>>() {

            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                if (response.isSuccessful()) {
                    List repos = response.body();
                   /* for (int i=0; i<repos.size(); i++) {
                        Log.d(TAG, "Repo is: " + repos.);
                    }*/
                } else {
                    //errors like 404s show up here, so assume nothing...nothing I tell you!
                    //infact all the error codes show up here : status 400 -599
                    //so even if onResponse called, it could still be null
                    //you can find out exact error with : response.errorBody().string()
                }
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                // the network call was a failure
                Log.d(TAG, "Error Occurred");
            }
        });

    }
}
