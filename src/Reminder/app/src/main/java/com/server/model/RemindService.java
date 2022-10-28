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
 * Model class for storing remind info. It also influences the schema of the
 * database for storing these class object.
 * 
 * Can't be abstract due to parsing errors
 */
public abstract class RemindService extends RemindServiceType implements Serializable {

	/** Date object contains date and/or time information */
	protected Date date;

	/** Text representation of date */
	protected String dateText;

	/** Remind user or not, default is true */
	protected boolean remind = true;

	/** RemindObject DateFormat */
	protected String dateFormat = "";

	/** Time Zone of the host */
	protected String timeZoneID = getTimeZoneIDOfHost();

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param remindType Type of RemindObject
	 * @param date       Date that contains all necessary date and time information
	 * @param dateFormat String that determines how to format the date
	 */
	public RemindService(RemindType remindType, Date date, String dateFormat) {
		super(remindType);
		this.date = normalizeDate(date);
		this.dateFormat = dateFormat;
		this.dateText = formatDateTime(date, dateFormat);
	}

	public RemindService(RemindType remindType) {
		super(remindType);
	}

	public static String formatDateTime(Date date, String dateFormat) {
		Format formatter = new SimpleDateFormat(dateFormat, Locale.GERMAN);
		return formatter.format(date);
	}

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

	public void setDate(Date date, String dateFormat) {
		this.date = normalizeDate(date);
		this.dateText = formatDateTime(date, dateFormat);
	}

	public Date getDate() {
		return date;
	}

	/** Regular remind service or not **/
	public abstract boolean isRegular();

	public abstract long getPeriodOfReminding();

	public abstract Date scheduleTimeToTriggerReminding();

	/**
	 * Get notification text of remind service
	 * 
	 * @return notification text
	 */
	public abstract String getNotificationText();

	/**
	 * Create a unique ID for the remind service
	 * 
	 * @return unique remind service id
	 */
	public abstract int createUniqueID();

	public abstract boolean isUnique();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RemindService otherRemindService = (RemindService) o;
		if (createUniqueID() == otherRemindService.createUniqueID())
			return true;
		else
			return false;
	}

	/**
	 * This function normalizes a date object, that means seconds and milliseconds
	 * are null.
	 * 
	 * @param date
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
	 * Get time zone of the operating system
	 * 
	 * @return time zone ID
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
		 * Kind of input
		 */
		public static final String COLUMN_INPUTTYPE = "input_type";

		/**
		 * Moment of reminding, can be a date or time or both Be care of: Sqlite has no
		 * support for date, it is saved as a string
		 */
		public static final String COLUMN_REMINDMOMENT = "moment";

		/**
		 * Remind yes or no
		 */
		public static final String COLUMN_REMIND = "remind";

		/**
		 * SQL Statement to create Reminder Database
		 */
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_USERNAME
				+ " INTEGER NOT NULL, " + COLUMN_INPUTTYPE + " TEXT NOT NULL, " + COLUMN_REMINDMOMENT
				+ " TEXT NOT NULL, " + COLUMN_REMIND + " INTEGER NOT NULL, PRIMARY KEY(" + COLUMN_USERNAME + ", "
				+ COLUMN_INPUTTYPE + ", " + COLUMN_REMINDMOMENT + ") )";
	}
}
