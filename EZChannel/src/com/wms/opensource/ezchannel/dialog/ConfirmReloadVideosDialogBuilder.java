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

package com.wms.opensource.ezchannel.dialog;

import com.wms.opensource.ezchannel.R;
import com.wms.opensource.ezchannel.listener.ConfirmReloadVideosDialogBuilderOnClickListener;
import com.wms.opensource.ezchannel.youtube.YouTubeVideoSource;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Handler;

public class ConfirmReloadVideosDialogBuilder extends Builder {

	public ConfirmReloadVideosDialogBuilder(Context context, Handler handler, YouTubeVideoSource youtubeVideoSource, String playlistID) {
		super(context);

		this.setCancelable(false);
		this.setMessage(context.getString(R.string.confirmReloadVideos));
		this.setNegativeButton(context.getString(R.string.cancel), null);
		this.setPositiveButton(context.getString(R.string.yes), new ConfirmReloadVideosDialogBuilderOnClickListener(context, handler, youtubeVideoSource, playlistID));		
	}

}
