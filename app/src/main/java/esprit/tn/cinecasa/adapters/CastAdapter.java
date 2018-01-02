package esprit.tn.cinecasa.adapters;

/**
 * Created by ahmed on 25-Nov-17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.yinglan.shadowimageview.ShadowImageView;

import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Cast;
import esprit.tn.cinecasa.utils.AutoResizeTextView;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder> {

    private Context mContext;
    private List<Cast> castList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AutoResizeTextView title, character;
        public ShadowImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (AutoResizeTextView) view.findViewById(R.id.title);
            character = (AutoResizeTextView) view.findViewById(R.id.character);
            thumbnail = (ShadowImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    esprit.tn.cinecasa.utils.Context.ACTOR_ID = castList.get(position ).getId();
                    esprit.tn.cinecasa.utils.Context.selected = 2;
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    startActivityNoAnimation(intent);
                }
            });
        }

        public void startActivityNoAnimation(Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mContext.startActivity(intent);
        }
    }


    public CastAdapter(Context mContext, List<Cast> castList) {
        this.mContext = mContext;
        this.castList = castList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cast_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.title.setText(cast.getName());
        holder.character.setText(cast.getCharacter());

        // loading album cover using Glide library

        Glide.with(mContext)
                .load(cast.getProfile_path())    // you can pass url too
                .asBitmap()
                .placeholder(R.drawable.celebph)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // you can do something with loaded bitmap here

                        holder.thumbnail.setImageBitmap(resource);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }
}
