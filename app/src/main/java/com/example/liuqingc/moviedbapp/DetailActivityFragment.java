package com.example.liuqingc.moviedbapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);

        WebView movieDetails = (WebView) detailView.findViewById(R.id.webView);
        movieDetails.setWebViewClient(new MyWebViewClient());
        movieDetails.getSettings().setJavaScriptEnabled(true);
        movieDetails.loadUrl("http://www.themoviedb.org/movie/"+getActivity().getIntent().getStringExtra("movieID"));

        return detailView;
    }

private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
