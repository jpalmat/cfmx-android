package ec.com.smx.cfmx.data.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ec.com.smx.cfmx.data.persistence.entity.Notification

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Dao
interface NotificationDao : BaseDao<Notification> {

    @Query("SELECT * FROM notifications WHERE user_id = :userId")
    fun list(userId: String): MutableList<Notification>
}
