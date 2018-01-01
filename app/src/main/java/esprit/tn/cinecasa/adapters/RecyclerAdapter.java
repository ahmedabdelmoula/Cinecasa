package esprit.tn.cinecasa.adapters;

/**
 * Created by Shade on 5/9/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Movie;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Movie> data;
    private Context con;

    public RecyclerAdapter(List<Movie> data){
        this.data = data;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public RoundedImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (RoundedImageView) itemView.findViewById(R.id.item_imagee);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    esprit.tn.cinecasa.utils.Context.ITEM_MOVIE = data.get(position);
                    esprit.tn.cinecasa.utils.Context.selected = 0;
                    Intent intent = new Intent(con, DetailsActivity.class);
                    startActivityNoAnimation(intent);


                }
            });
        }

        public void startActivityNoAnimation(Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            con.startActivity(intent);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layoutt, viewGroup, false);
        con = viewGroup.getContext();

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Glide
                .with(con)
                .load(data.get(i).getPoster_path())
                .asBitmap()
                .placeholder(R.drawable.ph)
                .skipMemoryCache(true)
                .into(viewHolder.itemImage);
//        Glide
//                .with(con)
//                .load("https://image.tmdb.org/t/p/w300/oSLd5GYGsiGgzDPKTwQh7wamO8t.jpg")
//                .into(viewHolder.itemImage);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Movie> images) {
        data = images;
    }
}