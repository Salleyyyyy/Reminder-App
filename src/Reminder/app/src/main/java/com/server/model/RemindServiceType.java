package com.server.model;

import java.io.Serializable;

/**
 * For Doc of all model classes in this package please refer to the server doc.
 */
public class RemindServiceType implements Serializable {

	private static final long serialVersionUID = 1L;
	/** Type of remind service **/
	protected final RemindType remindType;

	public RemindServiceType(RemindType remindType) {
		this.remindType = remindType;
	}

	public RemindType getRemindType() {
		return remindType;
	}

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
