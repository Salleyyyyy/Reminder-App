package com.server.model.parse;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.server.model.RemindService;
import com.server.model.RemindServiceType;

/**
 * This class is used for serializing and deserializing a remind service in JSON
 * representation.
 */
public class RemindServiceJsonParser {

	private Gson gson = new Gson();

	public RemindServiceJsonParser() {
	}

	/**
	 * Serializes a list of remind services to JSON.
	 * 
	 * @param remindServices: List of remind services
	 * @return JSON text representation of remind services seperated with a new line
	 */
	public String parseToJson(List<RemindService> remindServices) {
		String remindServiceList_json = "";
		for (RemindService remindService : remindServices) {
			remindServiceList_json += gson.toJson(remindService) + "\n";
		}
		return remindServiceList_json;
	}

	/**
	 * Serializes a remind service object to JSON
	 * 
	 * @param remindServiceType: Remind service object
	 * @return JSON text representation of remind service object
	 */
	public String parseToJson(RemindServiceType remindServiceType) {
		return gson.toJson(remindServiceType) + "\n";
	}

	/**
	 * Reads and creates a remind service object from his JSON representation.
	 * 
	 * @param remindServiceAsJson: Remind service as JSON text representation
	 * @return Remind service object from JSON
	 * @throws JsonSyntaxException
	 */
	public RemindService readRemindServiceFromJson(String remindServiceAsJson) throws JsonSyntaxException {
		RemindServiceType remindserviceType = gson.fromJson(remindServiceAsJson, RemindServiceType.class);
		return gson.fromJson(remindServiceAsJson, remindserviceType.getClassByEnum());
	}
}
