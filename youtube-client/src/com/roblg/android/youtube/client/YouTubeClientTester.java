package com.roblg.android.youtube.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class YouTubeClientTester {
	
	public static void main(String[] args) throws IOException {
		// TODO: auth?
		
		HttpTransport transport = new NetHttpTransport();
		
		
		// client.authorize();
		
		Scanner s = new Scanner(System.in);
		
		System.out.print("Dev ID file: ");
		String devIdFileName = s.nextLine();
		
		Scanner devIdScanner = new Scanner(new File(devIdFileName));
		String devId = devIdScanner.nextLine();
		
		System.out.print("User: ");
		String user = s.nextLine();
		
		System.out.print("PW: ");
		String pw = s.nextLine();
		
		ClientLogin login = new ClientLogin();
		login.transport = transport;
		login.accountType = "GOOGLE";
		login.authTokenType = "youtube";
		login.username = user;
		login.password = pw;
		
		Response resp = login.authenticate();
		System.out.println(resp.getAuthorizationHeaderValue());
		/*
		YouTubeClient client = new YouTubeClient(transport, devId, resp.getAuthorizationHeaderValue());
		UploadRequestData data = new UploadRequestData();
		data.title = "Foo";
		data.category = "People";
		data.description = "A test upload";
		data.fileName = URLEncoder.encode("testvideo.m4v", "UTF-8");
		data.fileData = new FileInputStream(new File("/Users/robert/Desktop/testvideo.m4v"));
		System.out.println(client.executeUpload(data));
		*/
	}
	
}
	