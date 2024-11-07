package com.chant.android.ui.notifications

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.databinding.ItemNotificationsBinding
import com.chant.android.model.entities.Notification
import java.time.format.DateTimeFormatter

class NotificationsViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    private val bindingNotification = ItemNotificationsBinding.bind(view)



    fun renderNotis(notification: Notification, onClickListener:(Notification) -> Unit){
        if (notification.type == "post"){
            bindingNotification.notiAction.text = notification.author + " ha respondido a tu Post:"
            bindingNotification.notiText.text = notification.text
            bindingNotification.notiDate.text = notification.date.format(
                DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
        }
        else {
            bindingNotification.notiAction.text = "A " + notification.author + " le ha gustado tu Post:"
            bindingNotification.notiText.text = notification.text
            bindingNotification.notiDate.text = notification.date.format(
                DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
        }


        itemView.setOnClickListener{ onClickListener(notification)}
    }


}

