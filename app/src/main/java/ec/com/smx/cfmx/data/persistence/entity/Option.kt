package ec.com.smx.cfmx.data.persistence.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Entity(tableName = "options",
        foreignKeys = [
            (ForeignKey(entity = Module::class,
                    parentColumns = arrayOf("module_id"),
                    childColumns = arrayOf("module_id"),
                    onDelete = ForeignKey.CASCADE))
        ],
        indices = [(Index(value = ["module_id", "option_id"]))])
// option dao
class Option {
    @PrimaryKey
    @ColumnInfo(name = "option_id")
    var optionId: Long? = null
    @ColumnInfo(name = "name")
    var name: String = ""
    @ColumnInfo(name = "description")
    var description: String = ""
    @ColumnInfo(name = "package_name")
    var packageName: String = ""
    @ColumnInfo(name = "order")
    var order: Int = 0
    @ColumnInfo(name = "image")
    var image: String = ""
    @ColumnInfo(name = "color")
    var color: String? = null
    @ColumnInfo(name = "android_package")
    var androidPackage: String? = null
    @ColumnInfo(name = "ios_package")
    var iosPackage: String? = null
    @ColumnInfo(name = "module_id")
    var moduleId: Int = 0
    @ColumnInfo(name = "user_id")
    var userId: String = ""
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false
    @ColumnInfo(name = "codigo_ventana")
    var codigoVentana: String? = null
}
