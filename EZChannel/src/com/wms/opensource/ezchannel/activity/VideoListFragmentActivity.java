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

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.adapter.VideosFragmentAdapter;
import com.wms.opensource.ezchannel.dialog.ConfirmReloadVideosDialogBuilder;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.task.LoadVideosOverallInfoTask;
import com.wms.opensource.ezchannel.type.NetworkStatus;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.MessageUtil;
import com.wms.opensource.ezchannel.util.NetworkUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class VideoListFragmentActivity extends AppCompatActivity {

	//private static final int MAX_PAGE_COUNT = 30;
	
	// If videos come from YouTube playlist or search
	public static YouTubeVideoSource videoSource = YouTubeVideoSource.Playlist;
		
	private String playlistID = "";
	private String queryTerm = "";
	
	private int pageCount = 1;	// At least 1 page
	
    private VideosFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;
    
    private LoadVideosOverallInfoTask loadVideosOverallInfoTask = null;

    private LoadVideosCountHandler loadVideosCountHandler = null;
	
	// Shall we automatically load video count info?
	boolean shouldAutoRunOverallInfoLoadingTask = false;
	
    private int savedVideosCount = 0;
    
    private int maxPageCount = 30;
    
    public static int currentPage = 1;
    
    // Maintain tokens of all pages
    public static List<String> pageTokens = new ArrayList<String>();
    
    List<Fragment> fragments = new ArrayList<Fragment>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_video_pages);

        // Set logo in toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        maxPageCount = getResources().getInteger(R.integer.MAX_PAGE_COUNT);
        
        if(getString(R.string.YouTubePlaylistID).isEmpty()) {
        	// There must be query term
        	videoSource = YouTubeVideoSource.Search;
        }
        
       	if(videoSource == YouTubeVideoSource.Playlist) {
       		playlistID = getString(R.string.YouTubePlaylistID);
       	}
       	else {
       		queryTerm = getString(R.string.YouTubeQueryTerm);
       	}
        
        loadVideosCountHandler = new LoadVideosCountHandler();
        
    	String videosCountFilePath = "";
   		videosCountFilePath = StorageUtil.getTempDirectoryPath(this) + "/" + getString(R.string.YouTubeVideosCountFileName);
    	
    	boolean videoCountFileExists = FileUtil.fileExist(videosCountFilePath);
    	
    	if(videoCountFileExists) {
       		shouldAutoRunOverallInfoLoadingTask = true;   		
    		
    		// The app has fetched video count before, so we directly get video count
    		String videosCountStr = "";
    		videosCountStr = FileUtil.getStringFromFileInCache(StorageUtil.getTempDirectory(this), getString(R.string.YouTubeVideosCountFileName), 
    							getString(R.string.charSetName));

    		// The string may contain \n
    		videosCountStr = videosCountStr.replaceAll("\\n", "");
    		savedVideosCount = Integer.valueOf(videosCountStr);
    		
			pageCount = (int) Math.ceil(savedVideosCount * 1.0 / getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE));

   			MessageUtil.sendHandlerMessage(loadVideosCountHandler, HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED);
    	}
    	else {
    		// The app never fetches video count before, so we should load it
    		loadVideosOverallInfo(false, savedVideosCount);
    	}

        // Do not show the soft keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		return true;
	}

	public void onBackPressed() {
		// If a video thumbnail image view is visible, we treat backpressed as closing the image
		for(Fragment fragment : fragments) {
			VideoListFragment f = (VideoListFragment)fragment;
			if(f.getImageViewStandardThumbnail().getVisibility() == View.VISIBLE) {
				f.getImageViewStandardThumbnail().setVisibility(View.INVISIBLE);
				return;
			}
		}

		super.onBackPressed();
	}
	
    @SuppressLint("HandlerLeak")
	private class LoadVideosCountHandler extends Handler { 	
    	
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		if (msg.what == HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED_ASK_FOR_RELOADING) {
    			ConfirmReloadVideosDialogBuilder builder = new ConfirmReloadVideosDialogBuilder(VideoListFragmentActivity.this, loadVideosCountHandler, videoSource, playlistID);
    			builder.create().show();
    		}
    		else if (msg.what == HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED) {
    			final int numberOfVideosPerPage = getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE);
   				if(loadVideosOverallInfoTask != null) {
   					pageCount = (int) Math.ceil(loadVideosOverallInfoTask.getVideosCount() * 1.0 / numberOfVideosPerPage);
   				}
   				else {
   					pageCount = (int) Math.ceil(savedVideosCount * 1.0 / numberOfVideosPerPage);
   				}
   				
    	        if(pageCount == 0) {
    	        	// At least one page
    	        	pageCount = 1;
    	        }
    	        else if(pageCount > maxPageCount) {
    	        	pageCount = maxPageCount;
    	        }
    	        
    	        pageTokens.clear();
    	        for(int i = 0; i < pageCount; i++) {
    	        	pageTokens.add("");
    	        }
    	        if(pageTokens.size() > 1 && loadVideosOverallInfoTask != null) {
    	        	pageTokens.set(1, loadVideosOverallInfoTask.getSecondPageToken());
    	        }
    	        else if(pageTokens.size() > 1 && loadVideosOverallInfoTask != null) {
    	        	pageTokens.set(1, loadVideosOverallInfoTask.getSecondPageToken());
    	        }

    	        if(videoSource == YouTubeVideoSource.Playlist) {
    				mAdapter = new VideosFragmentAdapter(getSupportFragmentManager(), pageCount, playlistID, videoSource);
    	        }
    			else {
    				mAdapter = new VideosFragmentAdapter(getSupportFragmentManager(), pageCount, queryTerm, videoSource);
    			}

    	        mPager = (ViewPager)findViewById(R.id.pager);
    	        mPager.setAdapter(mAdapter);

    	        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
	        	// Set width of the line page indicator
    	        if(pageCount > 20) {
    	        	((LinePageIndicator)mIndicator).setLineWidth(15);
    	        }
    	        if(pageCount > 10) {
    	        	((LinePageIndicator)mIndicator).setLineWidth(25);
    	        }
    	        mIndicator.setViewPager(mPager);
    	        //We set this on the indicator, NOT the pager
    	        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    	            @Override
    	            public void onPageSelected(int position) {
    	            	currentPage = position + 1;
    	    			int startVideo = (currentPage - 1) * numberOfVideosPerPage + 1;
    	    			int endVideo = currentPage * numberOfVideosPerPage;
    	            	setTitle(getString(R.string.app_name) + " (" + startVideo + " to " + endVideo + ")");
    	            }

    	            @Override
    	            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    	            	
    	            }

    	            @Override
    	            public void onPageScrollStateChanged(int state) {
    	            	
    	            }
    	        });
    	         
    		}
    	}
    	
    }
    
    @Override
    public void onAttachFragment (Fragment fragment) {
    	// Collect all fragments of the activity
        fragments.add(fragment);
        
        if(shouldAutoRunOverallInfoLoadingTask && (pageCount == 1 && fragments.size() == 1 || pageCount >= 2 && fragments.size() == 2)) {
        	// After at least one fragment is attached, start reloading videos overall info.
        	loadVideosOverallInfo(true, savedVideosCount);
        }
    }

    private void loadVideosOverallInfo(boolean shouldReloadVideoCountSilently, int savedVideosCount) {
    	NetworkStatus networkStatus = NetworkUtil.getNetworkStatus(this);
	    if (networkStatus.equals(NetworkStatus.WIFI_CONNECTED) || networkStatus.equals(NetworkStatus.MOBILE_CONNECTED)) {
	    	if(videoSource == YouTubeVideoSource.Playlist) {
	    		loadVideosOverallInfoTask = new LoadVideosOverallInfoTask(this, loadVideosCountHandler, videoSource, getString(R.string.app_name), playlistID, shouldReloadVideoCountSilently, savedVideosCount);
	    	}
	       	else {
	       		loadVideosOverallInfoTask = new LoadVideosOverallInfoTask(this, loadVideosCountHandler, videoSource, getString(R.string.app_name), queryTerm, shouldReloadVideoCountSilently, savedVideosCount);
	       	}
	    	loadVideosOverallInfoTask.execute();   			
   		}
       	else {
       		Toast.makeText(this, getString(R.string.noNetworkAvailable), Toast.LENGTH_LONG).show();
       	}    	
    }
}
