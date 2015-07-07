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

public class PersistFileNameProvider {

    public static String getYouTubePlaylistPersistFileName(String playlistID, int page) {
    	return "YouTubeVideos-" + playlistID + "-page" + page + ".json";
    }
    
    public static String getYouTubeSearchListPersistFileName(String appID, int page) {
    	return "YouTubeVideos-App" + appID + "-page" + page + ".json";
    }
    
    public static String getYouTubePlaylistPersistFileNamePattern(String playlistID) {
    	return "YouTubeVideos-" + playlistID + "-page";
    }
	
    public static String getYouTubeSearchListPersistFileNamePattern(String appID) {
    	return "YouTubeVideos-App" + appID + "-page";
    }
    
}
