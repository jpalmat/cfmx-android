package ec.com.smx.cfmx.util

import com.google.gson.Gson
import ec.com.smx.cfmx.data.api.vo.response.ModuleResponse
import ec.com.smx.cfmx.data.api.vo.response.OptionResponse
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions
import ec.com.smx.cfmx.vo.OptionDataVO
import org.apache.commons.collections4.CollectionUtils
import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject



/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
object MapperUtil {

    fun mapToModuleWithOptions(moduleList: List<Module>): List<ModuleWithOptions> {
        val result = ArrayList<ModuleWithOptions>()
        CollectionUtils.collect<Module, ModuleWithOptions, ArrayList<ModuleWithOptions>>(moduleList,
                { input ->
                    // Fill values from pojo
                    val moduleWithOptions = ModuleWithOptions()
                    moduleWithOptions.module = input
                    moduleWithOptions.options = input.options
                    moduleWithOptions
                }, result)
        return result
    }

    fun mapToModule(moduleResponseList: List<ModuleResponse>, userId: String): List<Module> {
        val result = ArrayList<Module>()
        CollectionUtils.collect<ModuleResponse, Module, ArrayList<Module>>(moduleResponseList,
                { input ->
                    // Fill values from pojo
                    val module = Module()
                    module.moduleId = input.id
                    module.name = input.title
                    module.description = input.descripcion
                    module.userId = userId
                    val options = mapToOption(input.menuItems, module.moduleId, userId)
                    module.options = options
                    module
                }, result)
        return result
    }

    private fun mapToOption(menuItems: List<OptionResponse>, moduleId: Int, userId: String): List<Option> {
        val result = ArrayList<Option>()
        CollectionUtils.collect<OptionResponse, Option, ArrayList<Option>>(menuItems, { input ->
            // Fill values from pojo
            val option = Option()
            option.optionId = input.id
            option.name = input.title
            if (input.descripcion != null) {
                option.description = input.descripcion!!
            } else {
                option.description = ""
            }
            option.order = input.ordenMenu
            if (input.estiloPanel != null) {
                option.image = input.estiloPanel!!
            } else {
                option.image = ""
            }

            val data = input.href
            if(isJSONValid(data)) {
                val optionData = Gson().fromJson(data, OptionDataVO::class.java)
                option.color = optionData.color
                option.androidPackage = optionData.android
                option.iosPackage = optionData.iOS
            }
            option.moduleId = moduleId
            option.userId = userId
            option.isFavorite = input.favorito
            option.codigoVentana = input.codigoVentana
            option
        }, result)
        return result
    }

    private fun isJSONValid(test: String): Boolean {
        // check json well structured
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }
}
