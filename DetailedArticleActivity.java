package com.example.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DetailedArticleActivity extends AppCompatActivity {
    public ImageView imageView;
    public TextView titleView, sectView, dateView, articleView, urlView;
    public static final String mypreference = "mypref";
    public String sp_id;
    public String sp;
    public String sp_title;
    public LinearLayout linlaHeaderProgress;
    public String tweet_url;
    public Menu detail_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        imageView = findViewById(R.id.detail_img);
        titleView = findViewById(R.id.detail_title);
        sectView = findViewById(R.id.detail_sect);
        dateView = findViewById(R.id.detail_date);
        articleView = findViewById(R.id.detail_article);
        urlView = findViewById(R.id.detail_url);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        sp_id = intent.getStringExtra("Param");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        String url = "Path-to-backend/article_guardian?param1="+sp_id;
        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String json_resp = response.toString();
                    JSONObject jsonObject = new JSONObject(json_resp);
                    JSONObject article_det = jsonObject.getJSONObject("response").getJSONObject("content");
                    String title = article_det.getString("webTitle");
                    sp_title = title;
                    tweet_url = article_det.getString("webUrl");
                    String news_sect = article_det.getString("sectionName");
                    String thumbnail = "";
                    try {
                        thumbnail = article_det.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements").getJSONObject(0).getJSONArray("assets").getJSONObject(0).getString("file");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                    }
                    String detailed_article = "";
                    JSONArray body_parts = article_det.getJSONObject("blocks").getJSONArray("body");
                    for (int idx = 0; idx < body_parts.length(); idx ++)
                    {
                        detailed_article += body_parts.getJSONObject(idx).getString("bodyHtml");
                    }
                    String date = article_det.getString("webPublicationDate");
                    String web_id = article_det.getString("id");
                    String web_url = article_det.getString("webUrl");
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                    LocalDate dateTime = LocalDate.parse(date, inputFormatter);
                    sp = title + ";" + news_sect + ";" + date + ";" + thumbnail + ";" + tweet_url;
                    DateTimeFormatter f2 = DateTimeFormatter.ofPattern( "dd MMM YYYY" , Locale.ENGLISH) ;
                    String date_show = dateTime.format(f2) ;
                    dosomeparsing(title, news_sect, thumbnail, date_show, web_url, detailed_article);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
                Log.d("Error", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        detail_menu = menu;
        SharedPreferences preferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        if (preferences.contains(sp_id)) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_bookmark));
        }
        else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_bookmark_border));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_bookmark) {
            // Do something
            SharedPreferences preferences = getSharedPreferences(mypreference, MODE_PRIVATE);
            if (preferences.contains(sp_id)) {
                detail_menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_bookmark_border));
                SharedPreferences.Editor editor = getSharedPreferences(mypreference, MODE_PRIVATE).edit();
                editor.remove(sp_id);
                editor.commit();
                String toastMessage = "'" + sp_title + "' was removed from bookmarks";
                Toast.makeText(this, toastMessage,
                        Toast.LENGTH_LONG).show();
            }
            else {
                detail_menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_bookmark));
                SharedPreferences.Editor editor = getSharedPreferences(mypreference, MODE_PRIVATE).edit();
                editor.putString(sp_id, sp);
                editor.commit();
                String toastMessage = "'" + sp_title + "' was added to bookmarks";
                Toast.makeText(this, toastMessage,
                        Toast.LENGTH_LONG).show();
            }

            return true;
        }
        if (id == R.id.detail_twitter) {
            String twitter_url = "";
            try {
                twitter_url = "https://twitter.com/intent/tweet?text="+ URLEncoder.encode("Check out this Link: ", "UTF-8")+"&url="+URLEncoder.encode(tweet_url, "UTF-8")+"&hashtags=CSCI571NewsSearch";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dosomeparsing(String title, String news_sect, String thumbnail, String date, String web_url, String detailed_article) {
        titleView.setText(title);
        sectView.setText(news_sect);
        dateView.setText(date);
        sp_title = title;
        setTitle(title);
        String url = "<a href='" + web_url + "'>View Full Article</a>";
        urlView.setText(Html.fromHtml(url, Html.FROM_HTML_MODE_LEGACY));
        articleView.setText(Html.fromHtml(detailed_article, Html.FROM_HTML_MODE_LEGACY));
        urlView.setMovementMethod(LinkMovementMethod.getInstance());
        articleView.setMovementMethod(LinkMovementMethod.getInstance());
        Glide.with(this)
                .load(thumbnail)
                .into(imageView);
        linlaHeaderProgress.setVisibility(View.GONE);
    }

}
