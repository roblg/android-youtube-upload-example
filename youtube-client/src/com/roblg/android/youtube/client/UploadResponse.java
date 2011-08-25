package com.roblg.android.youtube.client;

import com.google.api.client.util.Key;

public class UploadResponse {
	@Key public String alt;
	@Key("upload_id") public String uploadId;
	@Key("v") public String version;
}
