package ec.com.smx.cfmx.repository.sqlite

import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Notification

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 *
 * Repository handling the work with notifications.
 */
class NotificationRepository private constructor(private val mDatabase: AppDatabase) {

    companion object {

        private var mInstance: NotificationRepository? = null

        fun getInstance(database: AppDatabase): NotificationRepository {
            if (mInstance == null) {
                synchronized(NotificationRepository::class.java) {
                    if (mInstance == null) {
                        mInstance = NotificationRepository(database)
                    }
                }
            }
            return mInstance!!
        }
    }

    /**
     * Get the list of notifications from the database
     */
    fun list(userId: String): MutableList<Notification> {
        return mDatabase.notificationDao().list(userId)
    }

    /**
     * Delete a notification from the database by ID
     */
    fun delete(notification: Notification) {
        mDatabase.notificationDao().delete(notification)
    }
}
