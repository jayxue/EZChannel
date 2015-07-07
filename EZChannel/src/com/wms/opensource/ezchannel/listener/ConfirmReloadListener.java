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

package com.wms.opensource.ezchannel.listener;

import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.handler.HandlerMessage;
import com.wms.opensource.ezchannel.util.FileUtil;
import com.wms.opensource.ezchannel.util.MessageUtil;
import com.wms.opensource.ezchannel.util.StorageUtil;
import com.wms.opensource.ezchannel.youtube.PersistFileNameProvider;
import com.wms.opensource.ezchannel.youtube.YouTubeCache;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;

public class ConfirmReloadListener implements OnClickListener {

	Context context = null;
	Handler handler = null;
	YouTubeVideoSource videoSource = YouTubeVideoSource.Playlist;
	String loadCriteria = "";
	
	public ConfirmReloadListener(Context context, Handler handler, YouTubeVideoSource videoSource, String loadCriteria) {
		this.context = context;
		this.handler = handler;
		this.videoSource = videoSource;
		this.loadCriteria = loadCriteria;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		YouTubeCache.clearCache();
   		if(videoSource == YouTubeVideoSource.Playlist) {
   			FileUtil.deleteFilesInDir(StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubePlaylistPersistFileNamePattern(loadCriteria));
   		}
   		else {
   			FileUtil.deleteFilesInDir(StorageUtil.getTempDirectory(context), PersistFileNameProvider.getYouTubeSearchListPersistFileNamePattern(context.getString(R.string.appID)));
   		}

		MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_VIDEOS_COUNT_LOADED);
	}
	
}
