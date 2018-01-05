package esprit.tn.cinecasa.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

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

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.CustPagerTransformer;

/**
 * Created by Yessine on 11/21/2017.
 */

public class CombinedCrediDetails2 extends Fragment {

    private View view;
    private TextView currentPage, allPages;
    private ViewPager viewPager;
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private final String[] imageArray = {"assets://image1.jpg", "assets://image2.jpg", "assets://image3.jpg", "assets://image4.jpg", "assets://image5.jpg"};

    private String urlJsonObj;
    private ProgressDialog pDialog;
    private static String TAG = CombinedCrediDetails2.class.getSimpleName();
    private String mediaType;
    private int id;
    List<Movie> dataSource;
    List<TVShow> dataSourcee;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_2, container, false);

        currentPage = (TextView) view.findViewById(R.id.current_pageee);
        allPages = (TextView) view.findViewById(R.id.all_pagesss);
        viewPager = (ViewPager) view.findViewById(R.id.viewpagerrr);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        mediaType = getArguments().getString("mediaType");
        id = getArguments().getInt("id");

        animation();

        urlJsonObj = "https://api.themoviedb.org/3/person/" + id + "/tv_credits?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeJsonObjectRequest();
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

                    JSONArray results = response.getJSONArray("cast");

                    dataSourcee = new ArrayList<>();


                    for (int i = 0; i < results.length() - 1; i++) {

                        JSONObject movie = (JSONObject) results.get(i);

                        int id = movie.getInt("id");
                        Double vote_average = movie.getDouble("vote_average");
                        String title = movie.getString("name");
                        String poster_path = ("https://image.tmdb.org/t/p/w300" + movie.getString("poster_path"));
                        final TVShow tv = new TVShow();
                        tv.setName(title);
                        tv.setVote_average(vote_average);
                        tv.setPoster_path(poster_path);
                        tv.setId(id);
                        dataSourcee.add(tv);

                        Bundle b = new Bundle();
                        b.putInt("id", id);
                        b.putString("vote", vote_average + "");
                        b.putString("title", title);
                        b.putString("poster", poster_path);

                        CommonFragment frag = new CommonFragment();
                        frag.setArguments(b);

                        fragments.add(frag);
                    }

                    viewPager.setPageTransformer(false, new CustPagerTransformer(getContext()));

                    viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            CommonFragment fragment = fragments.get(position);
                            return fragment;
                        }

                        @Override
                        public int getCount() {
                            return fragments.size();
                        }
                    });


                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            updateIndicatorTv();
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    updateIndicatorTv();

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
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void updateIndicatorTv() {
        int totalNum = fragments.size();
        int currentItem;
        if (dataSourcee.isEmpty())
            currentItem = 0;
        else
            currentItem = viewPager.getCurrentItem() + 1;
        currentPage.setText(currentItem+"");
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(currentPage,"translationX",-25,0),
                ObjectAnimator.ofFloat(currentPage,"alpha",0,1));
        animatorSet.setDuration(250);
        animatorSet.start();

        allPages.setText("/" + totalNum);
    }

    public void animation(){

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(currentPage,"translationX",-25,0),
                ObjectAnimator.ofFloat(currentPage,"alpha",0,1),
                ObjectAnimator.ofFloat(allPages,"translationX",25,0),
                ObjectAnimator.ofFloat(allPages,"alpha",0,1),
                ObjectAnimator.ofFloat(viewPager,"alpha",0,1)
        );
        animatorSet.setDuration(400);
        animatorSet.start();
    }
}
