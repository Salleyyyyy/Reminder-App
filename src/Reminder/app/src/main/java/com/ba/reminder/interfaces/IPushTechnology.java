package com.ba.reminder.interfaces;

import com.server.model.RemindService;

import java.util.List;

/**
 * Interface for all implemented Push Technologies.
 */
public interface IPushTechnology {

    /**
     * Requests for a remind service.
     *
     * @param remindObject: Remind service
     */
    void requestForRemindService(RemindService remindObject);

    /**
     * Cancels an already existing remind service.
     *
     * @param remindObject: Remind service
     */
    void cancelRemindService(RemindService remindObject);

    /**
     * Returns a list of all registered remind services of the user.
     *
     * @return: List of all registered remind services
     */
    List<RemindService> getRegisteredRemindServices();

    /**
     * Closes the push technology.
     */
    void close();
}
