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

package com.wms.opensource.ezchannel.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtil {

	public static ProgressDialog showWaitingProgressDialog(Context context, int style, String message, boolean cancelable) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(style);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancelable);
		
		// Avoid window leading exception after existing an activity
		try {
			progressDialog.show();
		}
		catch(Exception e) {
			
		}
		return progressDialog;
	}

	public static void showExceptionAlertDialog(Context context, String title,
			String message) {
		Builder exceptionAlertDialogBuilder = new Builder(context);
		exceptionAlertDialogBuilder.setTitle(title).setMessage(message)
				.setCancelable(true).setNeutralButton("OK", null);
		AlertDialog alert = exceptionAlertDialogBuilder.create();
		
		// Avoid window leading exception after existing an activity
		try {
			alert.show();
		}
		catch(Exception e) {
			
		}			
	}

}
