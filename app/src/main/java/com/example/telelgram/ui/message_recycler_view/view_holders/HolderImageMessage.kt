package com.example.telelgram.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.database.UID
import com.example.telelgram.ui.message_recycler_view.views.MessageView
import com.example.telelgram.utilits.asTime
import com.example.telelgram.utilits.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item_image.view.*

class HolderImageMessage(view: View):RecyclerView.ViewHolder(view), MessageHolder {
    private val blockReceivingImageMessage: ConstraintLayout = view.block_received_image_message
    private val blockUserImageMessage: ConstraintLayout = view.block_user_image_message
    private val chatUserImage: ImageView = view.chat_user_image
    private val chatReceivedImage: ImageView = view.chat_received_image
    private val chatUserImageMessageTime: TextView = view.chat_user_image_message_time
    private val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockReceivingImageMessage.visibility = View.GONE
            blockUserImageMessage.visibility = View.VISIBLE
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view.timeStamp.asTime()
        } else {
            blockReceivingImageMessage.visibility = View.VISIBLE
            blockUserImageMessage.visibility = View.GONE
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}