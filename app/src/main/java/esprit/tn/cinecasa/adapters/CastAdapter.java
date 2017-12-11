package esprit.tn.cinecasa.adapters;

/**
 * Created by ahmed on 25-Nov-17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.entities.Cast;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder> {

    private Context mContext;
    private List<Cast> castList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, character;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            character = (TextView) view.findViewById(R.id.character);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    esprit.tn.cinecasa.utils.Context.ACTOR_ID = castList.get(position).getId();
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
        holder.character.setText("As : "+cast.getCharacter());

        // loading album cover using Glide library
        Picasso.with(mContext).load(cast.getProfile_path()).into(holder.thumbnail);

       /** holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showPopupMenu(holder.overflow);
            }
        });**/
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
   /** private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


     // Click listener for popup menu items

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }**/

    @Override
    public int getItemCount() {
        return castList.size();
    }
}
