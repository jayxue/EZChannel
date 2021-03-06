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

import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ViewUtil {

	public static void setImageViewSize(ImageView imageView, int width,
			int height) {
		LayoutParams params = (LayoutParams) imageView.getLayoutParams();
		params.width = width;
		params.height = height;
		imageView.setLayoutParams(params);
	}

}
