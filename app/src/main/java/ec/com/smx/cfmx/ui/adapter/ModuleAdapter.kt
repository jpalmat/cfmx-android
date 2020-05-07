package ec.com.smx.cfmx.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions
import ec.com.smx.cfmx.ui.listener.OptionItemListener
import ec.com.smx.cfmx.util.ViewUtil
import kotlinx.android.synthetic.main.item_module.view.*
import org.apache.commons.collections4.CollectionUtils

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModuleAdapter(private val context: Context, private val items: List<ModuleWithOptions>,
                    private val listener: OptionItemListener)
    : RecyclerView.Adapter<ModuleAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // Bind a view
        holder.bind()
        // Get item
        val item = this.items[position]
        // set values
        holder.starImageView.visibility = View.GONE
        holder.optionsContainer.removeAllViews()
        holder.optionsContainer.contentDescription = null
        holder.titleModuleTextView.text = item.module!!.name
        if (CollectionUtils.isNotEmpty(item.options)) {
            // Sort list by order field
            val sortedListOptions = item.options!!.sortedBy { it.order }
            // Create view for each option
            sortedListOptions
                    .map { ViewUtil.createOptionView(context, it, listener) }
                    .forEach { holder.optionsContainer.addView(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_module,
                parent, false)
        return Holder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var optionRecyclerView: RecyclerView = itemView.optionRecyclerView
        var optionsContainer: LinearLayout = itemView.optionsContainer
        var titleModuleTextView: TextView = itemView.titleModuleTextView
        var starImageView: ImageView = itemView.starImageView

        fun bind() = with(itemView) {

        }
    }
}
