package test.com.server.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

public class TimerAPITest {

	@Test
	public void givenUsingTimer_whenSchedulingDailyTask_thenCorrectWhilePCIsInDozeMode() throws InterruptedException {
		TimerTask repeatedTask = new TimerTask() {
			public void run() {
				System.out.println("Task performed on " + new Date());
			}
		};
		
		TimerTask repeatedTask2 = new TimerTask() {
			public void run() {
				System.out.println("Task2 performed on " + new Date());
			}
		};
		
		TimerTask repeatedTask3 = new TimerTask() {
			public void run() {
				System.out.println("Task3 performed on " + new Date());
			}
		};
		
		Timer timer = new Timer("Timer");

		long noDelay = 0L;
		// Every ten minutes
		long period = 1000L * 60L * 10L;
		timer.scheduleAtFixedRate(repeatedTask, noDelay, period);
		
		Timer timer2 = new Timer("Timer2");
		// Every 30 minutes
		long period2 = 1000L * 60L * 30L;
		timer2.scheduleAtFixedRate(repeatedTask2, noDelay, period2);
		
		Timer timer3 = new Timer("Timer3");
		// Every hour
		long period3 = 1000L * 60L * 60L * 1L;
		timer3.scheduleAtFixedRate(repeatedTask3, noDelay, period3);

		// Test duration: 2h
		long testDuration = 1000L * 60L * 60L * 2L;
		Thread.sleep(testDuration);
	}
}
