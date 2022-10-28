package test.com.server.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.server.model.BloodPressure;
import com.server.model.DocAppointment;
import com.server.model.Medicine;
import com.server.model.RemindService;
import com.server.model.Water;

public class Test_RemindService {

	@Test
	public void two_identical_bloodpressure_objects() {
		BloodPressure bloodPressure1 = new BloodPressure();
		BloodPressure bloodPressure2 = new BloodPressure();
		assertTrue(bloodPressure1.equals(bloodPressure2));
		assertTrue(bloodPressure2.equals(bloodPressure1));
	}

	@Test
	public void two_identical_water_objects() {
		Water water1 = new Water();
		Water water2 = new Water();
		assertTrue(water1.equals(water2));
		assertTrue(water2.equals(water1));
	}

	@Test
	public void two_nonidentical_remindService_objects() {
		Water water = new Water();
		BloodPressure bloodPressure = new BloodPressure();
		assertFalse(water.equals(bloodPressure));
		assertFalse(bloodPressure.equals(water));
	}

	@Test
	public void two_identical_docAppointment() {
		DocAppointment docAppointment1 = new DocAppointment((new Date()).getTime(), 10, 10);
		DocAppointment docAppointment2 = new DocAppointment((new Date()).getTime(), 10, 10);
		assertTrue(docAppointment1.equals(docAppointment2));
		assertTrue(docAppointment2.equals(docAppointment1));
	}

	@Test
	public void two_nonidentical_docAppointment() {
		DocAppointment docAppointment1 = new DocAppointment((new Date()).getTime(), 10, 11);
		DocAppointment docAppointment2 = new DocAppointment((new Date()).getTime(), 10, 10);
		assertFalse(docAppointment1.equals(docAppointment2));
		assertFalse(docAppointment2.equals(docAppointment1));
	}

	@Test
	public void two_identical_medicine() {
		Medicine medicine1 = new Medicine(10, 10);
		Medicine medicine2 = new Medicine(10, 10);
		assertTrue(medicine1.equals(medicine2));
		assertTrue(medicine2.equals(medicine1));
	}

	@Test
	public void two_nonidentical_medicine() {
		Medicine medicine1 = new Medicine(10, 10);
		Medicine medicine2 = new Medicine(11, 10);
		assertFalse(medicine1.equals(medicine2));
		assertFalse(medicine2.equals(medicine1));
	}

	@Test
	public void format_and_parse_date() {
		Water water = new Water();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		Date tomorrowOneYearLater = cal.getTime();
		String formatedDate = RemindService.formatDateTime(tomorrowOneYearLater, water.getDateFormat());
		Date parsedDate = RemindService.parseDateTextToDate(formatedDate, water.getDateFormat());

		// Use a calendar instances for comparing
		Calendar expectedDate = Calendar.getInstance();
		expectedDate.setTime(tomorrowOneYearLater);

		Calendar actualDate = Calendar.getInstance();
		actualDate.setTime(parsedDate);

		assertTrue(expectedDate.get(Calendar.YEAR) == actualDate.get(Calendar.YEAR));
		assertTrue(expectedDate.get(Calendar.MONTH) == actualDate.get(Calendar.MONTH));
		assertTrue(expectedDate.get(Calendar.DAY_OF_MONTH) == actualDate.get(Calendar.DAY_OF_MONTH));
		assertTrue(expectedDate.get(Calendar.HOUR_OF_DAY) == actualDate.get(Calendar.HOUR_OF_DAY));
		assertTrue(expectedDate.get(Calendar.MINUTE) == actualDate.get(Calendar.MINUTE));
	}

	@Test
	public void format_date() {
		Water water = new Water();
		Date today = new Date();
		String formatedDate = RemindService.formatDateTime(today, water.getDateFormat());
		System.out.println(formatedDate);
		System.out.println(water.getDateFormat());
		// For assertion a regex has to be written for dateFormat
	}
	
	
}
