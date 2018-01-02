package esprit.tn.cinecasa.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.DragLayout;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener {
    private ImageView imageView;
    private View address1, address2, address3, address4;
    TextView address5, rate;
    private RatingBar ratingBar;
    private View head1, head2, head3, head4;
    private String imageUrl;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);
        Glide
                .with(rootView.getContext())
                .load(getArguments().getString("poster"))
                .placeholder(R.drawable.ph)
                .into(imageView);
        address5 = (TextView) rootView.findViewById(R.id.address4);
        rate = (TextView) rootView.findViewById(R.id.rate_value);
        rate.setText(getArguments().getString("vote"));
        address5.setText(getArguments().getString("title"));
        ratingBar = (RatingBar) dragLayout.findViewById(R.id.rating);

        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
    }

    public void bindData(String imageUrl) {

        this.imageUrl = imageUrl;
    }
}
