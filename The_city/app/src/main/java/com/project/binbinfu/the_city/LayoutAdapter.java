/*
 * Copyright (C) 2014 Lucas Rocha
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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {
    private static final int COUNT = 100;

    private final Context mContext;
    private final TwoWayView mRecyclerView;
    private final List<String> mItems;
    private int mCurrentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public LayoutAdapter(Context context, TwoWayView recyclerView, List<String> image_urls) {
        mContext = context;
        mItems = new ArrayList<String>(image_urls.size());
        for (int i = 0; i < image_urls.size(); i++) {
            addItem(i,image_urls.get(i));
        }

        mRecyclerView = recyclerView;
    }

    public void addItem(int position, String url) {
        mItems.add(position, url);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        //holder.title.setText(mItems.get(position).toString());
        ImageView pic = (ImageView) holder.itemView.findViewById(R.id.pic_one);
        if (!mItems.get(position).equals("")) {
            Log.v("my", "haha1");
            Picasso.with(mContext) //
                    .load("http://the-city.appspot.com"+mItems.get(position)) //
                 //   .placeholder(R.drawable.ico_loading) //
                    .resize(50, 50)
                    .into(pic);
        }
        else{
            Log.v("binbn","haha2");
            Picasso.with(mContext) //
                    .load("http://upload.wikimedia.org/wikipedia/commons/b/b9/No_Cover.jpg") //
                 //   .placeholder(R.drawable.ico_loading) //
                    .resize(50, 50)
                    .into(pic);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
