package ec.com.smx.cfmx.data.persistence.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Jimmy Palma.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Entity(tableName = "notifications")
class Notification {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "description")
    var description: String = ""
    @ColumnInfo(name = "user_id")
    var userId: String = ""
}
