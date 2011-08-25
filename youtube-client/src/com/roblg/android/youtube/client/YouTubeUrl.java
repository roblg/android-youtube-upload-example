package com.roblg.android.youtube.client;

import com.google.api.client.googleapis.GoogleUrl;

// good example of getting auth token for user: http://code.google.com/p/google-api-java-client/source/browse/picasa-atom-android-sample/src/com/google/api/client/sample/picasa/PicasaAndroidSample.java?repo=samples

public class YouTubeUrl extends GoogleUrl {
	
	static final String UPLOAD_ROOT = 
		/*"https://gdata.youtube.com/feeds/api/videos?" +
		"q=GoogleDevelopers" + 
	    "&max-results=1" + 
	    "&v=2" + 
	    "&alt=jsonc";*/
	    "http://uploads.gdata.youtube.com/resumable/feeds/api/users/default/uploads?alt=jsonc&v=2";
	
		// TODO FIXME uploads URL doesn't work. 404s. weird.
	
	static final String LOGIN_ROOT = ""; 
	
	YouTubeUrl(String encodedUrl) {
		super(encodedUrl);
		// this.alt = "jsonc";
	}
	
	/**
	 * Returns a YouTubeUrl suitable for uploading to the logged-in user's account. Making 
	 * a POST to this URL will return a redirect to another URL that the file contents should
	 * be sent to.
	 * @return
	 */
	public static YouTubeUrl forUploadRequest() {
		YouTubeUrl url = new YouTubeUrl(UPLOAD_ROOT);
		return url;
	}
	
}
