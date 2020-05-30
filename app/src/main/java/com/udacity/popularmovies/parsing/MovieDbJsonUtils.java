package com.udacity.popularmovies.parsing;

import android.content.Context;

import com.udacity.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDbJsonUtils {

    public static Movie[] getMoviesFromJson(Context context, String jsonString) throws JSONException {
        final String BASE_URL = "https://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500";
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String VOTE = "vote_average";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";

        JSONObject rootMovieObj = new JSONObject(jsonString);
        JSONArray movieResultsArray = rootMovieObj.getJSONArray(RESULTS);
        Movie[] movieResults = new Movie[movieResultsArray.length()];
        for (int i = 0; i < movieResultsArray.length(); i++) {
            Movie movie = new Movie();
            movie.setMoviePoster(BASE_URL + POSTER_SIZE + movieResultsArray.getJSONObject(i).optString(POSTER_PATH));
            movie.setMovieTitle(movieResultsArray.getJSONObject(i).optString(TITLE));
            movie.setMovieReleaseDate(movieResultsArray.getJSONObject(i).optString(RELEASE_DATE));
            movie.setMovieUserRating(movieResultsArray.getJSONObject(i).optString(VOTE));
            movie.setMovieOverview(movieResultsArray.getJSONObject(i).optString(OVERVIEW));
            movieResults[i] = movie;
        }
        return movieResults;
    }
}
