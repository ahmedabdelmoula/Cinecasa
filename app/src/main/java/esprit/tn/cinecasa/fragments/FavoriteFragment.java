package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.entities.Actor;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.CircleTransform;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.utils.Context;


/**
 * Created by Yessine on 11/25/2017.
 */

public class FavoriteFragment extends Fragment {

    private String urlJsonObj = "http://idol-design.com/Cinecasa/Scripts/SelectActorsByUserId.php?id_user=" + Context.CONNECTED_USER.getId();
    private ProgressDialog pDialog;
    private static String TAG = FavoriteFragment.class.getSimpleName();
    private View view;
    private List<Actor> actors;
    private List<Fragment> fragments, beforeFragments;
    private ViewPager pager;
    private SmartTabLayout tabs;
    private Adapter adapter;
    private SmartTabLayout.TabProvider tabProvider;
    private TextView nothingToShow;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.favorite_fragment, container, false);

        Context.FAV_FRAG = this;

        pager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (SmartTabLayout) view.findViewById(R.id.tabs);
        nothingToShow = (TextView) view.findViewById(R.id.nothing_to_show);


        fragments = new ArrayList<>();
        beforeFragments = new ArrayList<>();

        adapter = new Adapter(getChildFragmentManager(), fragments);
        pager.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        actors = new ArrayList<>();
        makeJsonObjectRequest();
        return view;
    }

    public void deleteFromFav() {
        int pos = pager.getCurrentItem();
        if (adapter.getCount() == 1) {

            CenterFabActivity c = (CenterFabActivity) getActivity();
            c.fullReloadFav();

        } else {
            fragments.remove(pos);
            beforeFragments.remove(pos);
            adapter.notifyDataSetChanged();
            actors.remove(pos);
            tabProvider = getTabProvider();
            tabs.setCustomTabView(tabProvider);
            tabs.setViewPager(pager);

            if (adapter.getCount() == pos + 1)
                pager.setCurrentItem(pos - 2, true);
            else
                pager.setCurrentItem(pos - 1, true);
        }
    }

    private static class Adapter extends FragmentStatePagerAdapter {

        private List<Fragment> data;

        public Adapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response

                    List<String> dataSource = new ArrayList<>();

                    switch (response.getString("type")) {

                        case "array":
                            nothingToShow.setVisibility(View.GONE);
                            JSONArray results = response.getJSONArray("favorite");

                            for (int i = 0; i < results.length(); i++) {

                                JSONObject actorId = (JSONObject) results.get(i);

                                dataSource.add(actorId.getString("id_actor"));

                            }
                            count = 0;

                            for (int i = 0; i < dataSource.size(); i++) {
                                makeMiniJsonObjectRequest(dataSource.get(i), i, dataSource.size());
                            }
                            break;


                        case "object":
                            nothingToShow.setVisibility(View.GONE);
                            JSONObject result = response.getJSONObject("favorite");

                            dataSource.add(result.getString("id_actor"));

                            count = 0;
                            makeMiniJsonObjectRequest(dataSource.get(0), 0, 1);
                            break;


                        default:
                            hidepDialog();
                            nothingToShow.setText("Nothing to Show");
                            break;
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
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                hidepDialog();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq, "hello");
    }

    private void makeMiniJsonObjectRequest(String idAct, final int pos, final int max) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://api.themoviedb.org/3/person/" + idAct + "?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US",
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response

                    String birthday;
                    String biography, placeOfBirth, profilePath, name;
                    Double popularity;

                    try {
                        biography = response.getString("biography");
                    } catch (Exception e) {
                        biography = "";
                    }
                    try {
                        birthday = response.getString("birthday");
                    } catch (Exception e) {
                        birthday = "Unknown";
                    }
                    try {
                        placeOfBirth = response.getString("place_of_birth");
                    } catch (Exception e) {
                        placeOfBirth = "Unknown";
                    }
                    try {
                        profilePath = response.getString("profile_path");
                    } catch (Exception e) {
                        profilePath = "";
                    }
                    try {
                        name = response.getString("name");
                    } catch (Exception e) {
                        name = " ";
                    }
                    try {
                        popularity = response.getDouble("popularity");
                    } catch (Exception e) {
                        popularity = 0.00000;
                    }
                    Actor actor = new Actor(response.getInt("id"),
                            birthday,
                            "None",
                            response.getInt("gender"),
                            biography,
                            popularity,
                            placeOfBirth,
                            "https://image.tmdb.org/t/p/w300/" + profilePath,
                            false,
                            "None",
                            "None",
                            name);

                    actors.add(actor);

                    Bundle b = new Bundle();
                    b.putInt("id", actor.getId());
                    b.putString("birthday", actor.getBirthday());
                    b.putString("deathday", actor.getDeathday());
                    b.putString("biography", actor.getBiography());
                    b.putString("place_of_birth", actor.getPlaceOfBirth());
                    b.putString("profile_path", actor.getImage());
                    b.putString("imdb_id", actor.getImdbId());
                    b.putString("homepage", actor.getHomePage());
                    b.putString("name", actor.getName());
                    b.putInt("id", actor.getId());
                    b.putInt("gender", actor.getGender());
                    b.putDouble("popularity", actor.getPopularity());
                    b.putBoolean("adult", actor.isAdult());

                    ActorProfileFragment actorProfileFragment = new ActorProfileFragment();
                    b.putInt("position", count);
                    actorProfileFragment.setArguments(b);
                    beforeFragments.add(actorProfileFragment);
                    count++;

                    if (count == max) {
                        for (Fragment f : beforeFragments)
                            fragments.add(f);
                        adapter.notifyDataSetChanged();
                        pDialog.hide();
                        tabProvider = getTabProvider();
                        tabs.setCustomTabView(tabProvider);
                        tabs.setViewPager(pager);
                    }

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
                hidepDialog();
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

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private SmartTabLayout.TabProvider getTabProvider() {
        return new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.tab, container, false);
                RoundedImageView roundedImg = (RoundedImageView) v.findViewById(R.id.item_image);
                Picasso
                        .with(getContext())
                        .load(actors.get(position).getImage())
                        .placeholder(R.drawable.celebph)
                        .transform(new CircleTransform())
                        .into(roundedImg);
                return v;
            }
        };
    }
}