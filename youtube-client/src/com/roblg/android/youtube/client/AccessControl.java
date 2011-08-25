package com.roblg.android.youtube.client;

import com.google.api.client.util.Key;

public class AccessControl {
	
	// e.g. "allowed"
	@Key public String syndicate;
	
	// e.g. "allowed"
	@Key public String embed;
	
	// e.g. "allowed"
	@Key public String rate;
	
	// e.g. "allowed"
	@Key public String list;
	
	// e.g. "moderated"
	@Key public String comment;
	
	// e.g. "allowed"
	@Key public String commentVote;
	
	// TODO if necessary
	// @Key public Group group;
	
	
}


