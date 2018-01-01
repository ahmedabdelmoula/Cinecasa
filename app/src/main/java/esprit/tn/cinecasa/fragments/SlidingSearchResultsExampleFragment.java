package esprit.tn.cinecasa.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.adapters.RecyclerAdapter;
import esprit.tn.cinecasa.entities.Actor;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.MultiSearch;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.SearchResultsListAdapter;


public class SlidingSearchResultsExampleFragment extends BaseExampleFragment {
    private final String TAG = "BlankFragment";
    private ProgressDialog pDialog;
    private List<MultiSearch> searchResult;

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private FloatingSearchView mSearchView;

    private RecyclerView mSearchResultsList;
    private SearchResultsListAdapter mSearchResultsAdapter;

    private boolean mIsDarkSearchTheme = false;

    private String mLastQuery = "";
    private boolean created = true;
    private View v;
    private static boolean CURRENT_TYPE = true;

    public SlidingSearchResultsExampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        searchResult = new ArrayList<>();

        if (esprit.tn.cinecasa.utils.Context.SELECTED_TYPE == CURRENT_TYPE) {
            FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerScroll, new GenresMoviesFragment(), "GenresMoviesFragment").commitAllowingStateLoss();

        } else {
            FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerScroll, new GenresTVShowsFragment(), "GenresTVShowsFragment").commitAllowingStateLoss();

        }

        return inflater.inflate(R.layout.fragment_sliding_search_results_example_fragment, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        v = view;
        mSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        mSearchResultsList = (RecyclerView) view.findViewById(R.id.search_results_list);
        setupFloatingSearch();
        setupResultsList();
        setupDrawer();
    }

    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    mSearchView.clearSuggestions();

                    String urlJsonObj = "https://api.themoviedb.org/3/search/multi?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&include_adult=false&query=";

                    makeJsonObjectRequest(urlJsonObj + newQuery.replace(" ", "%20"));
                    mSearchView.showProgress();

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.

                    //simulates a query call to a data source
                    //with a new query.
//                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
//                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {
//
//                                @Override
//                                public void onResults(List<MultiSearch> results) {
//
//                                    //this will swap the data and
//                                    //render the collapse/expand animations as necessary
//                                    mSearchView.swapSuggestions(results);
////
//                                    //let the users know that the background
//                                    //process has completed
//                                    mSearchView.hideProgress();
//                                }
//                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {


                MultiSearch a = (MultiSearch) searchSuggestion;
                switch (a.getMediaType()) {
                    case "person":
                        esprit.tn.cinecasa.utils.Context.ACTOR_ID = a.getId();
                        esprit.tn.cinecasa.utils.Context.selected = 2;
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        startActivityNoAnimation(intent);
                        break;
                    case "tv":
                        getTVShow("https://api.themoviedb.org/3/tv/" + a.getId() + "?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
                        break;
                    default:
                        getMovie("https://api.themoviedb.org/3/movie/" + a.getId() + "?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
                        break;
                }


                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                String urlJsonObj = "https://api.themoviedb.org/3/search/multi?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&include_adult=false&query=";
                query = query.replace(" ", "%20");
                mSearchView.showProgress();
                mSearchView.clearSuggestions();
                makeJsonObjectRequest(urlJsonObj + query);

//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<MultiSearch> results) {
//                                mSearchResultsAdapter.swapData(results);
//                            }
//
//                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
//                mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);
                hideKeyboardFrom(getContext(), v);
                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
//
//                if (item.getItemId() == R.id.action_change_colors) {
//
//                    mIsDarkSearchTheme = true;
//
//                    //demonstrate setting colors for items
//                    mSearchView.setBackgroundColor(Color.parseColor("#787878"));
//                    mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
//                    mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
//                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
//                } else {
//
//                    //just print action
//                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
//                            Toast.LENGTH_SHORT).show();
//                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                MultiSearch multiSearch = (MultiSearch) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

//                if (colorSuggestion.getIsHistory()) {
//                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                            R.drawable.maleficent_31, null));
//
////                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
////                    leftIcon.setAlpha(1f);
//                } else {
//                    leftIcon.setAlpha(0.0f);
//                    leftIcon.setImageDrawable(null);
//                }

//                textView.setTextColor(Color.parseColor(textColor));
                String text = multiSearch.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));

                Glide
                        .with(getContext())
                        .load("https://image.tmdb.org/t/p/w75" + multiSearch.getImage())
                        .into(leftIcon);
//                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                        R.drawable.maleficent_31, null));
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                mSearchView.clearSuggestions();

                Log.d(TAG, "onClearSearchClicked()");
            }
        });
    }

    private void setupResultsList() {
        mSearchResultsAdapter = new SearchResultsListAdapter();
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public boolean onActivityBackPress() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
//        if (!mSearchView.setSearchFocused(false)) {
//            return false;
//        }
        return true;
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }

    private void makeJsonObjectRequest(String url) {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("results");

                    searchResult.clear();
                    for (int i = 0; i < results.length(); i++) {

                        JSONObject multiSrch = (JSONObject) results.get(i);

                        MultiSearch m;


                        if (multiSrch.getString("media_type").equals("person")) {

                            m = new MultiSearch(multiSrch.getInt("id"),
                                    multiSrch.getString("name"),
                                    multiSrch.getString("media_type"),
                                    multiSrch.getString("profile_path"),
                                    null);

                        } else if (multiSrch.getString("media_type").equals("tv")) {

                            m = new MultiSearch(multiSrch.getInt("id"),
                                    multiSrch.getString("name"),
                                    multiSrch.getString("media_type"),
                                    multiSrch.getString("poster_path"),
                                    multiSrch.getString("first_air_date"));

                        } else {

                            m = new MultiSearch(multiSrch.getInt("id"),
                                    multiSrch.getString("title"),
                                    multiSrch.getString("media_type"),
                                    multiSrch.getString("poster_path"),
                                    multiSrch.getString("release_date"));

                        }

                        if (!m.getImage().isEmpty() && !m.getName().isEmpty())
                            searchResult.add(m);

                    }
                    mSearchView.swapSuggestions(searchResult);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                mSearchView.hideProgress();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                mSearchView.hideProgress();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getMovie(String url) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject movie) {
                Log.d(TAG, movie.toString());

                try {
                    String vote_count = movie.getString("vote_count");
                    int id = movie.getInt("id");
                    boolean video = movie.getBoolean("video");
                    Double vote_average = movie.getDouble("vote_average");
                    String title = movie.getString("title");
                    Double popularity = movie.getDouble("popularity");
                    String poster_path = ("https://image.tmdb.org/t/p/w150" + movie.getString("poster_path"));
                    String original_language = movie.getString("original_language");
                    String original_title = movie.getString("original_title");
                    String backdrop_path = "https://image.tmdb.org/t/p/w500" + movie.getString("backdrop_path");
                    boolean adult = movie.getBoolean("adult");
                    String overview = movie.getString("overview");
                    String release_date = movie.getString("release_date");

                    final Movie movie1 = new Movie(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, adult, null, overview, release_date);
                    movie1.setBackdrop_path(backdrop_path);

                    hidepDialog();

                    Context.ITEM_MOVIE = movie1;
                    esprit.tn.cinecasa.utils.Context.selected = 0;
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    startActivityNoAnimation(intent);

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
                hidepDialog();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getTVShow(String url) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject tvshow) {
                Log.d(TAG, tvshow.toString());

                try {

                    String vote_count = tvshow.getString("vote_count");
                    int id = tvshow.getInt("id");
                    Double vote_average = tvshow.getDouble("vote_average");
                    String name = tvshow.getString("name");
                    Double popularity = tvshow.getDouble("popularity");
                    String poster_path = "https://image.tmdb.org/t/p/w300" + tvshow.getString("poster_path");
                    String original_language = tvshow.getString("original_language");
                    String original_name = tvshow.getString("original_name");
                    String genre_ids = null;
                    String origin_country = tvshow.getString("origin_country");
                    String backdrop_path = "https://image.tmdb.org/t/p/w500" + tvshow.getString("backdrop_path");
                    String overview = tvshow.getString("overview");
                    String first_air_date = tvshow.getString("first_air_date");

                    final TVShow tvshow1 = new TVShow(genre_ids,
                            original_name,
                            name,
                            popularity,
                            origin_country,
                            vote_count,
                            first_air_date,
                            backdrop_path,
                            original_language,
                            id,
                            vote_average,
                            overview,
                            poster_path);
                    tvshow1.setOverview(overview);
                    tvshow1.setPoster_path(poster_path);
                    tvshow1.setBackdrop_path(backdrop_path);


                    esprit.tn.cinecasa.utils.Context.ITEM_TV_SHOW = tvshow1;
                    esprit.tn.cinecasa.utils.Context.selected = 1;
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    startActivityNoAnimation(intent);

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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public static void hideKeyboardFrom(android.content.Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
