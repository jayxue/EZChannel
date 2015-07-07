/*
 * Copyright 2015 Waterloo Mobile Studio
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.json.GenericJson;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.activity.VideoListFragmentActivity;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.util.DialogUtil;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.MessageUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.PersistFileNameProvider;
import com.wms.opensource.ezchannel.youtube.YouTubeCache;
import com.wms.opensource.ezchannel.youtube.YouTubeUtil;
import com.wms.opensource.ezchannel.youtube.YouTubeVideo;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class LoadVideosTask extends AsyncTask<String, Void, GenericJson> {

	private Context context = null;
	private Handler handler = null;
	private YouTubeVideoSource videoSource = YouTubeVideoSource.Playlist;
	// loadCriteria is either a playlist ID or a search query, depending on videoSource
	private String loadCriteria = "";
	private String pageToken = "";
	private int page = 1;
	
	private ProgressDialog progressDialog = null;
	
	private List<YouTubeVideo> videos = new ArrayList<YouTubeVideo>();
	
	public LoadVideosTask(Context context, Handler handler, YouTubeVideoSource videoSource, String loadCriteria, String pageToken, int page) {
		this.context = context;
		this.handler = handler;
		this.videoSource = videoSource;
		this.loadCriteria = loadCriteria;
		this.pageToken = pageToken;
		this.page = page;
	}

	protected void onPreExecute() {
		progressDialog = DialogUtil.showWaitingProgressDialog(context, ProgressDialog.STYLE_SPINNER, context.getString(R.string.loadingVideos), false);
    }
	
	@Override
	protected GenericJson doInBackground(String... params) {
		GenericJson response = null;
		if(videoSource == YouTubeVideoSource.Playlist) {
			response = YouTubeUtil.getPlaylistItems(context, context.getString(R.string.app_name), loadCriteria, pageToken);
			if(response != null) {
				List<PlaylistItem> playlistItems = ((PlaylistItemListResponse) response).getItems();
				for(PlaylistItem item : playlistItems) {
					videos.add(new YouTubeVideo(item));
				}
	    		// Keep in cache
	    		YouTubeCache.putPlaylistItemListResponse(page, (PlaylistItemListResponse) response);
			}
		}
		else {
			response = YouTubeUtil.getVidoesBySearch(context, context.getString(R.string.app_name), loadCriteria, pageToken);		
			if(response != null) {
				List<SearchResult> searchResults = ((SearchListResponse) response).getItems();
				for(SearchResult result : searchResults) {
					videos.add(new YouTubeVideo(result));
				}
	    		// Keep in cache 
	    		YouTubeCache.putSearchListResponse(page, ((SearchListResponse) response));
			}
		}
		
		if(response != null) {
			try {
				// Write response to local file
				String jsonString = response.toPrettyString();
	    		// Write to local file
				if(videoSource == YouTubeVideoSource.Playlist) {
		    		FileUtil.writeStringToFile(jsonString, StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubePlaylistPersistFileName(loadCriteria, page), 
		    				context.getString(R.string.charSetName));    		
				}
				else {
		    		FileUtil.writeStringToFile(jsonString, StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubeSearchListPersistFileName(context.getString(R.string.appID), page), 
		    				context.getString(R.string.charSetName));    						
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	protected void onPostExecute(GenericJson result) {
    	if(progressDialog != null && progressDialog.isShowing() == true)
    		progressDialog.dismiss();		

		if(result != null) {
	    	if(page == 1) {	// First page does not have pageToken
				// Set token for the second page
				if(videoSource == YouTubeVideoSource.Playlist) {
					VideoListFragmentActivity.pageTokens.set(1, ((PlaylistItemListResponse) result).getNextPageToken());
				}
				else {
					VideoListFragmentActivity.pageTokens.set(1, ((SearchListResponse) result).getNextPageToken());
				}
			}
			else if(page == VideoListFragmentActivity.pageTokens.size()) {
				// Do nothing
			}
			else {
				if(videoSource == YouTubeVideoSource.Playlist) {
					VideoListFragmentActivity.pageTokens.set(page - 2, ((PlaylistItemListResponse) result).getPrevPageToken());
					VideoListFragmentActivity.pageTokens.set(page, ((PlaylistItemListResponse) result).getNextPageToken());
				}
				else {
					VideoListFragmentActivity.pageTokens.set(page - 2, ((SearchListResponse) result).getPrevPageToken());
					VideoListFragmentActivity.pageTokens.set(page, ((SearchListResponse) result).getNextPageToken());
				}
			}
		}
		MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_VIDEOS_LOADED);
	}
	
	public List<YouTubeVideo> getVideos() {
		return videos;
	}
}
