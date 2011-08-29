package com.roblg.android.youtube.client;

import java.io.IOException;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

// based on:
// http://code.google.com/p/google-api-java-client/source/browse/youtube-jsonc-sample/src/main/java/com/google/api/client/sample/youtube/YouTubeClient.java?repo=samples
public class YouTubeClient {
	
	private final JsonFactory jsonFactory = new JacksonFactory();
	
	private final HttpTransport transport;

	private HttpRequestFactory requestFactory;
	
	/**
	 * @param transport the transport that's going to send data
	 * @param devId the developer id
	 * @param authHeaderValue the Authorization header value
	 */
	private YouTubeClient(YouTubeProtectedResource requestInitializer) {
		this.transport = requestInitializer.getTransport();
		final JsonCParser parser = new JsonCParser();
		parser.jsonFactory = jsonFactory;
		requestFactory = this.transport.createRequestFactory(requestInitializer);
	}
	
	public static YouTubeClient buildAuthorizedClient(HttpTransport transport, JsonFactory jsonFactory, OAuthConfig oauthConfig, String devId) throws IOException {
		// TODO: maybe full this first part out
		GoogleAuthorizationCodeGrant authRequest = new GoogleAuthorizationCodeGrant(transport,
				jsonFactory, oauthConfig.getOauthClientId(), oauthConfig.getOauthClientSecret(),
				oauthConfig.getOauthAuthorizationCode(), oauthConfig.getOauthRedirectUri());
		authRequest.useBasicAuthorization = false;
		AccessTokenResponse authResponse  = authRequest.execute();
		oauthConfig.setOauthAccessToken(authResponse.accessToken);
		oauthConfig.setOauthRefreshToken(authResponse.refreshToken);
		
		YouTubeProtectedResource initializer = new YouTubeProtectedResource(
				oauthConfig.getOauthAccessToken(), 
				transport, 
				jsonFactory, 
				oauthConfig.getOauthClientId(), 
				oauthConfig.getOauthClientSecret(), 
				oauthConfig.getOauthRefreshToken(), 
				devId);
		
		return new YouTubeClient(initializer);
	}
	
	public boolean authorize() throws IOException {
		return false;
	}
	
	public String executeUpload(UploadRequestData data) throws IOException {
		// our upload URL is always the same. -- uploading to the logged-in user's feed
		YouTubeUrl url = YouTubeUrl.forUploadRequest();
		HttpRequest request = requestFactory.buildPostRequest(url, getUploadContent(data));
		
		// blech... because YouTubeProtectedResource extends GoogleAccessProtectedResource and that class
		// make initialize final... we have to set up the stupid headers here 
		request.headers = new GoogleHeaders();
		
		// here, HttpRequest already has headers that have the app name and the developer id, but
		// I think we need to add a slug too...
		GoogleHeaders.class.cast(request.headers).slug = data.fileName;
		System.out.println(request.content);
		System.out.println(request.headers);
		
		HttpResponse initialUploadResponse = null;
		String uploadUrl = null;
		try {
			initialUploadResponse = request.execute();
			System.out.println("Resp: " + initialUploadResponse.parseAsString());
			System.out.println("Resp Headers: " + initialUploadResponse.headers.toString());	
			uploadUrl = initialUploadResponse.headers.location;
			System.out.println(uploadUrl);
		} catch (HttpResponseException e) {
			// blech. icky error string
			System.out.println(e.response.parseAsString());
		}
		
		if (null != uploadUrl) {
			InputStreamContent content = new InputStreamContent();
			content.inputStream = data.fileData;
			content.type = "video/x-m4v";
			HttpRequest actualRequest = requestFactory.buildPutRequest(new GenericUrl(uploadUrl), content);
			actualRequest.headers = new GoogleHeaders();
			try {
				actualRequest.execute().parseAsString();
			} catch (HttpResponseException e) {
				// TODO FIXME
				System.err.println(e.response.parseAsString());
			}
		} else {
			System.out.println("URL is null...skipping upload");
		}
		
		return null;
	}
	
	private HttpContent getUploadContent(UploadRequestData data) {
		JsonCContent content = new JsonCContent();
		content.jsonFactory = this.jsonFactory;
		content.data = data;
		return content;
	}
	
	private static class YouTubeProtectedResource extends GoogleAccessProtectedResource {
		
		private final String devId;

		public YouTubeProtectedResource(String accessToken,
				HttpTransport transport, JsonFactory jsonFactory,
				String clientId, String clientSecret, String refreshToken, String devId) {
			super(accessToken, transport, jsonFactory, clientId, clientSecret, refreshToken);
			this.devId = devId;
		}
		
		// AccessProtectedResource makes initialize() final, so we're taking advantage
		// of the fact that it also functions as a request interceptor...
		@Override
		public void intercept(HttpRequest request) throws IOException {
			// we need GoogleHeaders here, but initialize() is final... so whoever is making the 
			// request has to modify it before executing... grody. 
			GoogleHeaders headers = (GoogleHeaders)request.headers;
			headers.setApplicationName("roblg.com-youtubetest/1.0");
			headers.gdataVersion = "2";
			headers.setDeveloperId(devId);
			request.headers = headers;
			// let the superclass do its business
			super.intercept(request);
		}
		
	}
	
	
}