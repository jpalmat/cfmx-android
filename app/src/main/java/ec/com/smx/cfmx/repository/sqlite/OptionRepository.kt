package ec.com.smx.cfmx.repository.sqlite

import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Option
import java.util.ArrayList

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class OptionRepository private constructor(private val mDatabase: AppDatabase) {

    companion object {

        private var mInstance: OptionRepository? = null

        fun getInstance(database: AppDatabase): OptionRepository {
            if (mInstance == null) {
                synchronized(OptionRepository::class.java) {
                    if (mInstance == null) {
                        mInstance = OptionRepository(database)
                    }
                }
            }
            return mInstance!!
        }
    }

    /**
     * Get a option list from db by user and module id
     */
    fun findAllByModuleId(moduleId: Int, userId: String): List<Option> {
        return mDatabase.optionDao().findAllByModuleId(moduleId, userId)
    }

    /**
     * update option in db by user and module id
     */
    fun setFavoriteInOption(option: Option, userId: String) {
        return mDatabase.optionDao().setFavoriteInOption(option.isFavorite,
                option.optionId!!, option.moduleId, userId)
    }

    /**
     * update option fav status in DB
     */
    fun setFavoriteInOptionList(isFavorite: Boolean, optionIdList: String) {
        return mDatabase.optionDao().setFavoriteInOptionList(isFavorite, optionIdList)
    }
}
