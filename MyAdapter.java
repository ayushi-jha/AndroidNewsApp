package com.example.newsapp.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.DetailedArticleActivity;
import com.example.newsapp.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] mDataset;
    private String[] mDatasetsect;
    private String[] mDatatime;
    private String[] mThumbnail;
    private String[] mIds;
    private String[] mUrls;
    private Context mContext;
    public static final String mypreference = "mypref";
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView textView;
        public TextView sectView, dateView;
        public ImageView imageView;
        public ImageButton imageButton;
        public MyViewHolder(final View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view_news);
            textView = (TextView) v.findViewById(R.id.info_text);
            sectView = (TextView) v.findViewById(R.id.latest_news_section);
            dateView = (TextView) v.findViewById(R.id.latest_date);
            imageView = (ImageView) v.findViewById(R.id.image_news);
            imageButton = (ImageButton) v.findViewById(R.id.image_button_adapter);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset, String[] myDatasect, String[] myDatatime, String[] myImage, String [] ids, String[] urls, Context context) {
        mDataset = myDataset;
        mDatasetsect = myDatasect;
        mDatatime = myDatatime;
        mThumbnail = myImage;
        mContext = context;
        mIds = ids;
        mUrls = urls;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder((CardView) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset[position]);
        holder.sectView.setText(mDatasetsect[position]);
        String time = mDatatime[position];
        ZonedDateTime dateTime = ZonedDateTime.parse(time);
        LocalDateTime ldt = LocalDateTime.now();            //Local date time
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );        //Zone information
        ZonedDateTime curr_time = ldt.atZone(zoneId);     //Local time in Asia timezone
        String time_text = "";
        Duration duration = Duration.between(curr_time, dateTime);
        long seconds = Math.abs(duration.getSeconds());
        long days = seconds / 86400;
        long hours = seconds / 3600;
        seconds -= (hours * 3600);
        long minutes = seconds / 60;
        seconds -= (minutes * 60);
        if (days > 0)
        {
            time_text += days + "d";
        }
        else if (hours > 0)
        {
            time_text += hours + "h";
        }
        else if (minutes > 0)
        {
            time_text += minutes + "m";
        }
        else
        {
            time_text += seconds + "s";
        }
        time_text += " ago <font color=#6402f0 size=24sp><b>|</b></font> ";
        holder.dateView.setText(Html.fromHtml(time_text, Html.FROM_HTML_MODE_LEGACY));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cardviewclass", view.toString());
                Intent myIntent = new Intent(view.getContext(), DetailedArticleActivity.class);
                myIntent.putExtra("Param",mIds[position]);
                view.getContext().startActivity(myIntent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setTitle("Title...");
                dialog.setContentView(R.layout.custom_dialog);
                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
                text.setText(mDataset[position]);
                ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
                final ImageButton dialog_bookmark_button = (ImageButton) dialog.findViewById(R.id.dialog_bookmark);
                ImageButton dialog_twitter_button = (ImageButton) dialog.findViewById(R.id.dialog_twitter);
                dialog_twitter_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String twitter_url = "";
                        try {
                            twitter_url = "https://twitter.com/intent/tweet?text="+ URLEncoder.encode("Check out this Link: ", "UTF-8")+"&url="+URLEncoder.encode(mUrls[position], "UTF-8")+"&hashtags=CSCI571NewsSearch";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                        v.getContext().startActivity(browserIntent);
                        dialog.cancel();
                    }

                });
                SharedPreferences preferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                dialog_bookmark_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("dialogviewclass", v.toString());
                        SharedPreferences preferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                        if (preferences.contains(mIds[position])) {
                            SharedPreferences.Editor editor = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE).edit();
                            editor.remove(mIds[position]);
                            editor.commit();
                            dialog_bookmark_button.setImageResource(R.drawable.icon_bookmark_border);
                            holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark_border);
                            String toastMessage = "'" + mDataset[position] + "' was removed from bookmarks";
                            Toast.makeText(mContext, toastMessage,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            SharedPreferences.Editor editor = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE).edit();
                            String sp = mDataset[position] + ";" + mDatasetsect[position] + ";" + mDatatime[position] + ";" + mThumbnail[position] + ";" + mUrls[position];
                            editor.putString(mIds[position], sp);
                            editor.commit();
                            dialog_bookmark_button.setImageResource(R.drawable.icon_bookmark);
                            holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark);
                            String toastMessage = "'" + mDataset[position] + "' was added to bookmarks";
                            Toast.makeText(mContext, toastMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });
                if (preferences.contains(mIds[position])) {
                    dialog_bookmark_button.setImageResource(R.drawable.icon_bookmark);
                    holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark);
                } else {
                    dialog_bookmark_button.setImageResource(R.drawable.icon_bookmark_border);
                    holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark_border);
                }
                Glide.with(mContext).load(mThumbnail[position])
                        .into(dialog_image);
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();

                return true;
            }
        });
        SharedPreferences preferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (preferences.contains(mIds[position])) {
            holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark);
        } else {
            holder.imageButton.setBackgroundResource(R.drawable.icon_bookmark_border);
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("iamgeviewclass", v.toString());
                SharedPreferences preferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                if (preferences.contains(mIds[position])) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE).edit();
                    editor.remove(mIds[position]);
                    editor.commit();
                    v.setBackgroundResource(R.drawable.icon_bookmark_border);
                    String toastMessage = "'" + mDataset[position] + "' was removed from bookmarks";
                    Toast.makeText(mContext, toastMessage,
                            Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE).edit();
                    String sp = mDataset[position] + ";" + mDatasetsect[position] + ";" + mDatatime[position] + ";" + mThumbnail[position] + ";" + mUrls[position];
                    editor.putString(mIds[position], sp);
                    editor.commit();
                    v.setBackgroundResource(R.drawable.icon_bookmark);
                    String toastMessage = "'" + mDataset[position] + "' was added to bookmarks";
                    Toast.makeText(mContext, toastMessage,
                            Toast.LENGTH_LONG).show();
                }
            }

        });
        Glide.with(mContext).load(mThumbnail[position])
                .into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
