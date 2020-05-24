package com.example.newsapp.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class BookmarksFragment extends Fragment implements BookmarksAdapter.EventListener{
    private RecyclerView recyclerView;
    private TextView emptyView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String[] articles;
    String[] articles_sect;
    String[] thumbnails;
    String[] ids;
    String[] urls;
    String[] dates;
    public static final String mypreference = "mypref";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.bookmark_recycler_view);
        emptyView = (TextView) rootView.findViewById(R.id.no_bm);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SharedPreferences prefs = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        List<String> article_list = new ArrayList<>();
        List<String> article_sect_list = new ArrayList<>();
        List<String> thumbnail_list = new ArrayList<>();
        List<String> id_list = new ArrayList<>();
        List<String> url_list = new ArrayList<>();
        List<String> date_list = new ArrayList<>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            String id = entry.getKey();
            String val = entry.getValue().toString();
            List<String> list = new ArrayList<String>(Arrays.asList(val.split(";")));
            Log.d("News", val);
            String title = list.get(0);
            String news_sect = list.get(1);
            String date = list.get(2);
            String thumbnail;
            try {
                thumbnail = list.get(3);
            } catch (Exception e) {
                e.printStackTrace();
                thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
            }
            String full_url = list.get(4);
            article_list.add(title);
            article_sect_list.add(news_sect);
            thumbnail_list.add(thumbnail);
            id_list.add(id);
            url_list.add(full_url);
            date_list.add(date);
        }
        dosomeparsing(article_list, article_sect_list, date_list, thumbnail_list, id_list, url_list);

        return rootView;
    }
    public void onEvent() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    public void dosomeparsing(List<String> articles_list, List<String> articles_sect_list, List<String> date_list, List<String> thumbnail_list, List<String> id_list, List<String> url_list) {
        articles = articles_list.toArray(new String[0]);
        articles_sect = articles_sect_list.toArray(new String[0]);
        thumbnails = thumbnail_list.toArray(new String[0]);
        ids = id_list.toArray(new String[0]);
        urls = url_list.toArray(new String[0]);
        dates = date_list.toArray(new String[0]);
        if (articles_list.size() <= 0)
        {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            mAdapter = new BookmarksAdapter(articles, articles_sect, dates, thumbnails, ids, urls, getContext(), this);
            recyclerView.setAdapter(mAdapter);
            try {
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        List<String> article_list = new ArrayList<>();
        List<String> article_sect_list = new ArrayList<>();
        List<String> thumbnail_list = new ArrayList<>();
        List<String> id_list = new ArrayList<>();
        List<String> url_list = new ArrayList<>();
        List<String> date_list = new ArrayList<>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            String id = entry.getKey();
            String val = entry.getValue().toString();
            List<String> list = new ArrayList<String>(Arrays.asList(val.split(";")));
            Log.d("News", val);
            String title = list.get(0);
            String news_sect = list.get(1);
            String date = list.get(2);
            String thumbnail;
            try {
                thumbnail = list.get(3);
            } catch (Exception e) {
                e.printStackTrace();
                thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
            }
            String full_url = list.get(4);
            article_list.add(title);
            article_sect_list.add(news_sect);
            thumbnail_list.add(thumbnail);
            id_list.add(id);
            url_list.add(full_url);
            date_list.add(date);
        }
        dosomeparsing(article_list, article_sect_list, date_list, thumbnail_list, id_list, url_list);
    }

}
