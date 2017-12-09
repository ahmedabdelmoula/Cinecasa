package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.CastAdapter;
import esprit.tn.cinecasa.adapters.ReviewAdapter;
import esprit.tn.cinecasa.entities.Cast;
import esprit.tn.cinecasa.entities.Review;
import esprit.tn.cinecasa.entities.YoutubeVideo;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Config;
import esprit.tn.cinecasa.utils.Context;

/**
 * Created by ahmed on 18-Nov-17.
 */

public class MovieDetailsFragment extends Fragment {
    private RecyclerView recyclerView;
    private CastAdapter castadapter;
    private List<Cast> castList;
    private List<Review> listReview;
    private View view;
    RoundedImageView ivposter;
    ListView reviewList;
    Fragment fragment=this;
    ImageView detailsBG;
    TextView txttitle,txtvote_count,txtvote_average,txtpopularity,txtoriginal_title,txtoverview,txtrelease_date;
    EditText txtratevalue;
    Button btrate;
    String session_id;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    // YouTube player view
    private YouTubePlayerSupportFragment youTubePlayer;
    private String urlJsonObj = "https://api.themoviedb.org/3/movie/";
    private String urlSession="https://api.themoviedb.org/3/authentication/guest_session/new?api_key=7c408d3e3e9aec97d01604333744b592";
    private String urlJsonObjPart2="/videos?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private String urlJsonCast = "https://api.themoviedb.org/3/movie/";
    private String urlJsonCastPart2="/credits?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private String urlJsonReview="https://api.themoviedb.org/3/movie/"+ Context.ITEM_MOVIE.getId()+"/reviews?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&page=1";
    private static String TAG = MovieDetailsFragment.class.getSimpleName();
    String Youtube_code="";
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        castList = new ArrayList<>();
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setCancelable(false);
        urlJsonObj=urlJsonObj+ Context.ITEM_MOVIE.getId()+urlJsonObjPart2;
        urlJsonCast=urlJsonCast+ Context.ITEM_MOVIE.getId()+urlJsonCastPart2;
        makeJsonObjectRequest();
        makeJsonObjectCastRequest();
        makeJsonObjectReviewRequest();

        txtratevalue = (EditText) view.findViewById(R.id.ratevalue);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        btrate = (Button) view.findViewById(R.id.btrate);
        txttitle = (TextView) view.findViewById(R.id.txttitle);
        txtvote_count = (TextView) view.findViewById(R.id.txtvote_count);
        txtvote_average = (TextView) view.findViewById(R.id.txtvote_average);
        txtpopularity = (TextView) view.findViewById(R.id.txtpopularity);
        txtoriginal_title = (TextView) view.findViewById(R.id.txtoriginal_title);
        txtoverview = (TextView) view.findViewById(R.id.txtoverview);
        txtrelease_date = (TextView) view.findViewById(R.id.txtrelease_date);
        ivposter = (RoundedImageView) view.findViewById(R.id.ivposter);
        detailsBG = (ImageView) view.findViewById(R.id.details_bg);
        reviewList = (ListView) view.findViewById(R.id.listReview);

        Glide
                .with(getContext())
                .load(R.drawable.details_bg)
                .into(detailsBG);

        txttitle.setText( Context.ITEM_MOVIE.getTitle());
        txtvote_count.setText("Vote Count : "+ Context.ITEM_MOVIE.getVote_count());
        txtvote_average.setText("Vote Average : "+ Context.ITEM_MOVIE.getVote_average().toString());
        txtpopularity.setText("Popularity : "+ Context.ITEM_MOVIE.getPopularity().toString());
        txtoriginal_title.setText("Original Title : "+ Context.ITEM_MOVIE.getOriginal_title());
        txtrelease_date.setText("Release Date : "+ Context.ITEM_MOVIE.getRelease_date());
        txtoverview.setText("Story : "+ Context.ITEM_MOVIE.getOverview());
        Glide.with(getActivity()).load(Context.ITEM_MOVIE.getPoster_path()).into(ivposter);

        youTubePlayer = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayer).commit();
        // Initializing video player with developer key
        youTubePlayer.initialize(Config.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {

                    player.cueVideo(Youtube_code);
                    player.play();
                    player.setShowFullscreenButton(true);

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(fragment.getActivity(), RECOVERY_DIALOG_REQUEST).show();
                } else {
                    String errorMessage = String.format(
                            getString(R.string.error_player), errorReason.toString());
                    Toast.makeText(fragment.getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
        btrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getSession();

            }
        });



        return view;
    }



    private void makeJsonObjectRequest() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("results");
                    List<YoutubeVideo> dataSource = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject movie = (JSONObject) results.get(i);
                        YoutubeVideo youtubeVideo=new YoutubeVideo(movie.getString("key"),movie.getString("size"));

                        dataSource.add(youtubeVideo);

                    }
                    Youtube_code=dataSource.get(0).getKey();
                    for (YoutubeVideo y : dataSource)
                    {
                        if (y.getSize().contains("720"))
                        {
                            Youtube_code=y.getKey();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeJsonObjectCastRequest() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonCast, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("cast");
                    List<Cast> dataSource = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject cast = (JSONObject) results.get(i);
                        Cast cast1=new Cast(cast.getInt("cast_id"), cast.getString("character"), cast.getString("credit_id"), cast.getInt("gender"), cast.getInt("id"), cast.getString("name"), cast.getInt("order"), "https://image.tmdb.org/t/p/w150"+cast.getString("profile_path"));

                        dataSource.add(cast1);

                    }
                    castList=dataSource;
                    castadapter  = new CastAdapter(getActivity(),castList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                   recyclerView.setAdapter(castadapter);
                   //setListViewHeightBasedOnChildren(recyclerView);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getSession() {


        StringRequest strReq = new StringRequest(Request.Method.GET,urlSession, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "guest_session Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    session_id = jObj.getString("guest_session_id");
                    rate();


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(fragment.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "guest_session Error: " + error.getMessage());
                Toast.makeText(fragment.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);


    }

    private void rate()  {

        StringRequest strReq = new StringRequest(Request.Method.POST,"https://api.themoviedb.org/3/movie/"+Context.ITEM_MOVIE.getId()+"/rating?guest_session_id="+session_id+"&api_key=7c408d3e3e9aec97d01604333744b592", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Rate Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getContext(),jObj.getString("status_message"), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Rate Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("value", txtratevalue.getText().toString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);



    }

    private void makeJsonObjectReviewRequest() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonReview, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("results");
                    List<Review> dataSource = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject cast = (JSONObject) results.get(i);
                        Review review =new Review(cast.getString("id"), cast.getString("author"), cast.getString("content"));

                        dataSource.add(review);

                    }
                    listReview=dataSource;
                    reviewList.setAdapter(new ReviewAdapter(getContext(), R.layout.review_item,listReview));


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
