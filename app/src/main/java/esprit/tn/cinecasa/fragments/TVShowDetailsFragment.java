package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.yinglan.shadowimageview.ShadowImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.CastAdapter;
import esprit.tn.cinecasa.entities.Cast;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.AutoResizeTextView;
import esprit.tn.cinecasa.utils.Context;

/**
 * Created by ahmed on 18-Nov-17.
 */

public class TVShowDetailsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private CastAdapter castadapter;
    private List<Cast> castList;
    Fragment fragment=this;
    RatingBar txtratevalue;
    ShadowImageView ivposter;
    AutoResizeTextView txttitle;
    Button btrate;
    ImageView star;
    TextView txtname,txtvote_count,txtvote_average,txtpopularity,txtoriginal_name,txtoverview,txtfirst_air_date,rating;
    private String urlJsongetRated="http://idol-design.com/Cinecasa/Scripts/SelectRatedByUserUid.php?uid_user="+Context.CURRENT_USER.getUid()+"&type=tv";
    String months[] = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};
    private String urlSession="https://api.themoviedb.org/3/authentication/guest_session/new?api_key=7c408d3e3e9aec97d01604333744b592";
    private String urlJsonCast = "https://api.themoviedb.org/3/tv/";
    private String urlJsonCastPart2="/credits?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private static String TAG = TVShowDetailsFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    String session_id;
    List<String> dataSource = new ArrayList<>();
    List<String> dataSource1 = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tvshowdetails, container, false);

        ImageView shadow = (ImageView) view.findViewById(R.id.shadow);
        Glide
                .with(getContext())
                .load(R.drawable.shadow)
                .into(shadow);

        urlJsonCast=urlJsonCast+ Context.ITEM_TV_SHOW.getId()+urlJsonCastPart2;
        makeJsonObjectCastRequest();
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setCancelable(false);
        txtratevalue = (RatingBar) view.findViewById(R.id.ratevalue);
        btrate = (Button) view.findViewById(R.id.btrate);
        getRated();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        txtname = (TextView) view.findViewById(R.id.txtname);
        txtvote_count = (TextView) view.findViewById(R.id.txtvote_count);
        txtvote_average = (TextView) view.findViewById(R.id.txtvote_average);
        txtpopularity = (TextView) view.findViewById(R.id.txtpopularity);
        txtoverview = (TextView) view.findViewById(R.id.txtoverview);
        txtfirst_air_date = (TextView) view.findViewById(R.id.txtfirst_air_date);
        ivposter = (ShadowImageView) view.findViewById(R.id.ivposter);
        rating = (TextView) view.findViewById(R.id.ratingtxt);
        star = (ImageView) view.findViewById(R.id.starrr);
        txtname.setText(Context.ITEM_TV_SHOW.getName());
        txtvote_count.setText("Vote Count : "+ Context.ITEM_TV_SHOW.getVote_count());
        txtvote_average.setText(Context.ITEM_TV_SHOW.getVote_average().toString());

        try {

            String date = Context.ITEM_TV_SHOW.getFirst_air_date();
            String[] splitted = date.split("-");
            txtfirst_air_date.setText(splitted[2] + " " + months[Integer.parseInt(splitted[1])] + " " + splitted[0]);
        } catch (Exception e) {
            txtfirst_air_date.setText(Context.ITEM_MOVIE.getRelease_date());
        }

        txtfirst_air_date.setText(Context.ITEM_TV_SHOW.getFirst_air_date());
        txtoverview.setText(Context.ITEM_TV_SHOW.getOverview());
//        Picasso.with(getActivity()).load(Context.ITEM_TV_SHOW.getPoster_path()).into(ivposter);

        txtoverview.setText(Context.ITEM_TV_SHOW.getOverview());

        Glide.with(getContext())
                .load(Context.ITEM_TV_SHOW.getPoster_path())    // you can pass url too
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // you can do something with loaded bitmap here

                        ivposter.setImageBitmap(resource);
                    }
                });
        btrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getSession();

            }
        });
        return view;
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
                        Cast cast1=new Cast(cast.getString("character"), cast.getString("credit_id"), cast.getInt("gender"), cast.getInt("id"), cast.getString("name"), cast.getInt("order"), "https://image.tmdb.org/t/p/w300"+cast.getString("profile_path"));

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
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq);


    }

    private void rate()  {

        StringRequest strReq = new StringRequest(Request.Method.POST,"https://api.themoviedb.org/3/movie/"+Context.ITEM_TV_SHOW.getId()+"/rating?guest_session_id="+session_id+"&api_key=7c408d3e3e9aec97d01604333744b592", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Rate Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getContext(),jObj.getString("status_message"), Toast.LENGTH_LONG).show();
                    addRated();

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
                String s=String.valueOf(txtratevalue.getRating());
                params.put("value", s);


                return params;
            }

        };

        // Adding request to request queue
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq);



    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getRated() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsongetRated, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (!response.getBoolean("error")) {
                        JSONObject result = (JSONObject) response.get("result");
                        if (result.get("id_rated") instanceof JSONArray)
                        {
                            dataSource = new ArrayList<>();
                            dataSource1 = new ArrayList<>();
                            JSONArray ids_rated = (JSONArray) result.get("id_rated");
                            JSONArray values = (JSONArray) result.get("value");
                            for (int i = 0; i < ids_rated.length(); i++) {
                                String idrated = (String) ids_rated.get(i);
                                dataSource.add(idrated);
                                dataSource1.add((String) values.get(i));
                            }
                        }
                        else
                        {
                            dataSource = new ArrayList<>();
                            dataSource1 = new ArrayList<>();
                            String id_rated = (String) result.get("id_rated");
                            String value = (String) result.get("value");
                            dataSource.add(id_rated);
                            dataSource1.add(value);
                        }

                        if (dataSource.contains(String.valueOf(Context.ITEM_MOVIE.getId()))) {
                            txtratevalue.setVisibility(View.GONE);
                            btrate.setVisibility(View.GONE);
                            star.setVisibility(View.VISIBLE);
                            rating.setVisibility(View.VISIBLE);
                            int pos=dataSource.indexOf(String.valueOf(Context.ITEM_MOVIE.getId()));
                            rating.setText("Your Rating : "+dataSource1.get(pos));
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
//                Toast.makeText(getContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                hideDialog();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq, "hello");


    }
    void addRated(){
        StringRequest strReq = new StringRequest(Request.Method.GET,"http://idol-design.com/Cinecasa/Scripts/AddRated.php?uid_user="+Context.CURRENT_USER.getUid()+"&id_rated="+String.valueOf((Context.ITEM_TV_SHOW.getId()))+"&type=tv&value="+String.valueOf(txtratevalue.getRating()*2), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Rate Response: " + response.toString());
                hideDialog();



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Rate Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) ;

        // Adding request to request queue

        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq);




    }
}
