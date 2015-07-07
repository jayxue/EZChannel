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

package com.wms.opensource.ezchannel.activity;

import java.util.ArrayList;
import java.util.List;

import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.adapter.VideoArrayAdapter;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.task.LoadVideosFromLocalTask;
import com.wms.opensource.ezchannel.task.LoadVideosTask;
import com.wms.opensource.ezchannel.type.NetworkStatus;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.PersistFileNameProvider;
import com.wms.opensource.ezchannel.youtube.YouTubeUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeVideo;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;
import com.wms.opensource.ezchannel.util.NetworkUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class VideoListFragment extends Fragment {

	private static final String KEY_CONTENT = "VideoListFragment";
	
	private String mContent = "";
	private String playlistID = "";
	private String queryTerm = "";
	private int page = 1;

	private RelativeLayout layout = null;
	private ListView listView = null;
	private ImageView imageViewStandardThumbnail = null;
	private ProgressBar progressBar = null;
	
	private LoadVideosTask loadVideosTask = null;
	private LoadVideosFromLocalTask loadPlaylistVideosFromLocalTask = null;
	
	private LoadVideosHandler loadVideosHandler = new LoadVideosHandler();
	
	private List<YouTubeVideo> videos = new ArrayList<YouTubeVideo>();
	
    public static VideoListFragment newInstance(String content, String playlistIDOrQueryTerm, int page, YouTubeVideoSource videoSource) {
    	VideoListFragment fragment = new VideoListFragment();
        if(videoSource == YouTubeVideoSource.Playlist) {
        	fragment.playlistID = playlistIDOrQueryTerm;
        }
        else {
        	fragment.queryTerm = playlistIDOrQueryTerm;
        }
        fragment.page = page;
        return fragment;        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }        
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(layout != null) {	
    		// We need to remove layout from its parent first. Otherwise, adding layout to different viewgroup will result in error 
    		ViewGroup parent = (ViewGroup) layout.getParent();    		
    		parent.removeView(layout);
    		return layout;
    	}
    	
    	if(listView == null) {
            layout = new RelativeLayout(getActivity());                       
	        listView = new ListView(getActivity());	        
	        layout.addView(listView);
            
	        imageViewStandardThumbnail = new ImageView(getActivity());
            RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageViewStandardThumbnail.setLayoutParams(imageViewParams);
            imageViewStandardThumbnail.setVisibility(View.INVISIBLE);
            imageViewStandardThumbnail.setBackgroundResource(R.drawable.border);
            imageViewStandardThumbnail.setOnClickListener(new ImageView.OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				// Dismiss the image view
    				v.setVisibility(View.INVISIBLE);
    	    		Animation myAnim = AnimationUtils.loadAnimation(VideoListFragment.this.getActivity(), R.anim.fadeout);
    	    		v.startAnimation(myAnim);
    			}

            });
            layout.addView(imageViewStandardThumbnail);
	        
            progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);

            // Center a view in relative layout
            RelativeLayout.LayoutParams progressBarBarams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            progressBarBarams.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(progressBarBarams);
            progressBar.setVisibility(View.INVISIBLE);
            layout.addView(progressBar);

            // Before reloading videos, display videos if they have been saved before.            
	        String videosFilePath = "";
        	if(VideoListFragmentActivity.videoSource == YouTubeVideoSource.Playlist) {
        		videosFilePath = StorageUtil.getTempDirectory(this.getActivity()) + "/" + PersistFileNameProvider.getYouTubePlaylistPersistFileName(playlistID, page);
        	}
        	else {
        		videosFilePath = StorageUtil.getTempDirectory(this.getActivity()) + "/" + PersistFileNameProvider.getYouTubeSearchListPersistFileName(getActivity().getString(R.string.appID), page);
        	}
        	
	    	boolean videosFileExists = FileUtil.fileExist(videosFilePath);            
	    	if(videosFileExists) {
    			loadPlaylistVideosFromLocalTask = new LoadVideosFromLocalTask(getActivity(), loadVideosHandler, VideoListFragmentActivity.videoSource, progressBar, playlistID, page);
    			loadPlaylistVideosFromLocalTask.execute();
	    	}
	    	else {
		        NetworkStatus networkStatus = NetworkUtil.getNetworkStatus(getActivity());        
		        if (networkStatus.equals(NetworkStatus.WIFI_CONNECTED) || networkStatus.equals(NetworkStatus.MOBILE_CONNECTED)) {
        			String pageToken = page == 1? null : VideoListFragmentActivity.pageTokens.get(page - 1); // page start from 1
	        		if(VideoListFragmentActivity.videoSource == YouTubeVideoSource.Playlist) {
	        			loadVideosTask = new LoadVideosTask(getActivity(), loadVideosHandler, YouTubeVideoSource.Playlist, playlistID, pageToken, page);
	        		}
	        		else {
	        			loadVideosTask = new LoadVideosTask(getActivity(), loadVideosHandler, YouTubeVideoSource.Search, queryTerm, pageToken, page);
	        		}
	        		loadVideosTask.execute();
	        	}
		    	else {
		    		Toast.makeText(getActivity(), getString(R.string.noNetworkAvailable), Toast.LENGTH_LONG).show();
		    	}	    		
	    	}        
    	}    	
        return layout;
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
    @SuppressLint("HandlerLeak")
 	private class LoadVideosHandler extends Handler { 	
     	
     	public void handleMessage(Message msg) {
     		super.handleMessage(msg);
     		if (msg.what == HandlerMessage.YOUTUBE_VIDEOS_LOADED) {
   				videos = loadVideosTask.getVideos();
     		}
     		else if(msg.what == HandlerMessage.YOUTUBE_VIDEOS_LOADED_FROM_LOCAL) {
   				videos = loadPlaylistVideosFromLocalTask.getVideos();
     		}
     		progressBar.setVisibility(View.INVISIBLE);
     		setVideoList();

     		int numberOfVideosPerPage = getActivity().getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE);
 			int startVideo = 0;
 			if(page == 1) {
 				startVideo = 1;
 			}
 			else {
 				startVideo = (page - 1) * numberOfVideosPerPage;
 			}
 			int endVideo = numberOfVideosPerPage;
 			if(VideoListFragmentActivity.videoSource == YouTubeVideoSource.Playlist) {
 				endVideo = (page - 1) * numberOfVideosPerPage + videos.size();
 			}
 			else {
 				endVideo = (page - 1) * numberOfVideosPerPage + videos.size();
 			}
 			if(endVideo == 0) {
 				endVideo = numberOfVideosPerPage;
 			}
 			if(page == 1) {
 				if(getActivity() != null) {
					getActivity().setTitle(getString(R.string.app_name) + " (" + startVideo + " to " + endVideo + ")");
 				}
 			}
     	}
    }

	private void setVideoList() {
    	if(getActivity() != null)
    	{	
    		ArrayAdapter<YouTubeVideo> videoArrayAdapter = new VideoArrayAdapter(getActivity(), R.layout.youtube_video_list_item, videos, imageViewStandardThumbnail, progressBar);
			listView.setAdapter(videoArrayAdapter);
			listView.setOnItemClickListener(new ListView.OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					YouTubeVideo video = videos.get(position);
					YouTubeUtil.gotoSingleYouTubeVideoActivity(getActivity(), video);
				}
				
			});
    	}
    }
   
    public ImageView getImageViewStandardThumbnail() {
    	return imageViewStandardThumbnail;
    }

}
