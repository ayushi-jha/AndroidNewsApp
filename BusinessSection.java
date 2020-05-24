package com.example.newsapp.ui.home;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class BusinessSection extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public LinearLayout linlaHeaderProgress;
    String[] articles;
    String[] articles_sect;
    String[] thumbnails;
    String[] ids;
    String[] urls;
    String[] dates;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callingAPI();
            }
        });
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.fHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        callingAPI();
        return rootView;
    }
    public void callingAPI() {
        String url = "Path-to-backend/business_guardian";
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    public void dosomeparsing(List<String> articles_list, List<String> articles_sect_list, List<String> date_list, List<String> thumbnail_list, List<String> id_list, List<String> url_list) {
        articles = articles_list.toArray(new String[0]);
        articles_sect = articles_sect_list.toArray(new String[0]);
        thumbnails = thumbnail_list.toArray(new String[0]);
        ids = id_list.toArray(new String[0]);
        urls = url_list.toArray(new String[0]);
        dates = date_list.toArray(new String[0]);
        mAdapter = new MyAdapter(articles, articles_sect, dates, thumbnails, ids, urls, getContext());
        recyclerView.setAdapter(mAdapter);
        try {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
