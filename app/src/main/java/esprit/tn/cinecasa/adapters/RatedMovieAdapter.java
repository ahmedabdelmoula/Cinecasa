package esprit.tn.cinecasa.adapters;

/**
 * Created by ahmed on 25-Nov-17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.fragments.MovieDetailsFragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class RatedMovieAdapter extends RecyclerView.Adapter<RatedMovieAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
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

    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    public RatedMovieAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rated_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // loading album cover using Glide library
        Glide.with(mContext).load(movie.getPoster_path()).placeholder(R.drawable.ph).into(holder.thumbnail);


    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
