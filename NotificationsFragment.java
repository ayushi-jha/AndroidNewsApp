package com.example.newsapp.ui.notifications;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private LineChart lineChart;
    private String chart_search_text = "Coronavirus";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_search_chart);
        final EditText edittext = (EditText) root.findViewById(R.id.search_chart_input);
        lineChart = (LineChart) root.findViewById(R.id.lineChart);
        getJSONdata();
        edittext.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press edittext.getText()
                    chart_search_text = edittext.getText().toString();
                    getJSONdata();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND)) {
                    chart_search_text = edittext.getText().toString();
                    getJSONdata();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return root;
    }
    private void getJSONdata() {
        String url = "Path-to-backend/trends?param1=";
        if (chart_search_text.equals(""))
        {
            url += "Coronavirus";
        }
        else {
            url += chart_search_text;
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String json_resp = response.toString();
                    JSONObject jsonObject = new JSONObject(json_resp);
                    JSONArray results_array = jsonObject.getJSONObject("default").getJSONArray("timelineData");
                    //
                    List<Integer> article_list = new ArrayList<>();
                    for (int i = 0; i < results_array.length(); i++) {
                        JSONObject webtitle = results_array.getJSONObject(i);
                        String newsre = webtitle.getString("value").toString();
                        newsre = newsre.substring(1, newsre.length()-1);
                        article_list.add(Integer.parseInt(newsre));
                    }
                    getData(article_list);
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
    private void getData(List<Integer> value_list) {
        ArrayList<Entry> entries = new ArrayList<>();
        int idx = 0;
        for (Integer element : value_list) {
            entries.add(new Entry(idx, element));
            idx += 1;
        }
        String newstr = "Trending chart for " + chart_search_text;
        LineDataSet lineDataSet = new LineDataSet(entries, newstr);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.my_purple));
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.my_purple));
        lineDataSet.setCircleHoleColor(ContextCompat.getColor(getContext(), R.color.my_purple));
        lineDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.my_purple));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setGranularity(1f);
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setGranularity(1f);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setEnabled(false);
        xAxis.setDrawGridLines(false);
        yAxisRight.setDrawGridLines(false);
        yAxisLeft.setDrawGridLines(false);
        Legend l = lineChart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        LineData data = new LineData(lineDataSet);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
}

