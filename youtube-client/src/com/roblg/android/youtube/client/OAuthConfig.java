package com.roblg.android.youtube.client;

import java.io.Serializable;

public class OAuthConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String oauthClientId;
	private String oauthClientSecret;
	private String oauthRedirectUri;
	private String oauthAuthCode;
	
	private String oauthAccessToken;
	private String oauthRefreshToken;
	
	public OAuthConfig() {}
	
	public OAuthConfig(String oauthClientId, 
			String oauthClientSecret,
			String oauthRedirectUri, 
			String oauthAuthCode) {
		this.oauthClientId = oauthClientId;
		this.oauthClientSecret = oauthClientSecret;
		this.oauthRedirectUri = oauthRedirectUri;
		this.oauthAuthCode = oauthAuthCode;
	}
	
	public String getOauthClientId() {
		return oauthClientId;
	}
	public void setOauthClientId(String oauthClientId) {
		this.oauthClientId = oauthClientId;
	}
	public String getOauthClientSecret() {
		return oauthClientSecret;
	}
	public void setOauthClientSecret(String oauthClientSecret) {
		this.oauthClientSecret = oauthClientSecret;
	}
	public String getOauthRedirectUri() {
		return oauthRedirectUri;
	}
	public void setOauthRedirectUri(String oauthRedirectUri) {
		this.oauthRedirectUri = oauthRedirectUri;
	}
	public String getOauthAuthorizationCode() {
		return oauthAuthCode;
	}

	public void setOauthAuthorizationCode(String oauthAuthCode) {
		this.oauthAuthCode = oauthAuthCode;
	}

	public String getOauthAccessToken() {
		return oauthAccessToken;
	}
	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}
	public String getOauthRefreshToken() {
		return oauthRefreshToken;
	}
	public void setOauthRefreshToken(String oauthRefreshToken) {
		this.oauthRefreshToken = oauthRefreshToken;
	}
	
	
}
