package com.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Water model class. Notifications of this remind service are repetitive for
 * every half an hour. Furthermore, this service is unique, that means there are
 * no more instances.
 */
public class Water extends RemindService {

	private static final long serialVersionUID = 1L;
	private static final int WATER_ID = -1;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final String NOTIFICATION_MESSAGE = "Bitte trinken Sie jetzt ein Glas Wasser!";
	private static final boolean REGULAR = true;
	private static final long REGULAR_INTERVAL = IntervalUnit.INTERVAL_HALF_AN_HOUR;
	private static final boolean UNIQUE_SERVICE = true;

	/**
	 * Creates a Water remind service starting at the time of creation.
	 */
	public Water() {
		super(RemindType.WATER, new Date(System.currentTimeMillis()), DATE_FORMAT);
	}

	/**
	 * Creates a Water remind service starting at given date and time.
	 * 
	 * @param dateText: Text presentation of date object
	 * @param remind:   Reminding of user or not for canceling
	 */
	public Water(String dateText, boolean remind) {
		super(RemindType.WATER);
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
		return WATER_ID;
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
