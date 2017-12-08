package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Actor;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.AutoResizeTextView;
import esprit.tn.cinecasa.utils.CircleTransform;
import esprit.tn.cinecasa.utils.Context;

/**
 * Created by Yessine on 12/3/2017.
 */

public class ActorDetailsFragment extends Fragment {

    View view;
    private Actor actor;
    private ProgressDialog pDialog;
    private static String TAG = ActorDetailsFragment.class.getSimpleName();
    private AutoResizeTextView actorName;
    private Button favoriteButton;
    private CircularImageView actorImage;
    private boolean backImage;
    private RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_actor_detais, container, false);

        pDialog = new ProgressDialog(getActivity());


        actorName = (AutoResizeTextView) view.findViewById(R.id.actor_name);
        favoriteButton = (Button) view.findViewById(R.id.add_favorite);
        actorImage = (CircularImageView) view.findViewById(R.id.actor_image);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.background);

        getActorDetails();

        return view;
    }


    private void getActorDetails() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://api.themoviedb.org/3/person/" + Context.ACTOR_ID + "?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US",
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response

                    actor = new Actor(response.getInt("id"),
                            response.getString("birthday"),
                            response.getString("deathday"),
                            response.getInt("gender"),
                            response.getString("biography"),
                            response.getDouble("popularity"),
                            response.getString("place_of_birth"),
                            "https://image.tmdb.org/t/p/w300/" + response.getString("profile_path"),
                            response.getBoolean("adult"),
                            response.getString("imdb_id"),
                            response.getString("homepage"),
                            response.getString("name"));

                    getFav();

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void getFav() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://idol-design.com/Cinecasa/Scripts/SelectActorsByUserId.php?id_user=1",// + Context.CONNECTED_USER.getId(),
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("favorite");

                    List<String> dataSource = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject actorId = (JSONObject) results.get(i);

                        dataSource.add(actorId.getString("id_actor"));

                    }
//                    Resources res = getResources();
//                    LayerDrawable layerDrawable = (LayerDrawable) res.getDrawable(R.drawable.backgroundd);
//                    BitmapDrawable newDrawable = (BitmapDrawable ) res.getDrawable(R.drawable.bebe_pp);
//                    layerDrawable.setDrawableByLayerId(R.id.bitmap, getResources().getDrawable( R.drawable.bebe_pp ));
//                    relativeLayout.setBackground(layerDrawable);
                    try {
                        actorName.setText(actor.getName());
                        Picasso
                                .with(getContext())
                                .load(actor.getImage())
                                .into(actorImage);

                    } catch (NullPointerException e) {
                        actorName.setText("None");
                    }

                    if (!dataSource.contains(Context.ACTOR_ID + "")) {
                        favoriteButton.setBackgroundResource(R.drawable.ic_favorite_empty_64dp);
                        backImage = false;
                    } else {
                        favoriteButton.setBackgroundResource(R.drawable.ic_favorite_full_64dp);
                        backImage = true;
                    }

                    favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url;
                            if (backImage) {
                                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_empty_64dp);
                                url = "http://idol-design.com/Cinecasa/Scripts/DeleteFavorite.php?id_user=" + Context.CONNECTED_USER.getId() + "&id_act=" + actor.getId();
                                swapFavorite(url);
                                backImage = !backImage;
                            } else {
                                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_full_64dp);
                                url = "http://idol-design.com/Cinecasa/Scripts/AddFavorite.php?id_user=" + Context.CONNECTED_USER.getId() + "&id_act=" + actor.getId();
                                swapFavorite(url);
                                backImage = !backImage;
                            }


                        }
                    });

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                hidepDialog();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void swapFavorite(String url) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        jsonObjReq.setShouldCache(false);
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

}