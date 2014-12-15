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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.internal.is;
import com.google.android.gms.plus.model.people.Person;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class Profile extends Activity {

    private Bundle bundle;
    private static String email;
    private static Person_class person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final TextView nickname = (TextView)findViewById(R.id.nickname);
        final TextView interest = (TextView)findViewById(R.id.interest);
        final TextView introduce = (TextView)findViewById(R.id.introduce);
        final TextView gmail = (TextView)findViewById(R.id.gmail);
        final ImageView photo = (ImageView)findViewById(R.id.photo_person);
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.

        bundle = this.getIntent().getExtras();
        email = bundle.getString("email");
        gmail.setText(email);
        //Fetch data
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;
        try {
            jsonObject.put("user_id", email);
            entity = new StringEntity(jsonObject.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            Log.v("ann", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.v("ann", e.getMessage());
        }
        Log.v("ann","before_send");
        client.post(Profile.this, "http://the-city.appspot.com/api/person", entity, "application/json", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    String str = response.toString();
                    person = gson.fromJson(str, Person_class.class);
                    if (!person.nick.isEmpty()){
                        nickname.setText(person.nick);
                    }
                    if(!person.interest.isEmpty()){
                        String interest_text="";
                        for (int i = 0; i<person.interest.size();i++){
                            interest_text += person.interest.get(i)+" ";
                        }
                        interest.setText(interest_text);
                    }
                    if(!person.introduce.isEmpty()){
                        introduce.setText(person.introduce);
                    }
                    Log.v("binbin_photo",person.photo);
                    if(!person.photo.isEmpty()){
                            Picasso.with(Profile.this)
                                    .load("http://the-city.appspot.com/img?key=" + person.photo)
                            //        .placeholder(R.drawable.ico_loading)
                                    .resize(200, 200)
                                    .into(photo);
                    }

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
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
