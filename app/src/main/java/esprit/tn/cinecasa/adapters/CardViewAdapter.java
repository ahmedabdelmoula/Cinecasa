package esprit.tn.cinecasa.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import esprit.tn.cinecasa.R;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Yessine on 11/20/2017.
 */
public class CardViewAdapter extends ArrayAdapter<String> {

    int resource ;
    List<String> doubleCards;

    public CardViewAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);

        this.resource = resource;
        this.doubleCards = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String current = doubleCards.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);

//        CardView cardView = (CardView) convertView.findViewById(R.id.carddemo_Gplay1);
//        CardView cardView1 = (CardView) convertView.findViewById(R.id.carddemo_Gplay2);

        Card card = new Card(getContext());

        ImageView image = (ImageView) convertView.findViewById(R.id.testImage);

        Glide
                .with(getContext())
                .load(current)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d("IMAGE_EXCEPTION","Sometimes the image is not loaded and this text is not displayed");
                        return false;
                    }
                })
                .into(image);
//        cardView.setCard(current.getFirst());
//        cardView1.setCard(current.getSecond());

        return convertView;
    }
}
