package com.roblg.android.youtubetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.roblg.android.youtube.client.OAuthConfig;
import com.roblg.android.youtube.client.UploadRequestData;
import com.roblg.android.youtube.client.YouTubeClient;

public class YouTubeTestActivity extends Activity {
	
	private static final int AUTH_ACTIVITY = 1;
	
	Button doAuthBtn;
	Button doUploadBtn;
	
	OAuthConfig oauthConfig;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d("youtube-test", "Testing...");
        
        doAuthBtn = (Button)findViewById(R.id.do_auth_btn);
        doUploadBtn = (Button)findViewById(R.id.do_upload_btn);
        
        doAuthBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null == oauthConfig) {
					// start activity for result?
					startActivityForResult(
						new Intent(YouTubeTestActivity.this, AuthActivity.class),
						AUTH_ACTIVITY);
				} else {
					Toast.makeText(YouTubeTestActivity.this, "Already authed!", Toast.LENGTH_LONG);
				}
			}
        });
        
        doUploadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HttpTransport transport = new NetHttpTransport();
				JsonFactory jsonFactory = new JacksonFactory();
				try {
					YouTubeClient client = YouTubeClient.buildAuthorizedClient(transport, jsonFactory, oauthConfig, getString(R.string.devId));
					
					// TODO: update this section with the path to the video file...
					File sdCardDir = Environment.getExternalStorageDirectory();
					File videoFileParentDir = new File(sdCardDir, "com.roblg.android.youtubetest");
					File videoFile = new File(videoFileParentDir, "testvideo.m4v");
					
					UploadRequestData data = new UploadRequestData();
					data.title = "Foo";
					data.category = "People";
					data.description = "A test upload";
					data.fileName = URLEncoder.encode(videoFile.getName(), "UTF-8");
					data.fileData = new FileInputStream(videoFile);
					client.executeUpload(data);
					
				} catch (IOException e) {
					// TODO: better error handling
					throw new RuntimeException("Error creating youtube client or uploading", e);
				}
				
			}
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode) {
    	case AUTH_ACTIVITY:
    		if (resultCode == Activity.RESULT_OK) {
    			oauthConfig = (OAuthConfig)data.getSerializableExtra("com.roblg.android.youtubetest.OAuthConfig");
    			Toast.makeText(this, "You are now authed.", Toast.LENGTH_LONG);
    			doUploadBtn.setVisibility(View.VISIBLE);
    		}
    		break;
    	}
    }
}