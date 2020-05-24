package com.example.newsapp.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.MyViewHolder> {
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
    private EventListener mListener;

    public interface EventListener {
        public void onEvent();
    }


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
    public BookmarksAdapter(String[] myDataset, String[] myDatasect, String[] myDatatime, String[] myImage, String [] ids, String[] urls, Context context, EventListener listener) {
        mDataset = myDataset;
        mDatasetsect = myDatasect;
        mDatatime = myDatatime;
        mThumbnail = myImage;
        mContext = context;
        mIds = ids;
        mUrls = urls;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookmarksAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmarks_text_view, parent, false);
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
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        LocalDate dateTime = LocalDate.parse(time, inputFormatter);
        DateTimeFormatter f = DateTimeFormatter.ofPattern( "dd MMM" , Locale.ENGLISH) ;
        String date_new = dateTime.format(f) + "  <font color=#6402f0 size=24sp><b>|</b></font> ";
        holder.dateView.setText(Html.fromHtml(date_new, Html.FROM_HTML_MODE_LEGACY));
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
                final ImageButton dialog_twitter_button = (ImageButton) dialog.findViewById(R.id.dialog_twitter);
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
                            List<String> remove_list = new ArrayList<String>(Arrays.asList(mDataset));
                            remove_list.remove(position);
                            mDataset = remove_list.toArray(new String[0]);
                            List<String> remove_list1 = new ArrayList<String>(Arrays.asList(mDatasetsect));
                            remove_list1.remove(position);
                            mDatasetsect = remove_list1.toArray(new String[0]);
                            List<String> remove_list2 = new ArrayList<String>(Arrays.asList(mIds));
                            remove_list2.remove(position);
                            mIds = remove_list2.toArray(new String[0]);
                            List<String> remove_list3 = new ArrayList<String>(Arrays.asList(mThumbnail));
                            remove_list3.remove(position);
                            mThumbnail = remove_list3.toArray(new String[0]);
                            List<String> remove_list4 = new ArrayList<String>(Arrays.asList(mUrls));
                            remove_list4.remove(position);
                            mUrls = remove_list4.toArray(new String[0]);
                            List<String> remove_list5 = new ArrayList<String>(Arrays.asList(mDatatime));
                            remove_list5.remove(position);
                            mDatatime = remove_list5.toArray(new String[0]);
                            notifyDataSetChanged();
                            if (remove_list.size() <= 0)
                            {
                                mListener.onEvent();
                            }
//                            notifyItemRangeChanged(position, remove_list.size());
                            dialog.cancel();
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
                    List<String> remove_list = new ArrayList<String>(Arrays.asList(mDataset));
                    remove_list.remove(position);
                    mDataset = remove_list.toArray(new String[0]);
                    List<String> remove_list1 = new ArrayList<String>(Arrays.asList(mDatasetsect));
                    remove_list1.remove(position);
                    mDatasetsect = remove_list1.toArray(new String[0]);
                    List<String> remove_list2 = new ArrayList<String>(Arrays.asList(mIds));
                    remove_list2.remove(position);
                    mIds = remove_list2.toArray(new String[0]);
                    List<String> remove_list3 = new ArrayList<String>(Arrays.asList(mThumbnail));
                    remove_list3.remove(position);
                    mThumbnail = remove_list3.toArray(new String[0]);
                    List<String> remove_list4 = new ArrayList<String>(Arrays.asList(mUrls));
                    remove_list4.remove(position);
                    mUrls = remove_list4.toArray(new String[0]);
                    List<String> remove_list5 = new ArrayList<String>(Arrays.asList(mDatatime));
                    remove_list5.remove(position);
                    mDatatime = remove_list5.toArray(new String[0]);
                    notifyDataSetChanged();
                    if (remove_list.size() <= 0)
                    {
                        mListener.onEvent();
                    }
//                    notifyItemRangeChanged(position, remove_list.size());
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
