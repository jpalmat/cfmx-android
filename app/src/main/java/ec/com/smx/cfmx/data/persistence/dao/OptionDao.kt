package ec.com.smx.cfmx.data.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ec.com.smx.cfmx.data.persistence.entity.Option
import java.util.ArrayList

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Dao
interface OptionDao : BaseDao<Option> {

    @Query("DELETE FROM options WHERE user_id = :userId")
    fun deleteByUserId(userId: String)

    @Query("SELECT * FROM options WHERE user_id = :userId AND module_id = :moduleId")
    fun findAllByModuleId(moduleId: Int, userId: String): List<Option>

    @Query("UPDATE options SET is_favorite = :isFavorite WHERE user_id = :userId AND" +
            " module_id = :moduleId AND option_id = :optionId")
    fun setFavoriteInOption(isFavorite: Boolean, optionId: Long, moduleId: Int, userId: String)

    @Query("UPDATE options SET is_favorite = :isFavorite WHERE option_id IN (:optionIdList)")
    fun setFavoriteInOptionList(isFavorite: Boolean, optionIdList: String)
}
