package com.project.binbinfu.the_city;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class Setting extends Activity {
    private Bundle bundle;
    private String email;
    private String status;
    private static ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Button logout = (Button)findViewById(R.id.logout);
        final Button notification = (Button)findViewById(R.id.notification);
        bundle = this.getIntent().getExtras();
        email = bundle.getString("email");
        //Fetch data
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;
        try {
            jsonObject.put("email",email);
            jsonObject.put("action","0");
            entity = new StringEntity(jsonObject.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(Setting.this,"http://the-city.appspot.com/api/device",entity,"application/json",new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    status = response.getString("status");
                    if (status.equals("on"))
                        notification.setText("Cancel Notification");
                    else
                        notification.setText("Request Notification");
                } catch (Exception ex) {
                    Log.e("Hello", "Failed to parse JSON due to: " + ex);
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse) {
                Log.i("error", "fail to request");
                Log.v("geo","fail");
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                //Fetch data
                pDialog = ProgressDialog.show(v.getContext(), "Please wait", "Requesting");
                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonObject = new JSONObject();
                StringEntity entity = null;
                try {
                    jsonObject.put("email",email);
                    jsonObject.put("action","1");
                    entity = new StringEntity(jsonObject.toString());
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                client.post(Setting.this,"http://the-city.appspot.com/api/device",entity,"application/json",new JsonHttpResponseHandler(){
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            pDialog.dismiss();
                            status = response.getString("status");
                            if (status.equals("on"))
                                notification.setText("Cancel Notification");
                            else
                                notification.setText("Request Notification");
                        } catch (Exception ex) {
                            Log.e("Hello", "Failed to parse JSON due to: " + ex);
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse) {
                        Log.i("error", "fail to request");
                        Log.v("geo","fail");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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
