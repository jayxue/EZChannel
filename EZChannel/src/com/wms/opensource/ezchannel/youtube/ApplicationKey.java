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

import com.wms.opensource.ezchannel.R;

import android.content.Context;

public class ApplicationKey {
	
	/**
	 * Please set up AndroidApplicationKey in strings.xml which is enabled for the 
	 * YouTube Data API v3 service. Go to the 
	 * <a href="https://console.developers.google.com">Google Developers Console</a> to
	 * create a Google API project and create a key for Android applications. 
	 */
	private static String applicationKey = null;
	
	/**
	 * Return application key, which is Google API project's key for Android applications.
	 */
  	public static String getApplicationKey(Context context) {
  		if(applicationKey == null)
  			applicationKey = context.getString(R.string.AndroidApplicationKey);
  		
  		return applicationKey;
  	}
  	
}
