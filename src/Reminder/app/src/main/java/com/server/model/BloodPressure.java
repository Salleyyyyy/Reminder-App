package com.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BloodPressure extends RemindService {

	private static final long serialVersionUID = 1L;
	private static final int BLOODPRESSURE_ID = -2;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final String NOTIFICATION_MESSAGE = "Bitte messen Sie jetzt Ihren Blutdruck!";
	private static final boolean REGULAR = true;
	private static final long REGULAR_INTERVAL = IntervalUnit.INTERVAL_HOUR;
	private static final boolean UNIQUE_SERVICE = true;

	public BloodPressure() {
		super(RemindType.BLOODPRESSURE, new Date(System.currentTimeMillis()), DATE_FORMAT);
	}

	public BloodPressure(String dateText, boolean remind) {
		super(RemindType.BLOODPRESSURE);
		Date dateAndTime = parseDateTextToDate(dateText, DATE_FORMAT);
		super.setDate(dateAndTime, DATE_FORMAT);
		super.setRemind(remind);
	}

	@Override
	public String getNotificationText() {
		return NOTIFICATION_MESSAGE;
	}

	@Override
	public int createUniqueID() {
		return BLOODPRESSURE_ID;
	}

	@Override
	public Date scheduleTimeToTriggerReminding() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(getTimeZoneID()));
		calendar.setTime(getDate());
		return new Date(calendar.getTimeInMillis());
	}

	@Override
	public long getPeriodOfReminding() {
		return REGULAR_INTERVAL;
	}

	@Override
	public boolean isRegular() {
		return REGULAR;
	}
	
	@Override
	public boolean isUnique() {
		return UNIQUE_SERVICE;
	}
}
