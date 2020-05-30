package com.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.udacity.popularmovies.adapter.MovieAdapter;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.parsing.MovieDbJsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    @BindView(R.id.rv_movies)
    RecyclerView mDisplayMovieRecycleView;
    @BindView(R.id.layout_empty_view)
    ConstraintLayout mEmptyConstraintLayout;
    @BindView(R.id.progress_loading)
    ProgressBar mEmptyProgressbar;
    @BindView(R.id.textView_empty)
    TextView mEmptyTextview;

    private MovieAdapter mMovieAdapter;
    private Movie[] mMovieData;

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        int mNoOfColumns = calculateNoOfColumns(getApplicationContext());
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns);
        mDisplayMovieRecycleView.setLayoutManager(layoutManager);
        mDisplayMovieRecycleView.setHasFixedSize(true);
        mDisplayMovieRecycleView.setAdapter(mMovieAdapter);
        loadMovieData("popular");
    }

    private void loadMovieData(String mQueryCategory) {
        if (isInternetAvailable()) {
            showResult();
            new FetchMovieTask().execute(mQueryCategory);
        } else {
            showErrorMessage();
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    private void showResult() {
        mEmptyConstraintLayout.setVisibility(View.INVISIBLE);
        mDisplayMovieRecycleView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mDisplayMovieRecycleView.setVisibility(View.INVISIBLE);
        mEmptyConstraintLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int adapterPosition) {
        Intent intentToDeatilActivity = new Intent(this, DetailActivity.class);
        intentToDeatilActivity.putExtra("MovieData", mMovieData[adapterPosition]);
        startActivity(intentToDeatilActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected == R.id.action_most_popular) {
            loadMovieData("popular");
            return true;
        }

        if (menuItemSelected == R.id.action_top_rated) {
            loadMovieData("top_rated");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmptyProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                mMovieData = MovieDbJsonUtils.getMoviesFromJson(MovieActivity.this, jsonMovieResponse);
                return mMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            mEmptyProgressbar.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showResult();
                mMovieAdapter = new MovieAdapter(getApplicationContext(), movieData, MovieActivity.this);
                mDisplayMovieRecycleView.setAdapter(mMovieAdapter);
            } else {
                showErrorMessage();
            }
        }
    }
}
