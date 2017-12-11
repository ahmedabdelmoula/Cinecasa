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
import esprit.tn.cinecasa.entities.Movie;

/**
 * Created by ahmed on 26-Nov-17.
 */

public class CardMovieAdapter extends RecyclerView.Adapter<CardMovieAdapter.MyViewHolder> {

private Context mContext;
private List<Movie> movieList;

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


                esprit.tn.cinecasa.utils.Context.ITEM_MOVIE = movieList.get(position);
                esprit.tn.cinecasa.utils.Context.selected = 0;
                Intent intent = new Intent(mContext, DetailsActivity.class);
                startActivityNoAnimation(intent);


            }
        });
    }
}


    public CardMovieAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public CardMovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cmovie_item, parent, false);
        return new CardMovieAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardMovieAdapter.MyViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());

        // loading album cover using Glide library
        Glide.with(mContext).load(movie.getBackdrop_path())
                .asBitmap()
                .skipMemoryCache( true )
                .into(holder.thumbnail);
    }


    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
