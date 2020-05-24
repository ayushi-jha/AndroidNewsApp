package com.example.newsapp.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;

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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.List;
import java.util.Locale;

import static androidx.core.content.ContextCompat.getSystemService;


public class HomeFragment extends Fragment implements LocationListener{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private HomeViewModel homeViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout weatherView;
    private CardView weatherCardView;
    private TextView cityView, stateView, tempView, tempdescView;
    private ImageView weatherImgView;
    public LinearLayout linlaHeaderProgress;
    String[] articles;
    String[] articles_sect, dates;
    String[] thumbnails;
    String [] ids;
    String mCity;
    String mState;
    String mTemp, mTempdesc;
    String[] urls;
    Double mLat, mLon;
    LocationManager locationManager;
    String provider;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        settingLoc();
                    }

                }
            }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        checkLocationPermission();
        recyclerView.setHasFixedSize(true);
        weatherView = (LinearLayout) rootView.findViewById(R.id.home_weather_card);
        weatherCardView = (CardView) rootView.findViewById(R.id.weather_cardView);
        weatherImgView = (ImageView) rootView.findViewById(R.id.weather_img);
        cityView = (TextView) rootView.findViewById(R.id.weather_city);
        stateView = (TextView) rootView.findViewById(R.id.weather_state);
        tempView = (TextView) rootView.findViewById(R.id.temp);
        tempdescView = (TextView) rootView.findViewById(R.id.temp_desc);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.fHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // Location wasn't found, check the next most accurate place for the current location
        if (location == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = locationManager.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            location = locationManager.getLastKnownLocation(provider);
        }
        mLat = location.getLatitude();
        mLon = location.getLongitude();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callingAPI();
            }
        });
        callingAPI();
        return rootView;
    }
    public void settingLoc() {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            // Lat: 34.02, Lon: -118.28
            addresses = geocoder.getFromLocation(mLat, mLon, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCity = addresses.get(0).getLocality();
        mState = addresses.get(0).getAdminArea();
        String url_weather = "https://api.openweathermap.org/data/2.5/weather?q="+mCity+"&units=metric&appid=XXXX";
        JsonObjectRequest weatherRequest = new JsonObjectRequest(url_weather, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String json_resp = response.toString();
                    JSONObject jsonObject = new JSONObject(json_resp);
                    JSONArray weather_array = jsonObject.getJSONArray("weather");
                    JSONObject summary_obj = weather_array.getJSONObject(0);
                    String summary = summary_obj.getString("main").toString();
                    String temp = jsonObject.getJSONObject("main").getString("temp").toString();
                    set_weather_card(summary, temp);
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
        requestQueue.add(weatherRequest);
    }
    public void callingAPI() {
        String url = "Path-to-backend/latest_guardian";
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
                        String id = webtitle.getString("id");
                        JSONObject fields = webtitle.getJSONObject("fields");
                        String thumbnail = "";
                        try {
                            thumbnail = fields.getString("thumbnail");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }
                        String time = webtitle.getString("webPublicationDate");
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
        weatherCardView.setVisibility(View.VISIBLE);
    }
    public void set_weather_card(String summary, String temp) {
        mTemp = temp + "\u2103";
        mTempdesc = summary;
        cityView.setText(mCity);
        stateView.setText(mState);
        tempView.setText(mTemp);
        tempdescView.setText(mTempdesc);
        String bg_url = "";
        switch (summary)
        {
            case "Clouds": bg_url = "https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg"; break;
            case "Clear": bg_url = "https://csci571.com/hw/hw9/images/android/clear_weather.jpg"; break;
            case "Snow": bg_url =  "https://csci571.com/hw/hw9/images/android/snowy_weather.jpeg"; break;
            case "Rain":
            case "Drizzle": bg_url = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg"; break;
            case "Thunderstorm": bg_url = "https://csci571.com/hw/hw9/images/android/thunder_weather.jpg"; break;
            default: bg_url = "https://csci571.com/hw/hw9/images/android/sunny_weather.jpg";
        }
        Glide.with(getContext())
                .load(bg_url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        weatherImgView.setImageDrawable(resource);
//                        weatherImgView.setBackground(resource);
//                        weatherView.setBackground(resource);
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        mLat = location.getLatitude();
        mLon = location.getLongitude();
        Log.d("Locationlat", mLat.toString());
        settingLoc();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
