package com.server.model;

import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Model super class for all remind services. This class especially contains
 * date info for scheduling push time. All push time points are based on the
 * time zone of the operating host system in order to avoid time zone conflicts.
 */
public abstract class RemindService extends RemindServiceType implements Serializable {

	/** Date object contains date and/or time information */
	protected Date date;

	/** Text representation of date */
	protected String dateText;

	/** Remind user or not, default is true */
	protected boolean remind = true;

	/** Date format for date */
	protected String dateFormat = "";

	/** Time Zone of the host */
	protected String timeZoneID = getTimeZoneIDOfHost();

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a remind service.
	 *
	 * @param remindType Type of Remind Service
	 * @param date       Date that contains all necessary date and time information
	 * @param dateFormat String that determines how to format the date
	 */
	public RemindService(RemindType remindType, Date date, String dateFormat) {
		super(remindType);
		this.date = normalizeDate(date);
		this.dateFormat = dateFormat;
		this.dateText = formatDateTime(date, dateFormat);
	}

	/**
	 * Creates an empty remind service of the specific remind type
	 * 
	 * @param remindType: Remind type of remind service
	 */
	public RemindService(RemindType remindType) {
		super(remindType);
	}

	/**
	 * Formats the date with a date format.
	 * 
	 * @param date:       Date object which contains info of date or time
	 * @param dateFormat: Date format like "DD.MM.YYYY"
	 * @return
	 */
	public static String formatDateTime(Date date, String dateFormat) {
		Format formatter = new SimpleDateFormat(dateFormat, Locale.GERMAN);
		return formatter.format(date);
	}

	/**
	 * Parses a date text with a certain date format.
	 * 
	 * @param dateText:   String thats contains the date representation
	 * @param dateFormat: Format of date in dateText
	 * @return Date object after successful parsing or null
	 */
	public static Date parseDateTextToDate(String dateText, String dateFormat) {
		Date date = null;
		Format formatter = new SimpleDateFormat(dateFormat, Locale.GERMAN);
		try {
			date = (Date) formatter.parseObject(dateText);
		} catch (ParseException e) {
			System.out.println("Exception: " + e);
		}
		return date;
	}

	public String getTimeZoneID() {
		return timeZoneID;
	}

	public String getFormattedDateText() {
		return dateText;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setRemind(boolean remind) {
		this.remind = remind;
	}

	public boolean getRemind() {
		return remind;
	}

	/**
	 * Sets a date that is formated with the date format given
	 * 
	 * @param date:       Date object
	 * @param dateFormat: Date Format like "DD-MM-YYYY"
	 */
	public void setDate(Date date, String dateFormat) {
		// Date objects get normalized for easier testing
		this.date = normalizeDate(date);
		this.dateText = formatDateTime(date, dateFormat);
	}

	public Date getDate() {
		return date;
	}

	/** Repetitive remind service or not **/
	public abstract boolean isRegular();

	/** Period of repetition **/
	public abstract long getPeriodOfReminding();

	/**
	 * Schedules the exact time when to send a notification of this remind service.
	 * 
	 * @return Scheduled date to exactly push on time
	 */
	public abstract Date scheduleTimeToTriggerReminding();

	/**
	 * Returns the notification text of remind service.
	 * 
	 * @return Notification text
	 */
	public abstract String getNotificationText();

	/**
	 * Creates a unique ID for the remind service.
	 * 
	 * @return Unique remind service id
	 */
	public abstract int createUniqueID();

	/**
	 * Is this remind service unique to avoid duplicates.
	 * 
	 * @return true if remind service can only be registered one time, otherwise
	 *         false
	 */
	public abstract boolean isUnique();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RemindService otherRemindService = (RemindService) o;
		// Equality is checked with the help of created unique remind service Ids
		if (createUniqueID() == otherRemindService.createUniqueID())
			return true;
		else
			return false;
	}

	/**
	 * Normalizes a date object, that means seconds and milliseconds are discarded.
	 * E.g. 14:30:01:22 is normalized to 14:30:00:00
	 * 
	 * @param date: Date object
	 * @return date with 00 seconds and 00 milliseconds
	 */
	private Date normalizeDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Returns time zone of the operating system
	 * 
	 * @return Time zone ID of the server hosting machine.
	 */
	private String getTimeZoneIDOfHost() {
		return TimeZone.getDefault().getID();
	}

	/**
	 * Inner class that defines the table contents
	 */
	public static class RemindEntry {
		/**
		 * Table name
		 */
		public static final String TABLE_NAME = "RemindTable";

		/**
		 * Unique user ID
		 */
		public static final String COLUMN_USERNAME = "user_id";

		/**
		 * Type of remind service
		 */
		public static final String COLUMN_INPUTTYPE = "input_type";

		/**
		 * Moment of reminding, can be a date or time or both Be care of. Beware of that
		 * Sqlite has no support for date, it is saved as a string
		 */
		public static final String COLUMN_REMINDMOMENT = "moment";

		/**
		 * Remind user yes or no
		 */
		public static final String COLUMN_REMIND = "remind";

		/**
		 * SQL Statement to create a Reminder Database
		 */
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_USERNAME
				+ " INTEGER NOT NULL, " + COLUMN_INPUTTYPE + " TEXT NOT NULL, " + COLUMN_REMINDMOMENT
				+ " TEXT NOT NULL, " + COLUMN_REMIND + " INTEGER NOT NULL, PRIMARY KEY(" + COLUMN_USERNAME + ", "
				+ COLUMN_INPUTTYPE + ", " + COLUMN_REMINDMOMENT + ") )";
	}
}
