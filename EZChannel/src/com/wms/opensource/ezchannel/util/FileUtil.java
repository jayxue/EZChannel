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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.os.Environment;

public class FileUtil {

	public static boolean writeStringToFile(String str, File cacheDir, String fileName, String charsetName) {
		File file = new File(cacheDir, fileName);
		File parent = file.getParentFile();
		if(!parent.exists()) {
			if(!parent.mkdirs()) {
				return false;
			}
		}
		if(!file.exists()) {
			try {
				file.createNewFile();
			} 
			catch (IOException e) {
				return false;
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(file, false);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, charsetName);
			outputStreamWriter.write(str);
			outputStreamWriter.close();
			out.close();
			return true;
		} 
		catch (IOException e) {
			return false;
		}		
	}
	
	public static String getStringFromFileInCache(File cacheDir, String fileName, String charsetName) {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferReader = null;
		try {
			File file = new File(cacheDir, fileName);
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				bufferReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));

				String line;
				while ((line = bufferReader.readLine()) != null) {
					if (line.equals(""))
						continue;
					stringBuilder.append(line + "\n");
				}
				bufferReader.close();
				inputStream.close();
			}
		} 
		catch (IOException e) {

		} 
		finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} 
			catch (IOException e) {

			}
		}
		return stringBuilder.toString();
	}
	
	public static boolean fileExist(String filePath) {
		File file = new File(filePath);
		if(file.exists() && file.isFile())
			return true;
		else
			return false;
	}
	
	public static String getAppExternalStoragePath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + context.getApplicationContext().getPackageName();
	}

	public static void deleteFilesInDir(File dir, String namePattern) {
		if(dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            if(namePattern.equals("*") || children[i].contains(namePattern))
	            	new File(dir, children[i]).delete();
	        }
		}
	}

}
