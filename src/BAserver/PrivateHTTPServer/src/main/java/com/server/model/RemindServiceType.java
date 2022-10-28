package com.server.model;

import java.io.Serializable;

/**
 * Super class for all remind services to determine the type of remind service.
 */
public class RemindServiceType implements Serializable {

	private static final long serialVersionUID = 1L;

	protected final RemindType remindType;

	/**
	 * Create a remind Service of a specific remind type.
	 * 
	 * @param remindType: Remind type of the remind service
	 */
	public RemindServiceType(RemindType remindType) {
		this.remindType = remindType;
	}

	public RemindType getRemindType() {
		return remindType;
	}

	/**
	 * Returns the class depending on the remind type.
	 * 
	 * @return Class of remind service by his remind type
	 */
	public Class<? extends RemindService> getClassByEnum() {
		switch (remindType) {
		case DOCAPPOINTMENT:
			return DocAppointment.class;
		case MEDICINE:
			return Medicine.class;
		case BLOODPRESSURE:
			return BloodPressure.class;
		case WATER:
			return Water.class;
		}
		return null;
	}
}
