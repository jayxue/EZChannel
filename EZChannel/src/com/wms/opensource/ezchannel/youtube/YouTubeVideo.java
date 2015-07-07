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

import com.google.api.client.json.GenericJson;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.wms.opensource.ezchannel.util.DateTimeUtil;

import android.content.Context;

public class YouTubeVideo {

	GenericJson video = null;
			
	public YouTubeVideo(GenericJson object) {
		video = object;
	}
	
	public String getVideoId() {
		if(video instanceof PlaylistItem) {
			return ((PlaylistItem) video).getSnippet().getResourceId().getVideoId();
		}
		else {
			return ((SearchResult) video).getId().getVideoId();
		}		
	}
	
	public String getTitle() {
		if(video instanceof PlaylistItem) {
			return ((PlaylistItem) video).getSnippet().getTitle();
		}
		else {
			return ((SearchResult) video).getSnippet().getTitle();
		}
	}
	
	public String getPublishedAt() {
		if(video instanceof PlaylistItem) {
			return DateTimeUtil.getDateTimeString(((PlaylistItem) video).getSnippet().getPublishedAt());
		}
		else {
			return DateTimeUtil.getDateTimeString(((SearchResult) video).getSnippet().getPublishedAt());
		}		
	}
	
	public String getDescription() {
		if(video instanceof PlaylistItem) {
			return ((PlaylistItem) video).getSnippet().getDescription();
		}
		else {
			return ((SearchResult) video).getSnippet().getDescription();
		}		
	}
	
	public String getBestThumbnailUrl(Context context) {
		if(video instanceof PlaylistItem) {
			return YouTubeUtil.getBestThumbnailUrl(context, ((PlaylistItem) video).getSnippet().getThumbnails());
		}
		else {
			return YouTubeUtil.getBestThumbnailUrl(context, ((SearchResult) video).getSnippet().getThumbnails());
		}
	}
}
