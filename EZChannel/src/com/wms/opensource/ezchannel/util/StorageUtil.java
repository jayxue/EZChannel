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

package com.wms.opensource.ezchannel.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class StorageUtil {

	/**
	 * Get temporary storage directory of an app
	 * 
	 * @param context
	 * @return
	 */
	public static String getTempDirectoryPath(Context context) {
		File cache = getTempDirectory(context);
		return cache.getAbsolutePath();
	}

	public static File getTempDirectory(Context context) {
		File cache;
		// SD Card Mounted
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cache = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Android/data/"
					+ context.getPackageName() + "/cache/");
		} else { 
			// Use internal storage
			cache = context.getCacheDir();
		}

		// Create the cache directory if it doesn't exist
		if (!cache.exists()) {
			cache.mkdirs();
		}
		return cache;
	}

}
