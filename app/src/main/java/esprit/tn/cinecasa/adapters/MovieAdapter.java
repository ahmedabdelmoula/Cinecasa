package esprit.tn.cinecasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Movie;

/**
 * Created by esprit on 10/10/2017.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    int ressource;
    List<Movie> movies;
    public static Context cxt;
    public MovieAdapter(Context context, int resource, List<Movie> objects) {

        super(context, resource, objects);

        this.ressource = resource;
        this.movies = objects;
        cxt=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie currentMovie = movies.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(ressource, parent, false);
        TextView tvmovie = (TextView) convertView.findViewById(R.id.name_movie);
        ImageView ivmovie=(ImageView) convertView.findViewById(R.id.image_movie);

        tvmovie.setText(currentMovie.getTitle());
        Picasso.with(cxt).load(currentMovie.getPoster_path()).into(ivmovie);

        return convertView;
    }



}
