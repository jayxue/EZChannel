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

package com.wms.opensource.ezchannel.task;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.util.DialogUtil;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.MessageUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class LoadVideosOverallInfoTask extends AsyncTask<Void, Void, Void> {

	private Context context = null;
	private Handler handler = null;
	private YouTubeVideoSource videoSource = YouTubeVideoSource.Playlist;
	private String appName = "";
	// loadCriteria is either playlist ID or query term, depending on videoSource
	private String loadCriteria = "";
	private boolean shouldAutoRun = false;
	private int savedVideoCount = -1;
	
	private ProgressDialog progressDialog = null;
	private int videosCount = -1;
	private String secondPageToken = "";
	
	public LoadVideosOverallInfoTask(Context context, Handler handler, YouTubeVideoSource videoSource, String appName, String loadCriteria,
			boolean shouldAutoRun, int savedVideoCount) {
		this.context = context;
		this.handler = handler;
		this.videoSource = videoSource;
		this.appName = appName;
		this.loadCriteria = loadCriteria;
		this.shouldAutoRun = shouldAutoRun;
		this.savedVideoCount = savedVideoCount;
	}
	
	protected void onPreExecute() {
		if(!shouldAutoRun) {
			progressDialog = DialogUtil.showWaitingProgressDialog(context, ProgressDialog.STYLE_SPINNER, context.getString(R.string.loadingVideos), false);
		}
    }
	
	@Override
	protected Void doInBackground(Void... params) {
		if(videoSource == YouTubeVideoSource.Playlist) {
			PlaylistItemListResponse playlistItemListResponse = YouTubeUtil.getPlaylistOverallInfo(context, appName, loadCriteria);
        	videosCount = playlistItemListResponse.getPageInfo().getTotalResults();
        	secondPageToken = playlistItemListResponse.getNextPageToken();
		}
		else {
			SearchListResponse searchListResponse = YouTubeUtil.getSearchListOverallInfo(context, appName, loadCriteria);
	    	videosCount = searchListResponse.getPageInfo().getTotalResults();
	    	secondPageToken = searchListResponse.getNextPageToken();
		}
		FileUtil.writeStringToFile(videosCount + "", StorageUtil.getTempDirectory(context), context.getString(R.string.YouTubeVideosCountFileName), 
				context.getString(R.string.charSetName));

		return null;
	}

    protected void onPostExecute(Void result) { 
    	if(progressDialog != null && progressDialog.isShowing() == true) {
    		progressDialog.dismiss();
    	}
    	
    	if(videosCount == -1) {
    		DialogUtil.showExceptionAlertDialog(context, context.getString(R.string.loadVideoInfoFailed), context.getString(R.string.contactAppProducer));
    	}
    	else {
    		if(shouldAutoRun) {
    			// If the video count is loaded silently, we should ask the user if he wants to reload videos
    			if(videosCount > savedVideoCount) {
    				MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED_ASK_FOR_RELOADING);
    			}
    		}
    		else {
    			MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED);
    		}
    	}

    }

	public int getVideosCount() {
		return videosCount;
	}
        
	public String getSecondPageToken() {
		return secondPageToken;
	}
}
