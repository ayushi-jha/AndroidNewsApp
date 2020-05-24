package com.example.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.ui.home.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    public TextView textView;
    public RecyclerView recyclerView;
    public static final String mypreference = "mypref";
    public String sp_id;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public LinearLayout linlaHeaderProgress;
    public String sp;
    public MyAdapter mAdapter;
    public Menu detail_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.sHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callingAPI();
            }
        });

        Intent intent = getIntent();
        sp_id = intent.getStringExtra("Param");
        String search_title = "Search Results for " + sp_id;
        setTitle(search_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        callingAPI();
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void callingAPI() {
        String url = "Path-to-backend/search_guardian?param1="+sp_id;
        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String json_resp = response.toString();
                    JSONObject jsonObject = new JSONObject(json_resp);
                    JSONArray results_array = jsonObject.getJSONObject("response").getJSONArray("results");
                    //
                    List<String> article_list = new ArrayList<>();
                    List<String> article_sect_list = new ArrayList<>();
                    List<String> thumbnail_list = new ArrayList<>();
                    List<String> id_list = new ArrayList<>();
                    List<String> url_list = new ArrayList<>();
                    List<String> date_list = new ArrayList<>();
                    for (int i = 0; i < results_array.length(); i++) {
                        JSONObject webtitle = results_array.getJSONObject(i);
                        String newsre = webtitle.getString("webTitle");
                        String full_url = webtitle.getString("webUrl");
                        String news_sect = webtitle.getString("sectionName");
                        String thumbnail = "";
                        try {
                            thumbnail = webtitle.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements").getJSONObject(0).getJSONArray("assets").getJSONObject(0).getString("file");
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            continue;
                        }
                        String time = webtitle.getString("webPublicationDate");
                        String id = webtitle.getString("id");
                        article_list.add(newsre);
                        article_sect_list.add(news_sect);
                        date_list.add(time);
                        thumbnail_list.add(thumbnail);
                        id_list.add(id);
                        url_list.add(full_url);
                    }
                    dosomeparsing(article_list, article_sect_list, date_list, thumbnail_list, id_list, url_list);
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
    public void dosomeparsing(List<String> articles_list, List<String> articles_sect_list, List<String> date_list, List<String> thumbnail_list, List<String> id_list, List<String> url_list) {
        String[] articles = articles_list.toArray(new String[0]);
        String[] articles_sect = articles_sect_list.toArray(new String[0]);
        String[] thumbnails = thumbnail_list.toArray(new String[0]);
        String[] ids = id_list.toArray(new String[0]);
        String[] urls = url_list.toArray(new String[0]);
        String[] dates = date_list.toArray(new String[0]);
        mAdapter = new MyAdapter(articles, articles_sect, dates, thumbnails, ids, urls, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
        linlaHeaderProgress.setVisibility(View.GONE);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
