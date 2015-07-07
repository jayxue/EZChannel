/*
 * Copyright 2015 Waterloo Mobile Studio. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wms.opensource.ezchannel.adapter;

import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.listener.ImageViewSrcSelector;
import com.wms.opensource.ezchannel.util.DeviceUtil;
import com.wms.opensource.ezchannel.util.ViewUtil;
import com.wms.opensource.ezchannel.view.ExpandablePanel;
import com.wms.opensource.ezchannel.youtube.YouTubeUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeVideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoArrayAdapter extends ArrayAdapter<YouTubeVideo> {
	
	private ImageView imageViewStandardThumbnail = null;
	private ProgressBar progressBar = null;
	
	private AQuery listAq = null;
	private AQuery aq = null;
	
	public VideoArrayAdapter(Context context, int resource, List<YouTubeVideo> videos, ImageView imageViewStandardThumbnail, ProgressBar progressBar) {
		super(context, resource, videos);
		this.imageViewStandardThumbnail = imageViewStandardThumbnail;
		this.progressBar = progressBar;
		
		listAq = new AQuery(context);
		aq = new AQuery(context);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.youtube_video_list_item, null);
		}

		convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
           		YouTubeVideo video = VideoArrayAdapter.this.getItem(position);
           		YouTubeUtil.gotoSingleYouTubeVideoActivity(getContext(), video);
            }
            
        });
		
		final YouTubeVideo video = this.getItem(position);
		
		int screenWidth = DeviceUtil.getDeviceWidth(getContext());
		
		String thumbnailUrl = video.getBestThumbnailUrl(getContext());
		
		aq = listAq.recycle(convertView);
		ImageOptions options = new ImageOptions();
		options.round = 1;
		options.ratio = AQuery.RATIO_PRESERVE;
		options.memCache = true;
		options.fileCache = true;
		// Use 200 for down sampling. See https://code.google.com/p/android-query/wiki/ImageLoading.
		options.targetWidth = 200;
		aq.id(R.id.imageViewThumbnail).progress(R.id.YouTubeLoadingProgressBar).image(thumbnailUrl, options);
	
		ImageView imageViewThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
		imageViewThumbnail.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestThumbnailUrl = video.getBestThumbnailUrl(getContext());
    			// Try to load the thumbnail using Android Query.
    			AQuery aq = new AQuery(getContext());
    			boolean memCache = false;
    			boolean fileCache = true;

   				aq.id(imageViewStandardThumbnail).progress(progressBar).image(bestThumbnailUrl, memCache, fileCache, 0, 0, null, AQuery.FADE_IN);					
			}
			
		});
		imageViewThumbnail.setOnTouchListener(new ImageViewSrcSelector());			
		// Adjust sizes of the thumbnail image according to screen width			
		if(screenWidth >= 1200) {
			ViewUtil.setImageViewSize(imageViewThumbnail, 480, 360);
		}
		else if(screenWidth >= 640) {
			ViewUtil.setImageViewSize(imageViewThumbnail, 320, 180);
		}			

		TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
		textViewTitle.setText(video.getTitle());

		TextView textViewPublishTime = (TextView) convertView.findViewById(R.id.textViewPublishTime);
		textViewPublishTime.setText(video.getPublishedAt());

		ExpandablePanel panel = (ExpandablePanel) convertView.findViewById(R.id.expandablePanelVideoDescription);
		panel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
			public void onCollapse(View handle, View content) {
				ImageButton btn = (ImageButton) handle;
				btn.setImageResource(R.drawable.arrow_down);
			}
			public void onExpand(View handle, View content) {
				ImageButton btn = (ImageButton) handle;
				btn.setImageResource(R.drawable.arrow_up);
			}
		});			
		panel.setText(video.getDescription());
		
		return convertView;
	}
	
}
