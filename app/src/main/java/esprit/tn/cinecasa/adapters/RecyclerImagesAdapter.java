package esprit.tn.cinecasa.adapters;

/**
 * Created by Shade on 5/9/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Movie;

public class RecyclerImagesAdapter extends RecyclerView.Adapter<RecyclerImagesAdapter.ViewHolder> {

    static List<Movie> data;
    private Context con;

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.image_actor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    final Dialog dialog = new Dialog(con);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog);
                    // set the custom dialog components - text, image and button
                    ImageView img = (ImageView) dialog.findViewById(R.id.dialog_img);

                    String url = data.get(position).getPoster_path().replace("w150" , "w1000");

                    Glide
                            .with(con)
                            .load(url)
                            .asBitmap()
                            .skipMemoryCache(true)
                            .into(img);

                    // Close Button
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            //TODO Close button action
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layoutt, viewGroup, false);
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
                .skipMemoryCache(true)
                .into(viewHolder.itemImage);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static void setData(List<Movie> images) {
        data = images;
    }
}