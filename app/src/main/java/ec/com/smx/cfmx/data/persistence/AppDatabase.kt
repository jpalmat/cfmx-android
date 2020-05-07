package ec.com.smx.cfmx.data.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import ec.com.smx.cfmx.data.persistence.dao.ModuleDao
import ec.com.smx.cfmx.data.persistence.dao.NotificationDao
import ec.com.smx.cfmx.data.persistence.dao.OptionDao
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.entity.Notification
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.kcommons.util.UAppExecutors

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 *
 * You must specify all entities at begin of this class
 */
@Database(entities = [Notification::class, Module::class, Option::class], exportSchema = false, version = 2)
abstract class AppDatabase : RoomDatabase() {

    // Put your DAO instances here and use it on repositories instances

    abstract fun notificationDao(): NotificationDao
    abstract fun moduleDao(): ModuleDao
    abstract fun optionDao(): OptionDao

    companion object {

        private var mInstance: AppDatabase? = null

        private const val DATABASE_NAME = "nexo-db-v1.0"

        fun getInstance(context: Context, executors: UAppExecutors): AppDatabase {
            if (mInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (mInstance == null) {
                        mInstance = buildDatabase(context.applicationContext, executors)
                    }
                }
            }
            return mInstance!!
        }

        /**
         * Build the database. Only sets up the database configuration and
         * creates a new instance of the database.
         * The SQLite database is only created when it's accessed for the first time.
         */
        private fun buildDatabase(appContext: Context, executors: UAppExecutors): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executors.diskIO().execute {
                                // Generate the data for pre-population
                                val database = getInstance(appContext, executors)
                                insertData(database)
                            }
                        }
                    }).build()
        }

        private fun insertData(database: AppDatabase) {
            database.runInTransaction {
                // Insert some data here...
            }
        }
    }
}
