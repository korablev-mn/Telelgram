package com.example.telelgram.ui.message_recycler_view.view_holders

import com.example.telelgram.ui.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)  // подключает событие при появлении на экране при скроллинге
    fun onDetach() // отключает события при выходе за экран
}