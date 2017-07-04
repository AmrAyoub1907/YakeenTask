package com.amrayoub.yakeentask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    RadioGroup radioGroup;
    Spinner mSpinner;
    ProgressDialog progressDialog;
    RecycleviewAdapter recycleviewAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String mSection="";
    Boolean choice=true;
    String jsonCache="";
    RecyclerView rv;
    String APIkey ="679b6dea260f449ab2359023aa53d00c";
    String format="json";
    ArrayList<TopSories> mTopstories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        initRV();
        initSpinner();
        initradiogroup();
        initRefresher();
    }

    private void initRefresher() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isNetworkAvailable()){
                    radioGroup.setVisibility(View.GONE);
                    SharedPreferences editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String s = editor.getString(mSection,"");
                    parseoffline(s);
                }else{
                    radioGroup.setVisibility(View.VISIBLE);
                    createURL(mSection);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void initradiogroup(){
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioAsynctask) {
                   choice = true;
                } else if(checkedId == R.id.radioVolley) {
                    choice = false;
                }
            }
        });
    }
    private void initSpinner() {
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Sections, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSection = String.valueOf(mSpinner.getSelectedItemPosition());
    }
    private void initRV() {
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(mSection,jsonCache);
        editor.commit();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSection = parent.getSelectedItem().toString();
        if(!isNetworkAvailable()){
            radioGroup.setVisibility(View.GONE);
            SharedPreferences editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String s = editor.getString(mSection,"");
            parseoffline(s);
        }else{
            radioGroup.setVisibility(View.VISIBLE);
            createURL(mSection);
        }
        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public void createURL(String section){
        Toast.makeText(this,mSection,Toast.LENGTH_SHORT).show();
        String url = "https://api.nytimes.com/svc/topstories/v2/"+section+"."+format+"?api-key="+APIkey;
        if(choice)
            new NYasyncTask().execute(url);/*Using AsyncTask*/
        else
            volleyJsonObjectRequest(url);/*Using Volley*/

    }
    public void volleyJsonObjectRequest(String url){
        mTopstories.clear();
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        jsonCache = response.toString();
                        try {
                            JSONArray resultArray = response.getJSONArray("results");
                            for (int i = 0; i < resultArray.length(); i++) {
                                TopSories topSories = new TopSories();
                                JSONObject theObject = resultArray.getJSONObject(i);
                                String JsonTitleItem = theObject.getString("title");

                                JSONArray multimedia = theObject.getJSONArray("multimedia");
                                JSONObject photoObj = multimedia.getJSONObject(0);
                                String JsonUrlItem = photoObj.getString("url");

                                String JsondateItem =theObject.getString("published_date");
                                topSories = new TopSories(JsonTitleItem,JsondateItem,JsonUrlItem);
                                mTopstories.add(topSories);
                            }
                            progressDialog.dismiss();
                            recycleviewAdapter =  new RecycleviewAdapter(mTopstories);
                            //recycleviewAdapter.notifyDataSetChanged();
                            rv.setAdapter(recycleviewAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e("Error Data", String.valueOf(mTopstories.size()));
            }
        });
        // Adding JsonObject request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,"");
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching The Data ... Volley");
        progressDialog.show();
    }
    public String getJsonAsyncTask(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return data;
    }
    private class NYasyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mTopstories.clear();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching the data ... Asynctask");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... url) {
            try{
                String JsonAsync = getJsonAsyncTask(url[0]);
                jsonCache = JsonAsync;
                JSONObject response = new JSONObject(JsonAsync);
                JSONArray resultArray = response.getJSONArray("results");
                for (int i = 0; i < resultArray.length(); i++) {
                    TopSories topSories = new TopSories();
                    JSONObject theObject = resultArray.getJSONObject(i);
                    String JsonTitleItem = theObject.getString("title");

                    JSONArray multimedia = theObject.getJSONArray("multimedia");
                    JSONObject photoObj = multimedia.getJSONObject(0);
                    String JsonUrlItem = photoObj.getString("url");

                    String JsondateItem = theObject.getString("published_date");
                    topSories = new TopSories(JsonTitleItem, JsondateItem, JsonUrlItem);
                    mTopstories.add(topSories);
                }
            }catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

                progressDialog.dismiss();
                recycleviewAdapter = new RecycleviewAdapter(mTopstories);
            return null;
        }
        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            rv.setAdapter(recycleviewAdapter);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void parseoffline(String response){
        mTopstories.clear();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching the data ... Offline ");
        progressDialog.show();
        try{
        JSONObject jsonObject = new JSONObject(response);
        JSONArray resultArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            TopSories topSories = new TopSories();
            JSONObject theObject = resultArray.getJSONObject(i);
            String JsonTitleItem = theObject.getString("title");

            JSONArray multimedia = theObject.getJSONArray("multimedia");
            JSONObject photoObj = multimedia.getJSONObject(0);
            String JsonUrlItem = photoObj.getString("url");

            String JsondateItem = theObject.getString("published_date");
            topSories = new TopSories(JsonTitleItem, JsondateItem, JsonUrlItem);
            mTopstories.add(topSories);}
        }
        catch (Exception e) {
            Log.d("Offline Task", e.toString());
        }
        progressDialog.dismiss();
        recycleviewAdapter = new RecycleviewAdapter(mTopstories);
        rv.setAdapter(recycleviewAdapter);
    }
}