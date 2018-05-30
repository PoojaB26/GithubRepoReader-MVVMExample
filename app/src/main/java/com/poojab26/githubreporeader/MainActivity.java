package com.poojab26.githubreporeader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private RecyclerView recyclerView;
    private List<Repo> data;
    private RecyclerAdapter adapter;

    String TAG = "MVVM Tutorial_MainActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise RecyclerView
        initViews();

        //Fetch results from GitHub API
        fetchGitHub();

    }

    private void initViews(){
        recyclerView = findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new
                LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void fetchGitHub() {
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
            public void onResponse(Call<List<Repo>> call,
                                   Response<List<Repo>> response) {
                // The network call was a success and we got a response
                data = response.body();
                adapter = new RecyclerAdapter(data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                // the network call was a failure
                Log.d(TAG, "Error Occurred");
            }
        });
    }
}