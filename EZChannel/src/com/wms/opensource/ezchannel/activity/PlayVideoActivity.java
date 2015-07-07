/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.youtube.ApplicationKey;

import android.os.Bundle;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 * 
 * Waterloo Mobile Studio enhancement: in EZChannel, before opening PlayVideoActivity, you need to provide a "videoId" parameter.
 * See {@link com.wms.opensource.ezchannel.youtube.YouTubeUtil#gotoSingleYouTubeVideoActivity(android.content.Context, com.wms.opensource.ezchannel.youtube.YouTubeVideo)}.
 */
public class PlayVideoActivity extends YouTubeFailureRecoveryActivity {

	private String videoId = ""; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_playerview);
		
        Bundle bundle = this.getIntent().getExtras();
        videoId = (String) bundle.getString("videoId");
        
	    YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
	    youTubeView.initialize(ApplicationKey.getApplicationKey(this), this);
	}
	
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		if (!wasRestored) {			
			player.cueVideo(videoId);
		}
	}
	
	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

}
