package com.roblg.android.youtubetest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.roblg.android.youtube.client.OAuthConfig;

public class AuthActivity extends Activity {
	
	WebView webView;
	
	private static final String YOUTUBE_SCOPE = "http://gdata.youtube.com";
	
	String oauthClientId;
	String oauthClientSecret;
	String oauthRedirectUri;
	
	String oauthCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_main);
		readOAuthConfig();
		
		// the base URI - http://www.google.com/blah/blah/blah -- no query params
		/*Uri authBaseUri = Uri.parse(getString(R.string.oauth_auth_request_url));
		authBaseUri.buildUpon()
			.appendQueryParameter("client_id", oauthClientId)
			.appendQueryParameter("redirect_uri", oauthRedirectUri)
			.appendQueryParameter("response_type", "code");
		*/
		webView = (WebView)findViewById(R.id.auth_web_view);
		
		String authorizeUrl = new GoogleAuthorizationRequestUrl(oauthClientId, oauthRedirectUri, YOUTUBE_SCOPE).build();		 
		webView.loadUrl(authorizeUrl);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (url.startsWith(oauthRedirectUri) && null == oauthCode) {
					Uri redirectUri = Uri.parse(url);
					webView.stopLoading();
					Intent result = new Intent();
					result.putExtra("com.roblg.android.youtubetest.OAuthConfig", new OAuthConfig(
						oauthClientId,
						oauthClientSecret,
						oauthRedirectUri,
						redirectUri.getQueryParameter("code")
					));
					setResult(Activity.RESULT_OK, result);
					finish();
				}
			}
		});
	}
	
	private void readOAuthConfig() {
		oauthClientId = getString(R.string.oauth_clientId);
		oauthClientSecret = getString(R.string.oauth_secret);
		oauthRedirectUri = getString(R.string.oauth_redirect_uri);
	}
	
}
