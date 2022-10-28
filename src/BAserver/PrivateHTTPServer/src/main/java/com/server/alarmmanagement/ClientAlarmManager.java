package com.server.alarmmanagement;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.server.forwarding.NotificationInfo;
import com.server.model.RemindService;
import com.server.storage.NotificationStorage;
import com.server.storage.RemindServiceTimerStorage;

/**
 * This class is responsible for scheduling and saving the alarms of one client.
 * Those alarms will then trigger pushing the notification to the client with
 * the client ID.
 */
public class ClientAlarmManager {

	/**
	 * Client AlarmManager for Client with Client ID
	 */
	protected UUID clientId;

	/**
	 * All remind services and there corresponding timer are stored here for
	 * retrieving the timer of a remind service
	 */
	private RemindServiceTimerStorage remindServiceTimerStorage = new RemindServiceTimerStorage();

	/**
	 * All notification that have to be sent to the client is stored here
	 */
	private NotificationStorage notificationStorage = new NotificationStorage();

	/**
	 * Constructor of ClientAlarmManager for one Client
	 * 
	 * @param clientId: Client ID of the client
	 */
	public ClientAlarmManager(UUID clientId) {
		this.clientId = clientId;
	}

	/**
	 * Returns the list of all registered remind services of the client
	 * 
	 * @return List of all registered remind services, empty if there is no one
	 */
	public List<RemindService> getRegisteredRemindServices() {
		return remindServiceTimerStorage.getRegisteredRemindServices();
	}

	/**
	 * Checks if there is a notification available to be sent to the client
	 * 
	 * @return true, if there is a new notification for client, otherwise false
	 */
	public synchronized boolean anyNotificationAvailable() {
		return notificationStorage.anyNotificationAvalaible();
	}

	/**
	 * Registers for a new remind service or cancels an existing one. It depends on
	 * the remind flag in the remind service object.
	 * 
	 * @param specificRegistrationForNotifying: Specific Remind Service which
	 *                                          contains all necessary info (type,
	 *                                          date, time, remind or not for
	 *                                          canceling)
	 */
	public synchronized void registerOrCancelRemindService(RemindService specificRegistrationForNotifying) {
		if (specificRegistrationForNotifying.getRemind()) {
			registerForRemindService(specificRegistrationForNotifying);
		} else {
			cancelRemindService(specificRegistrationForNotifying);
		}
	}

	/**
	 * Returns the notification text if there is a notification available for
	 * sending which can be checked with {@link #anyNotificationAvailable()
	 * anyNotificationAvailable} method.
	 * 
	 * @return Notification text or null if a notification is not available
	 */
	public synchronized NotificationInfo getNotificationInfo() {
		if (notificationStorage.anyNotificationAvalaible()) {
			// Notification is removed because it will be sent
			RemindService remindService = notificationStorage.removeNotificationForSending();
			if (!remindService.isRegular()) {
				// Remove remind service only if it is not regular as it is only notified one
				// time
				remindServiceTimerStorage.removeRemindService(remindService);
			}
			boolean notificationHasHighPriority = false;
			// Determine which priority the notification has according to the type of remind
			// service
			// If priority is considered, depends on the specific push technology
			switch (remindService.getRemindType()) {
			case DOCAPPOINTMENT:
				notificationHasHighPriority = true;
				break;
			case MEDICINE:
				notificationHasHighPriority = true;
				break;
			case BLOODPRESSURE:
				notificationHasHighPriority = false;
				break;
			case WATER:
				notificationHasHighPriority = false;
				break;
			}
			return new NotificationInfo(remindService.getNotificationText(), notificationHasHighPriority);
		}
		return null;
	}

	/**
	 * Registers for a remind service.
	 * 
	 * @param remindService: Registered remind service of the client
	 */
	private synchronized void registerForRemindService(RemindService remindService) {
		// Maybe: Better solution
		// Delete these remindObject before inserting if it is unique
		// Change data model
		// Deletion before insertion
		if (remindService.isUnique() && remindServiceTimerStorage.existsRemindService(remindService)) {
			// Canceling the scheduled timer of the remind service
			Timer scheduledTimer = remindServiceTimerStorage.getTimerForRemindService(remindService);
			scheduledTimer.cancel();
			// Removing from timer storage
			remindServiceTimerStorage.removeRemindService(remindService);
		}

		Timer timer = scheduleTimerTask(remindService);
		remindServiceTimerStorage.registerForRemindService(remindService, timer);
	}

	/**
	 * Schedules the timer with the timer task which will trigger the 'pushing' of
	 * the notification to the client at the proper timer.
	 * 
	 * @param specificRegistrationForNotifying: Remind service with all necessary
	 *                                          info for scheduling a timer
	 * @return Timer with scheduled task
	 */
	private Timer scheduleTimerTask(RemindService specificRegistrationForNotifying) {
		Date timeToSendNotification = specificRegistrationForNotifying.scheduleTimeToTriggerReminding();
		TimerTask timerTask = createTimerTask(specificRegistrationForNotifying);
		Timer timer = new Timer();

		if (specificRegistrationForNotifying.isRegular()) {
			timer.scheduleAtFixedRate(timerTask, timeToSendNotification,
					specificRegistrationForNotifying.getPeriodOfReminding());
		} else {
			timer.schedule(timerTask, timeToSendNotification);
		}
		return timer;
	}

	/**
	 * Creates a timer task for this remind service. This timer task will add the
	 * remind service to the storage of notifications. A thread is observing this
	 * storage and will take the notification to send to the client.
	 * 
	 * @param remindService: Remind service for creating a timer task
	 * @return Timer task for this remind service
	 */
	private TimerTask createTimerTask(RemindService remindService) {
		return new TimerTask() {
			@Override
			public void run() {
				printInfo("Add new remindService of type " + remindService.getRemindType());
				addNewNotificationToStorage(remindService);
				printInfo("RemindService of type " + remindService.getRemindType() + " added");
			}
		};
	}

	/**
	 * Adds a remind service to the notification storage from which the notification
	 * will be taken to push
	 * 
	 * @param remindService: Remind service that is added to storage
	 */
	private synchronized void addNewNotificationToStorage(RemindService remindService) {
		if (!notificationStorage.contains(remindService)) {
			printInfo("RemindService not already stored!");
			notificationStorage.addNewNotificationMessage(remindService);
		} else {
			printInfo("RemindService already stored!");
		}
	}

	/**
	 * Cancels an already registered remind service. The timer for that remind
	 * service will be canceled and deleted from the store.
	 * 
	 * @param canceledRemindService: Remind service that will be canceled
	 */
	private synchronized void cancelRemindService(RemindService canceledRemindService) {
		// Check if this remind service already exists
		if (!remindServiceTimerStorage.existsRemindService(canceledRemindService))
			return;
		cancelTimerForRemindService(canceledRemindService);
		remindServiceTimerStorage.removeRemindService(canceledRemindService);
	}

	/**
	 * Cancels the scheduled timer for this remind service
	 * 
	 * @param canceledRemindService: Remind service that is canceled and whose timer
	 *                               will be calceled too
	 */
	private synchronized void cancelTimerForRemindService(RemindService canceledRemindService) {
		Timer timer = remindServiceTimerStorage.getTimerForRemindService(canceledRemindService);
		timer.cancel();
	}

	/**
	 * Print information text on console.
	 * 
	 * @param info: Info message
	 */
	private void printInfo(String info) {
		System.out.println("INFO" + info);
	}
}
