package com.ba.reminder;


import com.flavor.reminder.PushTechnology;
import com.server.model.BloodPressure;
import com.server.model.DocAppointment;
import com.server.model.Medicine;
import com.server.model.RemindService;
import com.server.model.Water;

import org.junit.Test;
import java.util.Date;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void sendHTTPPost() throws InterruptedException {
        PushTechnology technology = new PushTechnology(null);
        technology.requestForRemindService(new Water());
        technology.requestForRemindService(new DocAppointment((new Date()).getTime(), 12, 12));
        technology.requestForRemindService(new Water());
        technology.requestForRemindService(new Medicine(12, 12));
        technology.requestForRemindService(new BloodPressure());
        List<RemindService> remindObjectList = technology.getRegisteredRemindServices();
        // Thread.sleep(1000);
        for(RemindService remindObject: remindObjectList){
            System.out.println(remindObject.toString());
        }
    }
}