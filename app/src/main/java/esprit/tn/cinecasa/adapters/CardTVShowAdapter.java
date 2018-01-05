package esprit.tn.cinecasa.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.TVShow;

/**
 * Created by ahmed on 26-Nov-17.
 */

public class CardTVShowAdapter extends RecyclerView.Adapter<CardTVShowAdapter.MyViewHolder> {

    private Context mContext;
    private List<TVShow> tvshowList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    esprit.tn.cinecasa.utils.Context.ITEM_TV_SHOW = tvshowList.get(position);
                    esprit.tn.cinecasa.utils.Context.selected = 1;
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    startActivityNoAnimation(intent);


                }
            });
        }
    }
    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    public CardTVShowAdapter(Context mContext, List<TVShow> tvshowList) {
        this.mContext = mContext;
        this.tvshowList = tvshowList;
    }

    @Override
    public CardTVShowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cmovie_item, parent, false);
        return new CardTVShowAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardTVShowAdapter.MyViewHolder holder, int position) {
        TVShow tvShow = tvshowList.get(position);
        holder.title.setText(tvShow.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(tvShow.getBackdrop_path()).placeholder(R.drawable.phbig).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return tvshowList.size();
    }


}
