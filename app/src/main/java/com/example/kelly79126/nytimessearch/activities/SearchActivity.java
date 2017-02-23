package com.example.kelly79126.nytimessearch.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.kelly79126.nytimessearch.R;
import com.example.kelly79126.nytimessearch.adapters.ArticleArrayAdapter;
import com.example.kelly79126.nytimessearch.dialogfragments.FilterDialogFragment;
import com.example.kelly79126.nytimessearch.dialogfragments.NoInternetDialogFragment;
import com.example.kelly79126.nytimessearch.models.Article;
import com.example.kelly79126.nytimessearch.widget.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener{

    @BindView(R.id.etQuery) EditText etQuery;
    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.btnSearch) Button btnSearch;
    String mStrBeginDate = null;
    String mStrSortOder = null;
    String mStrNewsDesk = "";
    int mIntPage = 0;
    private boolean mbLoading = true;


    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
//                i.putExtra("article", Parcels.wrap(article));
//                startActivity(i);
                openChromeCustom(article);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                Log.d("FilterDialogFragment", ""+mbLoading);
                return mbLoading; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        checkInternetAvailable();
    }

    public void openChromeCustom(Article article){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());

        int requestCode = 100;

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // Map the bitmap, text, and pending intent to this icon
        // Set tint to be true so it matches the toolbar color
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(article.getWebUrl()));
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkInternetAvailable();
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        mIntPage = offset;
        queryUrl(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance("Filter");
        filterDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishFilterDialog(String beginDate, String sortOrder, String newsdesk) {
        mStrBeginDate = beginDate;
        mStrSortOder = sortOrder;
        mStrNewsDesk = newsdesk;
        mIntPage = 0;
        queryUrl(true);
    }


    public void onArticleSearch(View view) {
        mIntPage = 0;
        queryUrl(true);
    }

    private void queryUrl(final boolean bClear){
        String query = etQuery.getText().toString();
        RequestParams params = new RequestParams();
        params.put("api-key", "66f3d0fab5b84ac8ab49cf2cd61b9c34");
        params.put("page", mIntPage);
        if(!query.isEmpty())
            params.put("q", query);
        if(mStrSortOder != null)
            params.put("sort", mStrSortOder);
        if(mStrBeginDate != null)
            params.put("begin_date", mStrBeginDate);
        if(!mStrNewsDesk.isEmpty())
            params.put("fq", mStrNewsDesk);


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        Log.d("FilterDialogFragment", params.toString());

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    if(bClear) {
                        adapter.clear();
                    }
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("FilterDialogFragment", articleJsonResults.toString());
                    Log.d("FilterDialogFragment", "" + articleJsonResults.length());

                    if(articleJsonResults.length() > 0) {
                        mbLoading = true;
                    }else {
                        mbLoading = false;
                    }

                    adapter.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void checkInternetAvailable(){
        if(false == isNetworkAvailable() || false == isOnline()){
            FragmentManager fm = getSupportFragmentManager();
            NoInternetDialogFragment alertDialog = NoInternetDialogFragment.newInstance("Internet is not available");
            alertDialog.show(fm, "fragment_alert");
        }
    }
}
