package ec.com.smx.cfmx.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.ui.listener.OptionItemListener
import kotlinx.android.synthetic.main.item_option.view.*
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@SuppressLint("InflateParams")
object ViewUtil {

    private const val DEFAULT_VALUE = "DEFAULT"
    private const val PREFIX_MATERIAL = "material/"

    fun createOptionView(context: Context, option: Option, listener: OptionItemListener): View {
        // Get inflater
        val inflater = LayoutInflater.from(context)
        val convertView = inflater.inflate(R.layout.item_option, null, false)
        // Get view
        val logoImageView = convertView.logoImageView
        val nameTextView = convertView.nameTextView
        val optionContainer = convertView.optionContainer
        // Set values
        nameTextView.text = option.name
        optionContainer.tag = option
        // Set listeners
        optionContainer.setOnClickListener { view ->
            listener.onOptionItemClicked(view)
        }

        optionContainer.setOnLongClickListener { view ->
            listener.onOptionItemLongClicked(view)
            true
        }
        // Validate logo image
        val logo = option.image
        val logoData = logo.split(PREFIX_MATERIAL)
        var icon = context.getString(R.string.default_app_icon).toUpperCase()
        if (logoData.size > 1) {
            val data = logoData[1].replace('-', '_').toUpperCase()
            if (data != DEFAULT_VALUE) {
                icon = data
            }
        }
        // Icon does not exist, must appear default one
        val exist = MaterialDrawableBuilder.IconValue.values().any { it.name == icon }
        if (!exist) {
            icon = context.getString(R.string.default_app_icon).toUpperCase()
        }
        // The method returns a MaterialDrawable,
        // but as it is private to the builder you'll have to store it as a regular Drawable ;)
        val yourDrawable = MaterialDrawableBuilder.with(context) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.valueOf(icon)) // provide an icon
                .setColor(Color.RED) // set the icon color
                .build() // Finally call build
        // Set logo on imageView
        logoImageView.setImageDrawable(yourDrawable)
        // Return view
        return convertView
    }
}
