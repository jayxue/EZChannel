# EZChannel - Android library for eazy creation of YouTube channel apps

An Android library that helps easily create apps with YouTube channel for users or searches.

![Demo Screenshot 1](https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/screenshot_1.png)
![Demo Screenshot 2](https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/screenshot_2.png)
![Demo Screenshot 3](https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/screenshot_3.png)
![Demo Screenshot 4](https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/screenshot_4.png)
![Demo Screenshot 5](https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/screenshot_5.png)

Check EZChannel Demo application on GooglePlay:<br />
<a target="_blank" href="https://play.google.com/store/apps/details?id=com.wsm.opensource.ezchannel.demo">
  <img alt="Get it on Google Play" src="https://github.com/jayxue/EZChannel/blob/master/EZChannel/res/raw/google_play.png" />
</a>

Details
-------
This Android library facilitates developers to create Android applications with personalized YouTube channels.

The major features are:
* Fetch videos uploaded by a user or videos searched by query term and display in lists.
* Browse videos on multiple pages with view page indicator.
* Display video description in expandable panel.
* Preview enlarged video thumbnail image.
* Watch video in full-screen activity.
* Automatically check new videos of the given user or query terms and ask for confirmation for reloading videos.

Usage
-----

In order to utilize this library, you just need to do some configurations without writing any code.
* Import all the three projects into workspace: android-support-v7-support, AndroidViewPageIndicator and EZChannel. You can also get android-support-v7-support from Android SDK. The other one, EZChannelSampleApp, is an app showing how to use this library.
* In your application, include ```EZChannel``` as library.
* In your application's ```AndroidManifest.xml```, make sure that you have the following permissions:
  * ```android.permission.INTERNET```
  * ```android.permission.ACCESS_NETWORK_STATE```
  * ```android.permission.ACCESS_WIFI_STATE```
  * ```android.permission.WRITE_EXTERNAL_STORAGE```
  * ```com.android.launcher.permission.INSTALL_SHORTCUT```
* In your application's ```AndroidManifest.xml```, include two activities:
  * ```com.wms.opensource.ezchannel.activity.VideoListFragmentActivity```
  * ```com.wms.opensource.ezchannel.activity.PlayVideoActivity```
* In your applications' ```res/values/strings.xml```,
  * Set ```appID``` (no space or special characters);
  * Set ```YouTubePlaylistID``` if you want videos of a YouTube user, or ```YouTubeQueryTerm``` if you want videos based on a query term. See comments in EZChannel/res/values/strings.xml for details.
  * Set ```AndroidApplicationKey``` in order to access YouTube API (you also need to set this if you want to try the demo app). See comments in EZChannel/res/values/strings.xml for details.
* You may override ```NUMBER_OF_VIDEOS_PER_PAGE``` in ```res/values/integers.xml``` of your application to adjust how many videos to show on each page.
* You may override ```MAX_PAGE_COUNT``` in ```res/values/integers.xml``` of your application to adjust the maximal number of pages allowed in your application.

Acknowledgement
---------------

This library utilizes the following libraries/contributions:
* Android ViewPageIndicator developed by Patrik Åkerfeldt/Jake Wharton: https://github.com/JakeWharton/ViewPagerIndicator
* Android Query: https://code.google.com/p/android-query/
* ExpandablePanel developed by Andrew Halberstadt: http://ahal.ca/blog/2011/android-expandablepanel/
* Many Google libraries

Developer
---------
* Jay Xue <yxue24@gmail.com>, Waterloo Mobile Studio

License
-------

    Copyright 2015 Waterloo Mobile Studio

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
