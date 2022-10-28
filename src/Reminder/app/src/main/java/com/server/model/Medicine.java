package com.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Medicine extends RemindService {

	private static final long serialVersionUID = 1L;
	private static final String DATE_FORMAT = "HH:mm";
	private static final String NOTIFICATION_MESSAGE = "Bitte nehmen Sie jetzt Ihr(e) Medikament(e) ein!";
	private static final boolean REGULAR = true;
	private static final long REGULAR_INTERVAL = IntervalUnit.INTERVAL_DAY;
	private static final boolean UNIQUE_SERVICE = false;

	private final int hourOfDay, minute;

	public Medicine(int hourOfDay, int minute) {
		super(RemindType.MEDICINE);
		super.setDate(createDate(hourOfDay, minute), DATE_FORMAT);
		super.setRemind(true);

		this.hourOfDay = hourOfDay;
		this.minute = minute;
	}

	public Medicine(String dateText, boolean remind) {
		super(RemindType.MEDICINE);
		Date time = parseDateTextToDate(dateText, DATE_FORMAT);
		super.setDate(time, DATE_FORMAT);
		super.setRemind(remind);

		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		this.hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
	}

	private Date createDate(int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	@Override
	public String toString() {
		return "Medikament-Einnahme um " + super.dateText + " Uhr";
	}

	@Override
	public String getNotificationText() {
		return NOTIFICATION_MESSAGE;
	}

	@Override
	public int createUniqueID() {
		return hourOfDay * 100 + minute;
	}

	@Override
	public Date scheduleTimeToTriggerReminding() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneID));
		calendar.set(Calendar.MINUTE, getMinute());
		calendar.set(Calendar.HOUR_OF_DAY, getHourOfDay());
		return new Date(calendar.getTimeInMillis());
	}

	@Override
	public long getPeriodOfReminding() {
		return REGULAR_INTERVAL;
	}

	public int getMinute() {
		return minute;
	}

	public int getHourOfDay() {
		return hourOfDay;
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