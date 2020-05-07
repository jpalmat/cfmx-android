package ec.com.smx.cfmx.data.persistence.dao

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 *
 * Use DAO’s inheritance capability. Do not annotate this interface, it is only for inheritance.
 */
interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(obj: List<T>)

    @Insert
    fun insert(vararg obj: T)

    @Delete
    fun delete(vararg obj: T)

    // TODO Put other commons func here...
}
