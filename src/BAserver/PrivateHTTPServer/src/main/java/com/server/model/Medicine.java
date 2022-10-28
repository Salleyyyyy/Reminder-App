package com.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Medicine model class. Notifications of this remind service are repetitive for
 * every day. The user is notified one time for one registered medicine remind
 * service. Furthermore, this service is not unique, more than one of this
 * remind services can be created.
 */
public class Medicine extends RemindService {

	private static final long serialVersionUID = 1L;
	/**
	 * The date format only contains time of taking medicine as notifications are
	 * pushed every day.
	 */
	private static final String DATE_FORMAT = "HH:mm";
	private static final String NOTIFICATION_MESSAGE = "Bitte nehmen Sie jetzt Ihr(e) Medikament(e) ein!";
	private static final boolean REGULAR = true;
	private static final long REGULAR_INTERVAL = IntervalUnit.INTERVAL_DAY;
	private static final boolean UNIQUE_SERVICE = false;

	/**
	 * Hour of day for the doc appointment (24h presentation)
	 */
	private final int hourOfDay;

	/**
	 * Minute of doc appointment
	 */
	private final int minute;

	/**
	 * Creates a medicine remind service for for certain hour and minute.
	 * 
	 * @param hourOfDay: Hour of day (24 presentation)
	 * @param minute:    Exact minutes
	 */
	public Medicine(int hourOfDay, int minute) {
		super(RemindType.MEDICINE);
		super.setDate(createDate(hourOfDay, minute), DATE_FORMAT);
		super.setRemind(true);

		this.hourOfDay = hourOfDay;
		this.minute = minute;
	}

	/**
	 * Creates a Medicine remind service with a text presentation of the date.
	 * 
	 * @param dateText: Text presentation of date
	 * @param remind:   Remind user of this or not for canceling
	 */
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

	/**
	 * Creates a date object that only contains time info.
	 * 
	 * @param hourOfDay: Hour of day (24h presentation)
	 * @param minute:    Exact minutes
	 * @return Date object that contains time info. Date info can be ignored as this
	 *         remind service is repeated every day.
	 */
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
		// Two Medicine remind services are equals if hour and minute are identical
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