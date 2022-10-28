package test.com.server.model.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.server.model.BloodPressure;
import com.server.model.DocAppointment;
import com.server.model.Medicine;
import com.server.model.Water;
import com.server.model.parse.RemindServiceJsonParser;

public class Test_RemindServiceJsonParser {

	@Test
	public void serialize_and_deserialize_remindServices() {
		BloodPressure expectedBloodPressure = new BloodPressure();
		Water expectedWater = new Water();
		DocAppointment expectedDocAppointment = new DocAppointment((new Date()).getTime(), 10, 10);
		Medicine expectedMedicine = new Medicine(10, 10);

		RemindServiceJsonParser remindServiceJsonParser = new RemindServiceJsonParser();

		String bloodPressure_json = remindServiceJsonParser.parseToJson(expectedBloodPressure);
		BloodPressure actualBloodPressure = (BloodPressure) remindServiceJsonParser
				.readRemindServiceFromJson(bloodPressure_json);
		assertEquals(expectedBloodPressure, actualBloodPressure);

		String water_json = remindServiceJsonParser.parseToJson(expectedWater);
		Water actualWater = (Water) remindServiceJsonParser.readRemindServiceFromJson(water_json);
		assertEquals(expectedWater, actualWater);

		String docAppointment_json = remindServiceJsonParser.parseToJson(expectedDocAppointment);
		DocAppointment actualDocAppointment = (DocAppointment) remindServiceJsonParser
				.readRemindServiceFromJson(docAppointment_json);
		assertEquals(expectedDocAppointment, actualDocAppointment);
		assertTrue(expectedDocAppointment.getHourOfDay() == actualDocAppointment.getHourOfDay());
		assertTrue(expectedDocAppointment.getMinute() == actualDocAppointment.getMinute());

		String medicine_json = remindServiceJsonParser.parseToJson(expectedMedicine);
		Medicine actualMedicine = (Medicine) remindServiceJsonParser.readRemindServiceFromJson(medicine_json);
		assertEquals(expectedMedicine, actualMedicine);
		assertTrue(expectedMedicine.getHourOfDay() == actualMedicine.getHourOfDay());
		assertTrue(expectedMedicine.getMinute() == actualMedicine.getMinute());
	}
}
