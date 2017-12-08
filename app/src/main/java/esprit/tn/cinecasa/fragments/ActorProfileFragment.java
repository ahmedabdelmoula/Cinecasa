package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
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
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;


public class ActorProfileFragment extends Fragment {

    View view;
    Button removeFavorite;
    private ProgressDialog pDialog;
    private static String TAG = ActorProfileFragment.class.getSimpleName();

    public ActorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank, container, false);

        TextView actorName = (TextView) view.findViewById(R.id.actorName);
        TextView birthday = (TextView) view.findViewById(R.id.birthday);
        TextView place = (TextView) view.findViewById(R.id.place);
        TextView biography = (TextView) view.findViewById(R.id.biography);
        removeFavorite = (Button) view.findViewById(R.id.remove_favorite);
        pDialog = new ProgressDialog(getActivity());

        try {
            actorName.setText(getArguments().getString("name"));
            birthday.setText("Birthday : " + getArguments().getString("birthday"));
            place.setText("Place or Birth : " + getArguments().getString("place_of_birth"));
            biography.setText("Biography : " + getArguments().getString("biography"));
        } catch (NullPointerException e) {
            actorName.setText("None");
        }

        removeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFavorite("http://idol-design.com/Cinecasa/Scripts/DeleteFavorite.php?id_user="+ Context.CONNECTED_USER.getId()+"&id_act="+getArguments().getInt("id"));
            }
        });

        return view;
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
