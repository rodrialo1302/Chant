package com.chant.android.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.model.entities.Notification

class NotificationAdapter(private var notiList:ArrayList<Notification>, private val onClickListener:(Notification) -> Unit ) : RecyclerView.Adapter<NotificationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NotificationsViewHolder(layoutInflater.inflate(R.layout.item_notifications, parent, false))
    }
    override fun getItemCount(): Int = notiList.size


    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val item = notiList[position]
        holder.renderNotis(item, onClickListener)

    }
}