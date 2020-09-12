package com.example.telelgram.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.database.UID
import com.example.telelgram.ui.message_recycler_view.views.MessageView
import com.example.telelgram.utilits.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMessage(view: View):RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserMessage: ConstraintLayout = view.block_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_message_time
    private val blockReceivedMessage: ConstraintLayout = view.block_received_message
    private val chatReceivedMessage: TextView = view.chat_received_message
    private val chatReceivedMessageTime: TextView = view.chat_received_message_time

    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
    //    fun setList(list: List<CommonModel>) {
//        mDiffResult = DiffUtil.calculateDiff(DiffUtilCalback(mlistMessagesCache, list)) // сравниваем списки сообщений
//        mDiffResult.dispatchUpdatesTo(this)  // в ресайкл отображает только новые добавленные сообщения
//        mlistMessagesCache = list
//       // notifyDataSetChanged()  // говорит что данные изменены и их надо обработать
//    }
}