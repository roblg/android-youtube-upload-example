package com.roblg.android.youtube.client;



import java.io.IOException;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
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
	private String authHeaderValue;
	
	/**
	 * @param transport the transport that's going to send data
	 * @param devId the developer id
	 * @param authHeaderValue the Authorization header value
	 */
	public YouTubeClient(HttpTransport transport, final String devId, String authHeaderValue) {
		this.transport = transport;
		this.authHeaderValue = authHeaderValue;
		final JsonCParser parser = new JsonCParser();
		parser.jsonFactory = jsonFactory;
		requestFactory = this.transport.createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
				// headers
		        GoogleHeaders headers = new GoogleHeaders();
		        headers.setApplicationName("roblg.com-youtubetest/1.0");
		        headers.authorization = YouTubeClient.this.authHeaderValue;
		        headers.setDeveloperId(devId);
		        headers.gdataVersion = "2";
		        request.headers = headers;
		        request.addParser(parser);
			}
		});
	}
	
	public boolean authorize() throws IOException {
//		AuthorizationRequestUrl builder = new AuthorizationRequestUrl("https://www.google.com/accounts/OAuthGetRequestToken");
//		builder.redirectUri = "http://roblg.com/testOAuth";
//		builder.scope = "http://gdata.youtube.com";
//		
//		String response = requestFactory.buildGetRequest(builder).execute().parseAsString();
		
		// HttpRequest request = requestFactory.buildPostRequest(builder, null);
		// String response = request.execute().parseAsString();
		// AuthorizationResponse authResponse = new AuthorizationResponse(redirectUrl);
		
		OAuthGetTemporaryToken getTempToken = new OAuthGetTemporaryToken("https://www.google.com/accounts/OAuthGetRequestToken");
		OAuthCredentialsResponse resp = getTempToken.execute();
		
		// resp.token; 
		return true;
	}
	
	public String executeUpload(UploadRequestData data) throws IOException {
		// our upload URL is always the same. -- uploading to the logged-in user's feed
		YouTubeUrl url = YouTubeUrl.forUploadRequest();
		HttpRequest request = requestFactory.buildPostRequest(url, getUploadContent(data));
		
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
	
}
