package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.AutoResizeTextView;
import esprit.tn.cinecasa.utils.Context;


public class ActorProfileFragment extends Fragment {

    View view;
    Button removeFavorite, moreDetails;
    private ProgressDialog pDialog;
    private static String TAG = ActorProfileFragment.class.getSimpleName();

    public ActorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank, container, false);

        AutoResizeTextView actorName = (AutoResizeTextView) view.findViewById(R.id.actorName);
        AutoResizeTextView birthday = (AutoResizeTextView) view.findViewById(R.id.birthday);
        AutoResizeTextView place = (AutoResizeTextView) view.findViewById(R.id.place);
        TextView biography = (TextView) view.findViewById(R.id.biography);
        removeFavorite = (Button) view.findViewById(R.id.remove_favorite);
        moreDetails = (Button) view.findViewById(R.id.more_details);
        pDialog = new ProgressDialog(getActivity());

        try {
            actorName.setText(getArguments().getString("name"));
            birthday.setText(getArguments().getString("birthday"));
            place.setText(getArguments().getString("place_of_birth"));
            biography.setText(getArguments().getString("biography"));
        } catch (NullPointerException e) {
            actorName.setText("None");
        }

        removeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFavorite("http://idol-design.com/Cinecasa/Scripts/DeleteFavorite.php?id_user=" + Context.CONNECTED_USER.getId() + "&id_act=" + getArguments().getInt("id"));
            }
        });

        moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                esprit.tn.cinecasa.utils.Context.ACTOR_ID = getArguments().getInt("id");
                esprit.tn.cinecasa.utils.Context.selected = 2;
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                startActivityNoAnimation(intent);
            }
        });

        return view;
    }

    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getContext().startActivity(intent);
    }

    private void deleteFavorite(String url) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidepDialog();
                Context.MAIN_ACTIVITY.refreshFavorite();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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
