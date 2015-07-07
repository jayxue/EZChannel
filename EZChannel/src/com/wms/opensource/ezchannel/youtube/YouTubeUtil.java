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

package com.wms.opensource.ezchannel.youtube;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.activity.PlayVideoActivity;
import com.wms.opensource.ezchannel.util.DeviceUtil;

public class YouTubeUtil {
	
	/**
     * Define a global instance of a YouBube object, which will be used to make YouTube Data API requests.
     * Doc: https://developers.google.com/youtube/v3/ 
     * 
     * Reference projects:
     * 		yt-direct-lite: https://github.com/youtube/yt-direct-lite-android
     * 		YouTubeAndroidAPIDemo: https://github.com/Yene-Me/youtube/tree/master/YouTubeAndroidAPIDemo
     * 		YouTube API Samples: https://github.com/youtube/api-samples/tree/master/java
     */
    private static YouTube youtube = null;

    private static void initYouTube(String appName) {
	   	if(youtube == null) {
	   		// This object is used to make YouTube Data API requests. The last argument is required, but since we don't need anything
	   		// initialized when the HttpRequest is initialized, we override the interface and provide a no-op function.
	   		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
	   			public void initialize(HttpRequest request) throws IOException {
	                	
	   			}
	   		}).setApplicationName(appName).build();
	   	}    	
    }
    
    /**
     * Fetch overall information of a playlist. You can go to https://developers.google.com/youtube/v3/docs/channels/list, enter "contentDetails" for "part"
     * and a user name for "forUsername". In the response, you can find the ID of the "uploads" channel of the given user. 
     * For example, a response contains something like:
     *	"contentDetails": {
	 *		"relatedPlaylists": {
     *			"likes": "LL4zccsgn1Nys_sIcOYd2fTw",
     *			"uploads": "UU4zccsgn1Nys_sIcOYd2fTw"
     *		},
     *		"googlePlusUserId": "113813076890051346066"
	 *	}
	 *
     * Here "uploads":"UU4zccsgn1Nys_sIcOYd2fTw" is what you need. This ID should be written in YouTubePlaylistID in strings.xml and used in loading play list items.
     */
    public static PlaylistItemListResponse getPlaylistOverallInfo(Context context, String appName, String playlistID) {
    	try {
    		initYouTube(appName);
		   	
    		// Only load brief info, making the request quickly complete.
		    YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list("contentDetails");
		    playlistItemsRequest.setKey(ApplicationKey.getApplicationKey(context));
		    playlistItemsRequest.setPlaylistId(playlistID);
		    playlistItemsRequest.setMaxResults((long) context.getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE));
		    
		    // We only need pageInfo and nextPageToken
		    playlistItemsRequest.setFields("nextPageToken,pageInfo");
		    PlaylistItemListResponse playlistItemsResult = playlistItemsRequest.execute();
		    return playlistItemsResult;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static SearchListResponse getSearchListOverallInfo(Context context, String appName, String queryTerm) {
    	try {
    		initYouTube(appName);
		   	
    		// Only load brief info, making the request quickly complete.
		    YouTube.Search.List search = youtube.search().list("id,snippet");
		    String apiKey = ApplicationKey.getApplicationKey(context);
		    search.setKey(apiKey);
		    search.setQ(queryTerm);
		    search.setMaxResults((long) context.getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE));
		    
		    // We only need pageInfo and nextPageToken
		    search.setFields("nextPageToken,pageInfo");
		    SearchListResponse searchResponse = search.execute();
		    return searchResponse;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * When loading the list of videos, use "snippet" for part, which will provide most details. Using "contentDetails" will only provide brief information.
     *  
     * @param appName
     * @param playlistID
     * @param pageToken Retrieve the first page if it is null
     * @return
     */
    public static PlaylistItemListResponse getPlaylistItems(Context context, String appName, String playlistID, String pageToken) {
    	try {
    		initYouTube(appName);
		   	
		    YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list("snippet");
		    playlistItemsRequest.setKey(ApplicationKey.getApplicationKey(context));
		    playlistItemsRequest.setPlaylistId(playlistID);
		    playlistItemsRequest.setMaxResults((long) context.getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE));
		    if(pageToken != null) {
		    	playlistItemsRequest.setPageToken(pageToken);
		    }
		    PlaylistItemListResponse playlistItemsResult = playlistItemsRequest.execute();
		    return playlistItemsResult;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return null;    	
    }
    
    public static SearchListResponse getVidoesBySearch(Context context, String appName, String queryTerm, String pageToken) {    	
    	try {
    		initYouTube(appName);

		   	YouTube.Search.List search = youtube.search().list("id,snippet");
		    String apiKey = ApplicationKey.getApplicationKey(context);
		    search.setKey(apiKey);
		    search.setQ(queryTerm);
		    if(pageToken != null) {
		    	search.setPageToken(pageToken);
		    }
		    // Restrict the search results to only include videos. See:
		    // https://developers.google.com/youtube/v3/docs/search/list#type
		    search.setType("video");

		    search.setMaxResults((long) (long) context.getResources().getInteger(R.integer.NUMBER_OF_VIDEOS_PER_PAGE));

		    SearchListResponse searchResponse = search.execute();
		    return searchResponse;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public static String getBestFullThumbnailUrl(Context context, ThumbnailDetails thumbnailDetails) {
		int screenWidth = DeviceUtil.getDeviceWidth(context);
		if(screenWidth >= 1200) {
			if(thumbnailDetails.getMaxres() != null) {
				return thumbnailDetails.getMaxres().getUrl();
			}
			else if(thumbnailDetails.getStandard() != null) {
				return thumbnailDetails.getStandard().getUrl();
			}
			else if(thumbnailDetails.getHigh() != null) {
				return thumbnailDetails.getHigh().getUrl();
			}
			else {
				return thumbnailDetails.getMedium().getUrl();
			}
		}
		else if(screenWidth >= 640) {
			if(thumbnailDetails.getStandard() != null) {
				return thumbnailDetails.getStandard().getUrl();
			}
			else if(thumbnailDetails.getHigh() != null) {
				return thumbnailDetails.getHigh().getUrl();
			}
			else {
				return thumbnailDetails.getMedium().getUrl();
			}
		}
		else {
			if(thumbnailDetails.getHigh() != null) {
				return thumbnailDetails.getHigh().getUrl();
			}
			else {
				return thumbnailDetails.getMedium().getUrl();
			}
		}
    }
    
    /**
     * Get the best full thumbnail image according to screen size
     * 
     * @param context
     * @param video
     */
    public static String getBestFullThumbnailUrlFromPlaylistItem(Context context, PlaylistItem video) {
    	return getBestFullThumbnailUrl(context, video.getSnippet().getThumbnails());
    }
    
    public static String getBestFullThumbnailUrlFromSearchResult(Context context, SearchResult video) {
    	return getBestFullThumbnailUrl(context, video.getSnippet().getThumbnails());
    }    
    
    public static String getBestThumbnailUrl(Context context, ThumbnailDetails thumbnailDetails) {
		int screenWidth = DeviceUtil.getDeviceWidth(context);
		if(screenWidth >= 1200) {
			if(thumbnailDetails.getStandard() != null) {
				return thumbnailDetails.getStandard().getUrl();
			}
			else if(thumbnailDetails.getHigh() != null) {
				return thumbnailDetails.getHigh().getUrl();
			}
			else {
				return thumbnailDetails.getMedium().getUrl();
			}
		}
		else if(screenWidth >= 640) {
			if(thumbnailDetails.getStandard() != null) {
				return thumbnailDetails.getStandard().getUrl();
			}
			else if(thumbnailDetails.getHigh() != null) {
				return thumbnailDetails.getHigh().getUrl();
			}
			else {
				return thumbnailDetails.getMedium().getUrl();
			}			
		}
		else {
			if(thumbnailDetails.getHigh() != null)
				return thumbnailDetails.getHigh().getUrl();
			else
				return thumbnailDetails.getMedium().getUrl();
		}    	
    }
    
    /**
     * Open a PlayVideoActivity to play a given YouTube video.
     * 
     * @param context
     * @param video
     */
    public static void gotoSingleYouTubeVideoActivity(Context context, YouTubeVideo video) {
    	Intent intent = new Intent();		
		intent.setClass(context, PlayVideoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("videoId", video.getVideoId());
		intent.putExtras(bundle);
		context.startActivity(intent);
    }
}
