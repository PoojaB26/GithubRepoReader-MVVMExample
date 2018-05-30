package com.poojab26.githubreporeader;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Repo> data;
    private RecyclerAdapter adapter;
    private EditText editTextUser;
    private Button btnFetch;
    private TextInputLayout textInputLayout;
    private CompositeDisposable compositeDisposable;

    String TAG = "MVVM Tutorial_MainActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise RecyclerView
        initViews();

        getUserInput();
        //Fetch results from GitHub API

    }

    private void getUserInput() {
        Observable<CharSequence> editTextObservable = RxTextView.textChanges(editTextUser);

        editTextObservable
                .skip(1) //1
                .debounce(500, TimeUnit.MILLISECONDS) //2
                .observeOn(AndroidSchedulers.mainThread())  //3
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                            btnFetch.setEnabled(false);
                            textInputLayout.setErrorEnabled(false);
                        String strUsername = charSequence.toString().trim();

                        //Regex for Github username rules
                        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+(?:[A-Za-z0-9-])*+([a-zA-Z0-9])*\\b$");
                        Matcher matcher = pattern.matcher(strUsername);

                        if (strUsername.matches("")) {
                            //empty string
                            textInputLayout.setError(getResources().getString(R.string.err_msg_emptybox));
                            textInputLayout.setErrorEnabled(true);
                        }else if (!matcher.matches()){
                            //string contains spaces, underscores or non-alphanumeric characters
                            textInputLayout.setError(getResources().getString(R.string.err_msg_username));
                            textInputLayout.setErrorEnabled(true);
                        }else {
                            //string is valid
                            btnFetch.setEnabled(true);
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initViews(){
        recyclerView = findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new
                LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        editTextUser = findViewById(R.id.etUser);
        btnFetch = findViewById(R.id.btnFetch);

        Observable<Object> buttonObservable = RxView.clicks(btnFetch);
        buttonObservable.subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);

            }

            @Override
            public void onNext(Object o) {
                recyclerView.removeAllViewsInLayout();
                String strGithubUser = editTextUser.getText().toString().trim();
                fetchGitHub(strGithubUser);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.removeAllViewsInLayout();
                String strGithubUser = editTextUser.getText().toString().trim();
                fetchGitHub(strGithubUser);
            }
        });

    }

    private void fetchGitHub(String user) {
        Gson gson = new GsonBuilder()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        GithubService githubClient = retrofit.create(GithubService.class);

        // Fetch a list of the Github repositories.
        Observable<List<Repo>> reposReturnedObservable = githubClient.reposForUser(user);

        reposReturnedObservable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Repo> repos) {
                        data = repos;
                        adapter = new RecyclerAdapter(data);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

     /*   // Execute the call asynchronously.
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
        });*/
    }

    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}