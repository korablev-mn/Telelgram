package com.example.telelgram.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.database.UID
import com.example.telelgram.database.getFileFromStorage
import com.example.telelgram.ui.message_recycler_view.views.MessageView
import com.example.telelgram.utilits.WRITE_FILES
import com.example.telelgram.utilits.asTime
import com.example.telelgram.utilits.checkPermissions
import com.example.telelgram.utilits.showToast
import kotlinx.android.synthetic.main.message_item_file.view.*
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val blockReceivingfileMessage: ConstraintLayout = view.block_received_file_message
    private val blockUserfileMessage: ConstraintLayout = view.block_user_file_message
    private val chatUserfileMessageTime: TextView = view.chat_user_file_message_time
    private val chatReceivedfileMessageTime: TextView = view.chat_received_file_message_time

    private val chatUserFileName:TextView = view.chat_user_filename
    private val chatUserBtnDownload:ImageView = view.chat_user_btn_download
    private val chatUserProgressBar:ProgressBar = view.chat_user_progress_bar

    private val chatReceivedFileName:TextView = view.chat_received_filename
    private val chatReceivedBtnDownload:ImageView = view.chat_received_btn_download
    private val chatReceivedProgressBar:ProgressBar = view.chat_received_progress_bar

    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockReceivingfileMessage.visibility = View.GONE
            blockUserfileMessage.visibility = View.VISIBLE
            chatUserfileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blockReceivingfileMessage.visibility = View.VISIBLE
            blockUserfileMessage.visibility = View.GONE
            chatReceivedfileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFileName.text = view.text
        }
    }

    //подключаем события
    override fun onAttach(view: MessageView) {
        if(view.from == UID) {
            chatUserBtnDownload.setOnClickListener { clickToBtnFile(view) }
        } else chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        if(view.from == UID) {
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else{
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if(checkPermissions(WRITE_FILES)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if(view.from == UID) {
                        chatUserBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else{
                        chatReceivedBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e:Exception){
            showToast(e.message.toString())
        }
    }

    // отключаем события
    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}