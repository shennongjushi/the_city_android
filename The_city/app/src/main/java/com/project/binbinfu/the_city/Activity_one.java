package com.project.binbinfu.the_city;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class Activity_one extends Activity {

    private Bundle bundle;
    private static String activity_id;
    private static Activity_class activity = new Activity_class();
    private static TwoWayView mRecyclerView;
    private static Toast mToast;
    private static final int SELECT_PICTURE = 1;
    private static String selectedImagePath;
    private static Bitmap photo = null;
    private static ProgressDialog pDialog;
    private static ProgressDialog pDialog_like;
    private static ProgressDialog pDialog_take;
    private static String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_one);
        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Receive the parameters
        bundle = this.getIntent().getExtras();
        activity_id = bundle.getString("id");
        email = bundle.getString("email");
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_one, menu);
        return true;
    }






    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {
                    Log.v("upload","hello");
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = getPath(selectedImageUri);
                    Log.v("upload",selectedImagePath);
                    UploadImage();
                }
            }
        }
        /**
         * helper to retrieve the path of an image URI
         */
        public String getPath(Uri uri) {
            // just some safety built in
            if( uri == null ) {
                // TODO perform some logging or show user feedback
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            // this is our fallback here
            return uri.getPath();
        }
        private void UploadImage() {
            try {
                Log.v("upload","hello2");
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                final Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath,
                        options);
                Log.v("upload",Integer.toString(bitmap.getWidth()));
                Matrix matrix = new Matrix();
                // resize the bit map
                //matrix.postScale(1f, 1f);
                // rotate the Bitmap
                matrix.postRotate(90);
                // recreate the new Bitmap
                Log.v("upload","hello3");
                photo = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                //Upload
                Log.v("upload","click");
                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonObject = new JSONObject();
                StringEntity entity = null;
                Log.v("upload","on click");
                try {
                    jsonObject.put("activity_id", activity_id);
                    entity = new StringEntity(jsonObject.toString());
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("upload","hello4");
                client.post(getActivity(), "http://the-city.appspot.com/api/android_get_url", entity, "application/json", new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.v("upload","hello5");
                        try {
                            Log.v("upload",response.getString("upload_url"));
                            postImage(response.getString("upload_url"));
                        } catch (Exception ex) {
                            Log.e("upload", "Failed to parse JSON due to: " + ex);
                        }
                    }

                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        public void postImage(String upload){
            pDialog = ProgressDialog.show(getActivity(), "Please wait", "Image Uploading");
            RequestParams params = new RequestParams();
            //params.put("comment",tag.getText().toString());
            // try {
            //params.put("img", new File(selectedImagePath));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 85, out);
            byte[] myByteArray = out.toByteArray();
            params.put("image",new ByteArrayInputStream(myByteArray),"image.png");
            params.put("activity_id",activity_id);
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Content-Type", "multipart/form-data");
            client.post(upload, params, new AsyncHttpResponseHandler() {

                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        Log.v("upload", "success!!!!");
                        Log.v("upload",new String(response));
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "Upload Image Success!", Toast.LENGTH_SHORT).show();
                        activity.pics.add(new String(response));
                        mRecyclerView.setAdapter(new LayoutAdapter(getActivity(), mRecyclerView,activity.pics));
                    } catch (Exception ex) {
                        Log.e("upload", "Failed to parse JSON due to: " + ex);
                    }

                }

                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.v("upload",statusCode+"");
                    Log.v("upload","postImage fail");
                }

            });
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_activity_one, container, false);
            final TextView title = (TextView) rootView.findViewById(R.id.title_one);
            final ImageView cover = (ImageView) rootView.findViewById(R.id.cover_one);
            final TextView date = (TextView) rootView.findViewById(R.id.date_one);
            final TextView address = (TextView) rootView.findViewById(R.id.address_one);
            final TextView host = (TextView) rootView.findViewById(R.id.host_one);
            final TextView tag = (TextView) rootView.findViewById(R.id.tag_one);
            final TextView details = (TextView) rootView.findViewById(R.id.details_one);
            final TextView take = (TextView) rootView.findViewById(R.id.take_action);
            final TextView like = (TextView) rootView.findViewById(R.id.like_action);
            final RelativeLayout direction = (RelativeLayout) rootView.findViewById(R.id.direction);
            final ImageView add_image = (ImageView) rootView.findViewById(R.id.add_image);
            //Fetch data
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonObject = new JSONObject();
            StringEntity entity = null;
            try {
                jsonObject.put("activity_id", activity_id);
                jsonObject.put("guest_id",email);
                entity = new StringEntity(jsonObject.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            client.post(rootView.getContext(),"http://the-city.appspot.com/api/activity",entity,"application/json",new JsonHttpResponseHandler(){
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson3 = gsonBuilder.create();
                        String str = response.toString();
                        Log.v("test2", "success");
                        activity = gson3.fromJson(str, Activity_class.class);
                        title.setText(activity.title);
                        date.setText(activity.start_date+" -- "+activity.end_date);
                        address.setText(activity.address);
                        host.setText(activity.host);
                        tag.setText(activity.tag);
                        details.setText(activity.details);
                        if(activity.like_action.equals("0"))
                            like.setBackgroundColor(Color.rgb(255, 204, 204));
                        else
                            like.setBackgroundColor(Color.rgb(51,0,0));
                        if(activity.take_action.equals("0"))
                            take.setBackgroundColor(Color.rgb(255, 204, 204));
                        else
                            take.setBackgroundColor(Color.rgb(51,0,0));
                        //Cover Image
                        if (!activity.cover.equals("")) {
                            Picasso.with(rootView.getContext())
                                    .load("http://the-city.appspot.com/img?key=" + activity.cover)
                                    //.placeholder(R.drawable.ico_loading)
                                    .resize(250, 200)
                                    .into(cover);
                        }
                        else{
                            Log.v("binbn","haha2");
                            Picasso.with(rootView.getContext()) //
                                    .load("http://upload.wikimedia.org/wikipedia/commons/b/b9/No_Cover.jpg") //
                                    //.placeholder(R.drawable.ico_loading) //
                                    .resize(100, 150)
                                    .into(cover);
                        }
                        //ListView
                        mToast = Toast.makeText(rootView.getContext(), "", Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        mRecyclerView = (TwoWayView) rootView.findViewById(R.id.list);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLongClickable(true);
                        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

                        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                                //mToast.setText("Item clicked: " + position);
                                //mToast.show();
                                //
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null);
                                final AlertDialog dialog = new AlertDialog.Builder(parent.getContext()).create();
                                ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                                /*Picasso.with(parent.getContext())
                                            .load("http://the-city.appspot.com/img?key=" + activity.pics.get(position))
                                            .into(img);*/
                                Log.v("picasso",activity.pics.get(position));
                                Picasso.with(parent.getContext()) //
                                        .load("http://the-city.appspot.com" + activity.pics.get(position)) //
                                        .into(img);
                                dialog.setView(imgEntryView); // 自定义dialog
                                dialog.show();
                                // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                                imgEntryView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        dialog.cancel();
                                    }
                                });

                                //
                            }
                        });

                        itemClick.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                                mToast.setText("Item long pressed: " + position);
                                mToast.show();
                                return true;
                            }
                        });

                        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int i, int i2) {

                            }
                        });

                        final Drawable divider = getResources().getDrawable(R.drawable.divider);
                        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
                        if (activity.pics.size()!=0)
                            mRecyclerView.setAdapter(new LayoutAdapter(rootView.getContext(), mRecyclerView,activity.pics));
                        else {
                            List<String> urls = null;
                            urls.add(0,"");
                            mRecyclerView.setAdapter(new LayoutAdapter(rootView.getContext(), mRecyclerView,urls));
                        }
                    } catch (Exception ex) {
                        Log.e("Hello", "Failed to parse JSON due to: " + ex);
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse) {
                    Log.i("error", "fail to request");
                    Log.v("geo","fail");
                }
            });
            //Like&Take listner
            like.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Fetch data
                        pDialog_like = ProgressDialog.show(getActivity(), "Please wait", "Requesting");
                        AsyncHttpClient client = new AsyncHttpClient();
                        JSONObject jsonObject = new JSONObject();
                        StringEntity entity = null;
                        try {
                            jsonObject.put("activity_id", activity_id);
                            jsonObject.put("guest_id",email);
                            jsonObject.put("action",activity.like_action);
                            entity = new StringEntity(jsonObject.toString());
                            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        client.post(rootView.getContext(),"http://the-city.appspot.com/api/like",entity,"application/json",new JsonHttpResponseHandler(){
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                pDialog_like.dismiss();
                                try {
                                    activity.like_action = response.getString("action");
                                    if(activity.like_action.equals("0"))
                                        like.setBackgroundColor(Color.rgb(255, 204, 204));
                                    else
                                        like.setBackgroundColor(Color.rgb(51,0,0));
                                    Log.v("hello","like_action"+activity.like_action);

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
            take.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public  void onClick(View v){
                        //Fetch data
                        pDialog_take = ProgressDialog.show(getActivity(), "Please wait", "Requesting");
                        AsyncHttpClient client = new AsyncHttpClient();
                        JSONObject jsonObject = new JSONObject();
                        StringEntity entity = null;
                        try {
                            jsonObject.put("activity_id", activity_id);
                            jsonObject.put("guest_id",email);
                            jsonObject.put("action",activity.take_action);
                            jsonObject.put("comment","");
                            entity = new StringEntity(jsonObject.toString());
                            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        client.post(rootView.getContext(),"http://the-city.appspot.com/api/android_take",entity,"application/json",new JsonHttpResponseHandler(){
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                pDialog_take.dismiss();
                                try {
                                    activity.take_action = response.getString("action");
                                    if(activity.take_action.equals("0"))
                                        take.setBackgroundColor(Color.rgb(255, 204, 204));
                                    else
                                        take.setBackgroundColor(Color.rgb(51,0,0));
                                    Log.v("hello","take_action"+activity.take_action);

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
            //Add Image onclick
            add_image.setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View v) {

                    // in onCreate or any event where your want the user to
                    // select a file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
            //Direction onclick
            direction.setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View v){

                            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse( "http://maps.google.com/maps?"  + "&daddr="+activity.latitude+","+activity.longitude));
                            intent.setClassName( "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }
            });
            //Cover image
            cover.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null);
                    final AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
                    ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                                /*Picasso.with(parent.getContext())
                                            .load("http://the-city.appspot.com/img?key=" + activity.pics.get(position))
                                            .into(img);*/
                    Log.v("picasso",activity.cover);
                    Picasso.with(v.getContext()) //
                            .load("http://the-city.appspot.com/img?key=" + activity.cover) //
                            .into(img);
                    dialog.setView(imgEntryView); // 自定义dialog
                    dialog.show();
                    // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                    imgEntryView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                }
            });
            return rootView;
        }
    }
}
