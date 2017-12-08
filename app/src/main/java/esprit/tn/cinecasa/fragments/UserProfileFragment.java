package esprit.tn.cinecasa.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.SessionManager;

/**
 * Created by ahmed on 03-Dec-17.
 */

public class UserProfileFragment extends Fragment {
    View view;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // SqLite database handler
        db = new SQLiteHandler(getContext());

        // session manager
        session = new SessionManager(getContext());
        View photoHeader = view.findViewById(R.id.photoHeader);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }
        TextView txtname = (TextView)view.findViewById(R.id.tvName);
        txtname.setText(Context.CURRENT_USER.getName());
        ImageView overflow = (ImageView)view.findViewById(R.id.button_overflow);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        return view;
    }

    private void logoutUser() {
        session.setLogin(false,Context.CURRENT_USER.getUid());

        db.deleteUsers();

        // Launching the register activity
        Intent i = new Intent(getContext(), RegisterActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
