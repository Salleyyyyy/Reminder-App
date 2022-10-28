package com.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DocAppointment extends RemindService {

	public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final long serialVersionUID = 1L;
	/** Amount of minutes to remind user of doc appointment in future **/
	public final static int MIN_BEFORE_REMINDING = 60;
	private final static boolean REGULAR = false;
	private final static long REGULAR_INTERVAL = 0;
	private static final boolean UNIQUE_SERVICE = false;
	private final static String NOTIFICATION_MESSAGE = "In " + MIN_BEFORE_REMINDING
			+ " min haben Sie einen Arzt-Termin!";

	private final int hourOfDay, minute;

	public DocAppointment(long dateInMilliseconds, int hourOfDay, int minute) {
		super(RemindType.DOCAPPOINTMENT);
		super.setDate(createDate(dateInMilliseconds, hourOfDay, minute), DATE_FORMAT);
		super.setRemind(true);

		this.hourOfDay = hourOfDay;
		this.minute = minute;
	}

	public DocAppointment(String dateText, boolean remind) {
		super(RemindType.DOCAPPOINTMENT);
		Date dateAndTime = parseDateTextToDate(dateText, DATE_FORMAT);
		super.setDate(dateAndTime, DATE_FORMAT);
		super.setRemind(remind);

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateAndTime);
		this.hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
	}
	
	public int getMinute() {
		return minute;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}

	@Override
	public String getNotificationText() {
		return NOTIFICATION_MESSAGE;
	}

	@Override
	public int createUniqueID() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(super.date.getTime());
		return (calendar.get(Calendar.YEAR) % 2) * 10000000 + calendar.get(Calendar.DAY_OF_YEAR) * 10000
				+ hourOfDay * 100 + minute;
	}

	@Override
	public Date scheduleTimeToTriggerReminding() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(getTimeZoneID()));
		calendar.setTime(getDate());
		calendar.set(Calendar.MINUTE, getMinute());
		calendar.set(Calendar.HOUR_OF_DAY, getHourOfDay());
		calendar.add(Calendar.MINUTE, -MIN_BEFORE_REMINDING);
		return new Date(calendar.getTimeInMillis());
	}

	@Override
	public boolean isRegular() {
		return REGULAR;
	}

	@Override
	public long getPeriodOfReminding() {
		return REGULAR_INTERVAL;
	}
	
	@Override
	public String toString() {
		return "Arzt-Termin um " + super.dateText + " Uhr ";
	}
	
	private Date createDate(long dateInMilliseconds, int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateInMilliseconds);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}
	
	@Override
	public boolean isUnique() {
		return UNIQUE_SERVICE;
	}
}