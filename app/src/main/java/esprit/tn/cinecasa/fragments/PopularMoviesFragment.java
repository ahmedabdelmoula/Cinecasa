package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.adapters.MovieAdapter;
import esprit.tn.cinecasa.R;


/**
 * Created by ahmed on 18-Nov-17.
 */

public class PopularMoviesFragment extends Fragment {
    private View view;
    private String urlJsonObj = "https://api.themoviedb.org/3/movie/popular?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    ListView lstMovie;
    private static String TAG = PopularMoviesFragment.class.getSimpleName();
    // Progress dialog
    private ProgressDialog pDialog;
    // temporary string to show the parsed response
    MovieAdapter adapter;
    List<Movie> moviesList;
    Fragment fragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_popularmovies, container, false);
        lstMovie = (ListView) view.findViewById(R.id.lv_movies);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        makeJsonObjectRequest();
        fragment=this;
        //Action mta3 ki yenzel 3la element fi list
//        lstMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                FragmentManager fragmentManager=fragment.getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.container, new MovieDetailsFragment(), "MovieDetailsFragment" ).commit();
//                Context.ITEM_MOVIE=(Movie)parent.getItemAtPosition(position);
//
//            }
//        });
        return view;
    }

    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("results");

                    List<Movie> dataSource = new ArrayList<>();
                    // nsob fi movies lkol fi west arraylist
                    for (int i = 0; i < results.length(); i++) {

                        JSONObject movie = (JSONObject) results.get(i);

                        String vote_count= movie.getString("vote_count");
                        int id= movie.getInt("id");
                        boolean video= movie.getBoolean("video");
                        Double vote_average= movie.getDouble("vote_average");
                        String title= movie.getString("title");
                        Double popularity= movie.getDouble("popularity");
                        String poster_path=("https://image.tmdb.org/t/p/w300"+movie.getString("poster_path"));
                        String original_language= movie.getString("original_language");
                        String original_title= movie.getString("original_title");
                        String genre_ids= movie.getString("genre_ids");
                        //String backdrop_path= movie.getString("https://image.tmdb.org/t/p/w300"+"backdrop_path");
                        boolean adult= movie.getBoolean("adult");
                        String overview= movie.getString("overview");
                        String release_date= movie.getString("release_date");
                        Movie movie1 = new Movie( vote_count,  id,  video,  vote_average,  title,  popularity,  poster_path,  original_language,  original_title,  genre_ids,  adult,  null,  overview,  release_date);

                        dataSource.add(movie1);

                    }
                    moviesList=dataSource;
                    //hna kima fel cour adapter w heka ri9
                    adapter = new MovieAdapter(getActivity(),R.layout.movie_item,dataSource );
                    lstMovie.setAdapter(adapter);
                    // hedhiya methode bech list todhhor mrigla fi west scrollable view ahika louta
                    setListViewHeightBasedOnChildren(lstMovie);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        System.out.println("hhhhhhhhhhhhey"+jsonObjReq.getBody());
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }



    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewPager.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
