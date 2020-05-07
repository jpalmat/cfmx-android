package ec.com.smx.cfmx.data.persistence.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Entity(tableName = "modules")
class Module {
    @PrimaryKey
    @ColumnInfo(name = "module_id")
    var moduleId: Int = 0
    @ColumnInfo(name = "name")
    var name: String = ""
    @ColumnInfo(name = "description")
    var description: String = ""
    @ColumnInfo(name = "user_id")
    var userId: String = ""
    @Ignore // Fill values on query, by default it is null
    var options: List<Option>? = null
}
