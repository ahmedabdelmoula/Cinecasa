package esprit.tn.cinecasa.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import esprit.tn.cinecasa.R;

@SuppressLint("ValidFragment")
public class SimpleCardFragment extends Fragment {
    private String mTitle;
    TextView card_title_tv, bio;

    public static SimpleCardFragment getInstance(String title) {
        SimpleCardFragment sf = new SimpleCardFragment();
        if (title.equals(""))
            sf.mTitle = "There's Nothing to Show";
        else
            sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_simple_card, null);
        card_title_tv = (TextView) v.findViewById(R.id.card_title_tv);
        bio = (TextView) v.findViewById(R.id.biography);
        card_title_tv.setText(mTitle);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(bio, "translationX", -25, 0),
                ObjectAnimator.ofFloat(bio, "alpha", 0, 1),
                ObjectAnimator.ofFloat(card_title_tv, "alpha", 0, 1));
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    public void animation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(bio, "translationX", -25, 0),
                ObjectAnimator.ofFloat(bio, "alpha", 0, 1),
                ObjectAnimator.ofFloat(card_title_tv, "alpha", 0, 1));
        animatorSet.setDuration(1000);
        animatorSet.start();
    }
}