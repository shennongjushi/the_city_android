/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.binbinfu.the_city;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    static HotPagerAdapter mHotPagerAdapter;
    private static FragmentManager fragmentmanager;
    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    static ViewPager mViewPager;
    /*
    Navigator drawer
     */
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMycitytitle;
    private Bundle bundle;
    private static String user_email;
    private static ImageAdapter adapter ;
    private static Search_streams streams;
    private static String radius;

    private static Hot_class hot_activities = new Hot_class();

    //Nearby
    private static GoogleMap map;
    private static ArrayList<Marker_List> markerPoints= new ArrayList<Marker_List>();
    public static double my_latitude;
    public static double my_longitude;
    public static int flag = 0;
    public static Nearby_Streams nearby_streams;
    private static Marker marker;
    private static ImageLoader imageLoader;
    private static DisplayImageOptions options;
    private static ProgressDialog pDialog;
    //Nearby

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.

        //mHotPagerAdapter = new HotPagerAdapter(getSupportFragmentManager());
        fragmentmanager = getSupportFragmentManager();
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.

        actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
                .setTabListener(this));

        mTitle = mDrawerTitle = getTitle();
        mMycitytitle = getResources().getStringArray(R.array.my_city_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMycitytitle));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        bundle = this.getIntent().getExtras();
        if(bundle!=null){
            user_email=bundle.getString("email");
            Log.v("testlogin",user_email);
        }
        /*
        Nearby
         */
    }
/*
Navigator Drawer
 */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.haha, menu);
        return super.onCreateOptionsMenu(menu);
    }
     /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        /*android.app.Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();*/
        switch (position) {
            case 0:
                // The first is Profile
                Bundle bundle = new Bundle();
                bundle.putString("email", user_email);
                Intent intent = new Intent(MainActivity.this, Profile.class);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
                break;
            case 1:
                // The second is My Like
                Bundle bundle1 = new Bundle();
                bundle1.putString("email", user_email);
                bundle1.putString("action","0");
                Intent intent1 = new Intent(MainActivity.this, My_activity.class);
                intent1.putExtras(bundle1);
                MainActivity.this.startActivity(intent1);
                break;
            case 2:
                // The third is My Take
                Bundle bundle2 = new Bundle();
                bundle2.putString("email", user_email);
                bundle2.putString("action","1");
                Intent intent2 = new Intent(MainActivity.this, My_activity.class);
                intent2.putExtras(bundle2);
                MainActivity.this.startActivity(intent2);
                break;
            case 3:
                //The 4th is My Post
                Bundle bundle3 = new Bundle();
                bundle3.putString("email", user_email);
                bundle3.putString("action","2");
                Intent intent3 = new Intent(MainActivity.this, My_activity.class);
                intent3.putExtras(bundle3);
                MainActivity.this.startActivity(intent3);
                break;
            case 4:
                //The 5th is Setting
                Bundle bundle4 = new Bundle();
                bundle4.putString("email", user_email);
                Intent intent4 = new Intent(MainActivity.this, Setting.class);
                intent4.putExtras(bundle4);
                MainActivity.this.startActivity(intent4);
                break;
            default:
                break;
        }


        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/*
Navigator Drawer
 */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab isselected, show the tabcontents in the
        // //container view.
        Fragment fragment3 = null;
        Fragment fragment1 = null;
        Fragment fragment2 = null;
        switch (tab.getPosition()) {
            case 0:
                // The first is Selected
                if(fragment1 == null) {
                    fragment1 = new SelectedFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment1).commit();
                break;
            case 1:
                // The second is Search
                if(fragment2 == null){
                    fragment2 = new AllFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment2).commit();
                break;
            case 2:
                // The third is My City
                if(fragment3 == null){
                    fragment3 = new NearbyFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment3).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class SelectedFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_selected, container, false);
            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
            //Fetch data
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonObject = new JSONObject();
            StringEntity entity = null;
            try {
                jsonObject.put("test", "test");
                entity = new StringEntity(jsonObject.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            client.post(rootView.getContext(),"http://the-city.appspot.com/api/hot_activity",entity,"application/json",new JsonHttpResponseHandler(){
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson3 = gsonBuilder.create();
                        String str = response.toString();
                        Log.v("test","success");
                        hot_activities = gson3.fromJson(str,Hot_class.class);
                        mHotPagerAdapter = new HotPagerAdapter(fragmentmanager);
                        mViewPager.setAdapter(mHotPagerAdapter);

                    } catch (Exception ex) {
                        Log.e("Hello", "Failed to parse JSON due to: " + ex);
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse) {
                    Log.i("error", "fail to request");
                    Log.v("geo","fail");
                }
            });
            // Set up the ViewPager, attaching the adapter.
            //mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
            //mViewPager.setAdapter(mHotPagerAdapter);

            return rootView;
        }
    }

    public static class AllFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_all, container, false);
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonObject = new JSONObject();
            StringEntity entity = null;
            try {
                jsonObject.put("test", "test");
                entity = new StringEntity(jsonObject.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (JSONException e) {
                Log.v("ann", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.v("ann", e.getMessage());
            }
            Log.v("ann","before_send");
            client.post(rootView.getContext(), "http://the-city.appspot.com/api/get_all", entity, "application/json", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Log.v("ann", "onSuccess");
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String str = response.toString();
                        Log.v("ann", str);
                        streams = gson.fromJson(str, Search_streams.class);
                        if(adapter == null)
                            adapter = new ImageAdapter(getActivity(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),
                                    new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                        adapter.covers.clear();
                        adapter.ids.clear();
                        adapter.titles.clear();
                        adapter.starts.clear();
                        adapter.ends.clear();
                        adapter.locations.clear();
                        adapter.types.clear();
                        if (!streams.ongoing_activity.isEmpty()) {
                            Log.v("ann", "ongoing_not_empty");
                            adapter.covers.addAll(streams.ongoing_cover);
                            adapter.ids.addAll(streams.ongoing_activity);
                            adapter.titles.addAll(streams.ongoing_title);
                            adapter.starts.addAll(streams.ongoing_start_time);
                            adapter.ends.addAll(streams.ongoing_end_time);
                            adapter.locations.addAll(streams.ongoing_location);
                            adapter.types.addAll(streams.ongoing_tag);
                        }
                        if (!streams.past_activity.isEmpty()) {
                            Log.v("ann", "past_notempty");
                            adapter.covers.addAll(streams.past_cover);
                            adapter.ids.addAll(streams.past_activity);
                            adapter.titles.addAll(streams.past_title);
                            adapter.starts.addAll(streams.past_start_time);
                            adapter.ends.addAll(streams.past_end_time);
                            adapter.locations.addAll(streams.past_location);
                            adapter.types.addAll(streams.past_tag);

                        }
                        adapter.notifyDataSetChanged();
                        final ExpandableGridView gridview = (ExpandableGridView)rootView.findViewById(R.id.grid_view_all);
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
            final ExpandableGridView gridview = (ExpandableGridView)rootView.findViewById(R.id.grid_view_all);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", Long.toString(id));
                    bundle.putString("email", user_email);
                    Intent intent = new Intent(rootView.getContext(), Activity_one.class);
                    intent.putExtras(bundle);
                    rootView.getContext().startActivity(intent);
                    //Toast.makeText(rootView.getContext(), "" + id, Toast.LENGTH_SHORT).show();
                }
            });
            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class NearbyFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);
            final EditText keyword =(EditText)rootView.findViewById(R.id.et_place);
            final ImageButton search_btn = (ImageButton)rootView.findViewById(R.id.btn_show);
            initImageLoader(rootView);
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.ic_launcher)
                    .cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();


            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            map = fm.getMap();
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    my_latitude = arg0.getLatitude();
                    my_longitude = arg0.getLongitude();
                    flag =1;
                    //Log.v("ann", "" + my_latitude);
                    //Log.v("ann", "" + my_longitude);
                }
            });
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter(inflater));
       /* map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(Marker_List mark:markerPoints){
                    if(marker.getTitle().equals(mark.title)){
                        Toast.makeText(Nearby.this, mark.title,Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });*/
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    for(Marker_List mark:markerPoints){
                        if(marker.getTitle().equals(mark.title)){
                            //Toast.makeText(rootView.getContext(), mark.title,Toast.LENGTH_SHORT).show();
                            /////////////////////add intent////////////////////////
                            Bundle bundle = new Bundle();
                            bundle.putString("id", mark.activity);
                            bundle.putString("email", user_email);
                            Intent intent = new Intent(rootView.getContext(), Activity_one.class);
                            intent.putExtras(bundle);
                            rootView.getContext().startActivity(intent);
                        }
                    }
                }
            });

            Log.v("ann","map");

            if(map!=null) {
                Log.v("ann", "not empty map");
                // Enable MyLocation Button in the Map
                map.setMyLocationEnabled(true);
                //this.my_latitude = map.getMyLocation().getLatitude();
                //this.my_longitude = map.getMyLocation().getLongitude();
            }

            search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radius = keyword.getText().toString();
                    //Fetch data
                    if(!radius.isEmpty()) {
                        pDialog = ProgressDialog.show(getActivity(), "Please wait", "Fetching data");
                        PostFetcher post = new PostFetcher();
                        post.execute();
                    }
                }
            });

            return rootView;
        }
        /*
    Nearby
*/


        private class PostFetcher extends AsyncTask<Void, Void, String>{
            public static final String NEARBY_URL = "http://the-city.appspot.com/api/nearby";
            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                markerPoints.clear();
                map.clear();
                Log.v("ann", "size = " + nearby_streams.markers.size());
                Random rand = new Random();
                for (int i = 0; i < nearby_streams.markers.size(); i++) {
                    LatLng location = new LatLng(Double.parseDouble(nearby_streams.markers.get(i).latitude) + (-1 + 2 * rand.nextDouble()) / 2000, Double.parseDouble(nearby_streams.markers.get(i).longitude) + (-1 + 2 * rand.nextDouble()) / 2000);
                    markerPoints.add(new Marker_List(location, nearby_streams.markers.get(i).title, nearby_streams.markers.get(i).activity, nearby_streams.markers.get(i).cover));
                    map.addMarker(new MarkerOptions()
                            .position(location)
                            .title(nearby_streams.markers.get(i).title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                pDialog.dismiss();
            }
            @Override
            protected String doInBackground(Void... params){
                try{
                    //Create an HTTP client
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(NEARBY_URL);
                    try{
                        Gson gson = new Gson();
                        Nearby_Request request = new Nearby_Request();
                        if(!radius.isEmpty())
                            request.radius = radius;
                        else
                            request.radius="100";
                        while(flag == 0);
                        request.latitude = ""+my_latitude;
                        request.longitude = ""+my_longitude;
                        String json_request = gson.toJson(request);
                        Log.v("ann","send resquest :"+json_request);
                        post.setHeader("content-type", "application/json");
                        post.setEntity(new StringEntity(json_request));
                        HttpResponse response = client.execute(post);
                        StatusLine statusLine = response.getStatusLine();
                        if(statusLine.getStatusCode() == 200){
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            try{
                                Reader reader = new InputStreamReader(content);
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson2 = gsonBuilder.create();
                                //Log.v("ann",reader.toString());
                                if (reader != null) {
                                    nearby_streams = gson2.fromJson(reader,Nearby_Streams.class);
                                }


                            }catch (Exception ex){
                                Log.e("ann", "Failed to parse JSON due to: " + ex);

                            }
                        }else{
                            Log.e("ann", "Server responded with status code: " + statusLine.getStatusCode());

                        }
                    }catch(Exception ex){
                        Log.v("ann",ex.getMessage());

                    }
                }catch(Exception ex){
                    Log.v("ann","Failed to send HTTP POST request due to: " + ex);

                }
                return null;
            }
        }

        protected static class Nearby_Request{
            private String radius;
            private String latitude;
            private String longitude;
        }

        private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
            private View view;
            private boolean not_first = false;

            public CustomInfoWindowAdapter(LayoutInflater inflater) {
                view = inflater.inflate(R.layout.info_window, null);
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker != null && marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                    marker.showInfoWindow();
                }
                return null;
            }

            @Override
            public View getInfoWindow(final Marker marker1) {
                marker = marker1;
                String url = null;
                for (Marker_List mark : markerPoints) {
                    if (mark.title.equals(marker.getTitle())) {
                        url = "http://the-city.appspot.com/img?key=" + mark.cover;
                        Log.v("ann", url);
                    }
                }
                final ImageView cover = ((ImageView) view.findViewById(R.id.cover));
            /*if (not_first) {
                Picasso.with(Nearby.this)
                        .load(url)
                        .placeholder(R.drawable.ico_loading)
                        .resize(120, 120)
                        .into(cover);
            } else {
                not_first = true;
                Picasso.with(Nearby.this)
                        .load(url)
                        .placeholder(R.drawable.ico_loading)
                        .resize(120, 120)
                        .into(cover, new InfoWindowRefresher(marker));
            }*/

                imageLoader.displayImage(url,cover,options, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageurl, View view, Bitmap loaded){
                        super.onLoadingComplete(imageurl,view,loaded);
                        getInfoContents(marker);
                    }
                });


                final TextView title = ((TextView) view.findViewById(R.id.title));
                title.setText(marker.getTitle());
                return view;
            }
        }
    }




    private static void initImageLoader(View view) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    view.getContext().getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                view.getContext()).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        ImageLoader.getInstance().init(config);


    }
    /*
    Nearby
     */
    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class HotPagerAdapter extends FragmentStatePagerAdapter {

        public HotPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new HotObjectFragment();
            Bundle args = new Bundle();
            Log.v("test","fragment");
            Log.v("test",hot_activities.hot_id.get(0));
            if (hot_activities.hot_id.size()!=0) {
                args.putString("hot_id", hot_activities.hot_id.get(i));
                args.putString("hot_title", hot_activities.hot_title.get(i));
                args.putString("hot_start", hot_activities.hot_start.get(i));
                args.putString("hot_end", hot_activities.hot_end.get(i));
                args.putString("hot_take", hot_activities.hot_take.get(i));
                args.putString("hot_like", hot_activities.hot_like.get(i));
                args.putString("hot_address", hot_activities.hot_address.get(i));
                args.putString("hot_type", hot_activities.hot_type.get(i));
                args.putString("hot_cover",hot_activities.hot_cover.get(i));
            }
            else{
                args.putString("hot_id", "");
                args.putString("hot_title", "");
                args.putString("hot_start", "");
                args.putString("hot_end", "");
                args.putString("hot_take", "");
                args.putString("hot_like", "");
                args.putString("hot_address", "");
                args.putString("hot_type", "");
                args.putString("hot_cover","");
            }
                //args.putString("hot_title",hot_activities.hot_id.get(0));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return hot_activities.hot_id.size();
        }

    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class HotObjectFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            final TextView title = (TextView) rootView.findViewById(R.id.title);
            final TextView tag = (TextView) rootView.findViewById(R.id.tag);
            final TextView time = (TextView) rootView.findViewById(R.id.time);
            final TextView address = (TextView) rootView.findViewById(R.id.address);
            final TextView like = (TextView) rootView.findViewById(R.id.like);
            final TextView take = (TextView) rootView.findViewById(R.id.take);
            final ImageView cover = (ImageView) rootView.findViewById(R.id.cover);
            final LinearLayout item = (LinearLayout) rootView.findViewById(R.id.item);
            final Bundle args = getArguments();
            title.setText(args.getString("hot_title"));
            tag.setText(args.getString("hot_type"));
            time.setText(args.getString("hot_start")+" -- " +args.getString("hot_end"));
            address.setText(args.getString("hot_address"));
            like.setText("Like: "+args.getString("hot_like"));
            take.setText("Take: "+args.getString("hot_take"));
            if (!args.getString("hot_cover").equals("")) {
                Picasso.with(rootView.getContext())
                        .load("http://the-city.appspot.com/img?key="+args.getString("hot_cover"))
                     //   .placeholder(R.drawable.ico_loading)
                        .resize(250, 200)
                        .into(cover);
            }
            else{
                Log.v("binbn","haha2");
                Picasso.with(rootView.getContext()) //
                        .load("http://upload.wikimedia.org/wikipedia/commons/b/b9/No_Cover.jpg") //
                     //   .placeholder(R.drawable.ico_loading) //
                        .resize(250, 200)
                        .into(cover);
            }
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id",args.getString("hot_id"));
                    bundle.putString("email",user_email);
                    Intent intent = new Intent(getActivity(),Activity_one.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
            case R.id.action_websearch:
                Bundle bundle = new Bundle();
                bundle.putString("email",user_email);
                Intent intent = new Intent(MainActivity.this,Search.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected static class Marker_List{
        public LatLng location;
        public String title;
        public String activity;
        public String cover;
        Marker_List(LatLng loc, String title, String activity,String cover){
            this.location = loc;
            this.title = title;
            this.activity = activity;
            this.cover = cover;
            this.cover = cover;

        }

    }
}
