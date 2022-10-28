package com.server.model.parse;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.server.model.RemindService;
import com.server.model.RemindServiceType;

public class RemindServiceJsonParser {

	private Gson gson = new Gson();

	public RemindServiceJsonParser() {
	}

	public String parseToJson(List<RemindService> remindServices) {
		String remindServiceList_json = "";
		for (RemindService remindService : remindServices) {
			// TODO PARSING ERROR -- Remove
			// Date is irrelevant for client otherwise errors will appear
			// remindService.setDateToNull();
			// gson = new GsonBuilder().setDateFormat(remindService.getDateFormat()).create();
			remindServiceList_json += gson.toJson(remindService) + "\n";
		}
		return remindServiceList_json;
	}
	
	public String parseToJson(RemindServiceType remindServiceType) {
		return gson.toJson(remindServiceType) + "\n";
	}

	public RemindService readRemindServiceFromJson(String remindServiceAsJson) throws JsonSyntaxException {
		RemindServiceType remindserviceType = gson.fromJson(remindServiceAsJson, RemindServiceType.class);
		return gson.fromJson(remindServiceAsJson, remindserviceType.getClassByEnum());
	}
}
