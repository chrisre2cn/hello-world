package com.example.liuqingc.moviedbapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.liuqingc.moviedbapp.R.drawable.ic_sync_black_24dp;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter myAdapter;

    private String queryMethod = "popularity.desc";

      @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }


    public class MovieBrief {
        private String movieID;
        private String logoURL;
        public MovieBrief(String movieID, String logoURL) {
            this.movieID = movieID;
            this.logoURL = logoURL;
        }

        public String getMovieID() {
            return movieID;
        }

        public String getLogoURL() {
            return logoURL;
        }
    }

    public MainActivityFragment() {
    }


    public class MovieAdapter extends ArrayAdapter<MovieBrief> {

        private Context context;

        public MovieAdapter(Context context, int resource) {
            super(context, resource);
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
  //          return super.getView(position, convertView, parent);
   //               ImageView imageView;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = (ImageView) mInflater.inflate(R.layout.logo_layout, parent, false);

       }
            String logoURL = getItem(position).getLogoURL();

            if ( logoURL.equalsIgnoreCase("null"))
                    ((ImageView) convertView).setImageResource(ic_sync_black_24dp);
            else
                    Picasso.with(context).load(logoURL).into((ImageView) convertView);

            return convertView;

        }
    }


       private MovieBrief[] getMovieDataFromJson(String myJsonStr, int numMovies)
            throws JSONException {


        final String TMDB_RESULT = "results";
        final String TMDB_ID = "id";
        final String TMDB_LOGO = "poster_path";
           final String TMDB_URL_PREFIX = "http://image.tmdb.org/t/p/w185";


        JSONObject moviesJson = new JSONObject(myJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULT);


        MovieBrief[] resultStrs = new MovieBrief[numMovies];
        for(int i = 0; i < movieArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String id;
            String logo_path;

            // Get the JSON object representing the day
            JSONObject movie = movieArray.getJSONObject(i);

            id=movie.getString(TMDB_ID);


            logo_path=movie.getString(TMDB_LOGO);

            if ( !logo_path.equalsIgnoreCase("null")) {
                logo_path=TMDB_URL_PREFIX+logo_path;
            }

            resultStrs[i] = new MovieBrief(id,logo_path);

        }


        return resultStrs;

    }

    public class discoverMoviesTask extends AsyncTask < String,Void,MovieBrief[] > {

         protected void onPostExecute(MovieBrief[] netcast) {

            if ( netcast != null ) {
                myAdapter.clear();
                myAdapter.addAll(netcast);
            }
        }


        protected  MovieBrief[] doInBackground(String... querymode) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String myJsonStr = null;

            try

            {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("api.themoviedb.org");
                builder.appendPath("3");
                builder.appendPath("discover");
                builder.appendPath("movie");
                builder.appendQueryParameter("api_key", "8e81975d4df0e73feccb0eb10926645a");
                builder.appendQueryParameter("sort_by", querymode[0]);

                Log.v("url to be built:", builder.toString());



                URL url = new URL(builder.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                myJsonStr = buffer.toString();
            } catch (
                    IOException e
                    )

            {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }


            MovieBrief[] resultstr=null;
            try { resultstr = getMovieDataFromJson(myJsonStr, 20); }
            catch (JSONException e)
            {
                Log.e("error", e.getMessage());
            }

            return resultstr;

        }



    }



    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootview =  inflater.inflate(R.layout.fragment_main, container, false);




 //      Picasso.with(getActivity().getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(imageView);

    GridView gridview = (GridView) rootview.findViewById(R.id.gridview);
       myAdapter = new MovieAdapter(getContext(),0);
        myAdapter.clear();
        myAdapter.add(new MovieBrief("0", "http://i.imgur.com/DvpvklR.png"));
        gridview.setAdapter(myAdapter);

        discoverMoviesTask loadMovies = new discoverMoviesTask();
        loadMovies.execute(queryMethod);

    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
//            Toast.makeText(getContext(), "" + position,
//                    Toast.LENGTH_SHORT).show();
                        Intent detailPage = new Intent(getContext(), DetailActivity.class );
                        detailPage.putExtra("movieID",myAdapter.getItem(position).getMovieID() );
                        startActivity(detailPage);

        }
    });
        return rootview;
    }


        @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.discovermenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popularity) {

            queryMethod = (String) getResources().getText(R.string.mostPopular);

        }else if ( id == R.id.action_sort_vote ) {
                queryMethod = (String) getResources().getText(R.string.bestRated);
        }

        discoverMoviesTask loadMovies = new discoverMoviesTask();
        loadMovies.execute(queryMethod);

        return super.onOptionsItemSelected(item);
    }




}



