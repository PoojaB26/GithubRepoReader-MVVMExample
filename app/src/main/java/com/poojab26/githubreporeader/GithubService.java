package com.poojab26.githubreporeader;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by poojab26 on 30-May-18.
 */
public interface GithubService {
    String ENDPOINT = "https://api.github.com";

    @GET("/users/{user}/repos")
    Call<List<Repo>> reposForUser (@Path("user") String user);
}