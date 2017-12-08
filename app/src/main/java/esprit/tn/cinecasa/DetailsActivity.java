package esprit.tn.cinecasa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import esprit.tn.cinecasa.fragments.ActorDetailsFragment;
import esprit.tn.cinecasa.fragments.LoopViewPagerFragment;
import esprit.tn.cinecasa.fragments.MovieDetailsFragment;
import esprit.tn.cinecasa.fragments.TVShowDetailsFragment;
import esprit.tn.cinecasa.fragments.TVShowsFragment;
import esprit.tn.cinecasa.utils.Context;

/**
 * Created by ahmed on 02-Dec-17.
 */

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_details);
        //getSupportActionBar().hide();


//        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, new MovieDetailsFragment(), "MovieDetailsFragment").commit();

        Fragment fragment;
        if (Context.selected == 1) {
            fragment = Fragment.instantiate(DetailsActivity.this, TVShowDetailsFragment.class.getName());
        } else  if (Context.selected == 0){
            fragment = Fragment.instantiate(DetailsActivity.this, MovieDetailsFragment.class.getName());
        } else {
            fragment = Fragment.instantiate(DetailsActivity.this, ActorDetailsFragment.class.getName());
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.container, fragment).disallowAddToBackStack();
        fragmentTransaction.commit();

    }
}
