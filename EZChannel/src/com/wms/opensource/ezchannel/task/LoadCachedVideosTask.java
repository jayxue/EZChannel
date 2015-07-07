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

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.MessageUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.PersistFileNameProvider;
import com.wms.opensource.ezchannel.youtube.YouTubeCache;
import com.wms.opensource.ezchannel.youtube.YouTubeVideo;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class LoadCachedVideosTask extends AsyncTask<Integer, Void, Void> {

	private Context context = null;
	private ProgressBar progressBar = null;
	private YouTubeVideoSource videoSource = YouTubeVideoSource.Playlist;
	private Handler handler = null;
	private String playlistID = "";
	private int page = 1;	
	
	private List<YouTubeVideo> videos = new ArrayList<YouTubeVideo>();
	
	public LoadCachedVideosTask(Context context, Handler handler, YouTubeVideoSource videoSource, ProgressBar progressBar, String playlistID, int page) {
		this.context = context;
		this.handler = handler;
		this.videoSource = videoSource;
		this.progressBar = progressBar;
		this.playlistID = playlistID;
		this.page = page;
	}
	
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
    }
	
	@Override
	protected Void doInBackground(Integer... params) {
		if(videoSource == YouTubeVideoSource.Playlist) {
			if(YouTubeCache.containsPage(page, videoSource)) {
				PlaylistItemListResponse playlistItemListResponse = YouTubeCache.getPlaylistItemListResponse(page);
				List<PlaylistItem> playlistItems = playlistItemListResponse.getItems();
				for(PlaylistItem item : playlistItems) {
					videos.add(new YouTubeVideo(item));
				}
				return null;
			}
		}
		else {
			if(YouTubeCache.containsPage(page, videoSource)) {
				SearchListResponse searchListResponse = YouTubeCache.getSearchListResponse(page);
				List<SearchResult> searchResults = searchListResponse.getItems();
				for(SearchResult result : searchResults) {
					videos.add(new YouTubeVideo(result));
				}
				return null;
			}
		}
		JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
		String jsonString = "";
		try {
			if(videoSource == YouTubeVideoSource.Playlist) {
				jsonString = FileUtil.getStringFromFileInCache(StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubePlaylistPersistFileName(playlistID, page), context.getString(R.string.charSetName));
				PlaylistItemListResponse playlistItemListResponse = jacksonFactory.fromString(jsonString, PlaylistItemListResponse.class);
				List<PlaylistItem> playlistItems = playlistItemListResponse.getItems();
				for(PlaylistItem item : playlistItems) {
					videos.add(new YouTubeVideo(item));
				}
	    		// Keep in cache
	    		YouTubeCache.putPlaylistItemListResponse(page, playlistItemListResponse);
			}
			else {
				jsonString = FileUtil.getStringFromFileInCache(StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubeSearchListPersistFileName(context.getString(R.string.appID), page), context.getString(R.string.charSetName));
				SearchListResponse searchListResponse = YouTubeCache.getSearchListResponse(page);
				List<SearchResult> searchResults = searchListResponse.getItems();
				for(SearchResult result : searchResults) {
					videos.add(new YouTubeVideo(result));
				}
	    		// Keep in cache
	    		YouTubeCache.putSearchListResponse(page, searchListResponse);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {  
		progressBar.setVisibility(View.INVISIBLE);
		MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_VIDEOS_LOADED_FROM_LOCAL);
	}

	public List<YouTubeVideo> getVideos() {
		return videos;
	}
}
