package com.project.binbinfu.the_city;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class My_activity extends Activity {
    private static ImageAdapter adapter ;
    private static My_activity_class my_activity;
    private static Bundle bundle;
    private static String email;
    private static String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity);
        final ActionBar actionBar = getActionBar();
        final TextView title = (TextView)findViewById(R.id.my_activity_action);
        actionBar.setDisplayHomeAsUpEnabled(true);
        bundle = this.getIntent().getExtras();
        email = bundle.getString("email");
        action = bundle.getString("action");
        Log.v("my_binbin",email);
        if (action.equals("0"))
            title.setText("My Like");
        else if (action.equals("1"))
            title.setText("My Take");
        else if (action.equals("2"))
            title.setText("My Post");
        Log.v("my_binbin","hello");
        //Fetch Data
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;
        try {
            jsonObject.put("email", email);
            jsonObject.put("action",action);
            entity = new StringEntity(jsonObject.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            Log.v("ann", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.v("ann", e.getMessage());
        }
        Log.v("ann","before_send");
        client.post(My_activity.this, "http://the-city.appspot.com/api/android_person_activity", entity, "application/json", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.v("ann", "onSuccess");
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    String str = response.toString();
                    Log.v("ann", str);
                    my_activity = gson.fromJson(str, My_activity_class.class);
                    if(adapter == null)
                        adapter = new ImageAdapter(My_activity.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),
                                new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                    adapter.covers.clear();
                    adapter.ids.clear();
                    adapter.titles.clear();
                    adapter.starts.clear();
                    adapter.ends.clear();
                    adapter.locations.clear();
                    adapter.types.clear();
                    if (!my_activity.ids.isEmpty()) {
                        Log.v("ann", "ongoing_not_empty");
                        adapter.covers.addAll(my_activity.covers);
                        adapter.ids.addAll(my_activity.ids);
                        adapter.titles.addAll(my_activity.titles);
                        adapter.starts.addAll(my_activity.starts);
                        adapter.ends.addAll(my_activity.ends);
                        adapter.locations.addAll(my_activity.locations);
                        adapter.types.addAll(my_activity.types);
                    }
                    adapter.notifyDataSetChanged();
                    final ExpandableGridView gridview = (ExpandableGridView)findViewById(R.id.grid_my_activity);
                    gridview.setAdapter(adapter);
                } catch (Exception ex) {
                    Log.v("ann", "Failed to parse JSON due to :" + ex);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // Log.v("ann",response.toString());
                Log.v("ann", "fail to request");
                Log.v("ann", Long.toString(statusCode));

            }
        });
        final ExpandableGridView gridview = (ExpandableGridView)findViewById(R.id.grid_my_activity);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", Long.toString(id));
                bundle.putString("email", email);
                Intent intent = new Intent(My_activity.this, Activity_one.class);
                intent.putExtras(bundle);
                My_activity.this.startActivity(intent);
                //Toast.makeText(rootView.getContext(), "" + id, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
