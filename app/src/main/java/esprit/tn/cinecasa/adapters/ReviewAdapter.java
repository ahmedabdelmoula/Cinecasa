package esprit.tn.cinecasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Review;

/**
 * Created by ahmed on 28-Nov-17.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    int ressource;
    List<Review> movies;
    public static Context cxt;
    public ReviewAdapter(Context context, int resource, List<Review> objects) {

        super(context, resource, objects);

        this.ressource = resource;
        this.movies = objects;
        cxt=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review currentMovie = movies.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(ressource, parent, false);
        CircleImageView profilePicture = (CircleImageView) convertView.findViewById(R.id.civProfilePicrev);
        TextView firstLetter = (TextView) convertView.findViewById(R.id.first_letter);
        TextView auth = (TextView) convertView.findViewById(R.id.reviewAuthor);
        TextView cont = (TextView) convertView.findViewById(R.id.Reviewcontent);
        firstLetter.setText(currentMovie.getAuthor().substring(0,1).toUpperCase());
        auth.setText(currentMovie.getAuthor());
        cont.setText(currentMovie.getContent());

        return convertView;
    }



}