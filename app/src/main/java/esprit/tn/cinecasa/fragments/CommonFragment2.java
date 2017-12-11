package esprit.tn.cinecasa.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.utils.DragLayout;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment2 extends Fragment implements DragLayout.GotoDetailListener {
    private ImageView imageView;
    private View address1, address2, address3, address4;
    TextView address5;
    private RatingBar ratingBar;
    private View head1, head2, head3, head4;
    private String imageUrl;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_common2, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.imagee);
        Glide
                .with(rootView.getContext())
                .load(getArguments().getString("poster"))
                .into(imageView);
        address5 = (TextView) rootView.findViewById(R.id.address44);
        address5.setText(getArguments().getString("title"));
        ratingBar = (RatingBar) dragLayout.findViewById(R.id.rating);
        ratingBar.setRating(getArguments().getFloat("vote"));

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
