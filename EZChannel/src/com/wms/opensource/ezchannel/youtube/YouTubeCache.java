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

package com.wms.opensource.ezchannel.youtube;

import android.util.SparseArray;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

public class YouTubeCache {

	private static SparseArray<PlaylistItemListResponse> playlistItemListResponsesCache = new SparseArray<PlaylistItemListResponse>();
	private static SparseArray<SearchListResponse> searchListResponseCache = new SparseArray<SearchListResponse>();
	
	public static void putPlaylistItemListResponse(int page, PlaylistItemListResponse response) {
		playlistItemListResponsesCache.put(page, response);
	}
	
	public static PlaylistItemListResponse getPlaylistItemListResponse(int page) {
		return playlistItemListResponsesCache.get(page);
	}
	
	public static void putSearchListResponse(int page, SearchListResponse response) {
		searchListResponseCache.put(page, response);
	}
	
	public static SearchListResponse getSearchListResponse(int page) {
		return searchListResponseCache.get(page);
	}
	
	public static boolean isCacheEmpty(YouTubeVideoSource source) {
		if(source == YouTubeVideoSource.Playlist) {
			return playlistItemListResponsesCache.size() == 0;
		}
		else {
			return searchListResponseCache.size() == 0;
		}			
	}
	
	public static boolean containsPage(int page, YouTubeVideoSource source) {		
		if(source == YouTubeVideoSource.Playlist) {
			return playlistItemListResponsesCache.get(page) != null;
		}
		else {
			return searchListResponseCache.get(page) != null;
		}
	}
	
	public static void clearCache() {
		playlistItemListResponsesCache.clear();
		searchListResponseCache.clear();		
	}

}
