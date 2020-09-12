package com.example.telelgram.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.ui.message_recycler_view.views.MessageView
import com.example.telelgram.ui.message_recycler_view.view_holders.AppHolderFactory
import com.example.telelgram.ui.message_recycler_view.view_holders.MessageHolder

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mlistMessagesCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolder>()
//    private lateinit var mDiffResult: DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mlistMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = mlistMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { // отображение холдера
//        when(holder){
//            is HolderImageMessage -> holder.drawMessageImage(holder, mlistMessagesCache[position])
//            is HolderTextMessage -> holder.drawMessageText(holder, mlistMessagesCache[position])
//            is HolderVoiceMessage -> holder.drawMessageVoice(holder, mlistMessagesCache[position])
//            else -> {}
//        }
        (holder as MessageHolder).drawMessage(mlistMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {  //эта функция отрабатывает тольео при появлении объекта(holder) на экране
        (holder as MessageHolder).onAttach(mlistMessagesCache[holder.adapterPosition])
        mListHolders.add(holder as MessageHolder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) { //отрабатывает как только holder уходит из видимости экрана
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    fun addItemToBottom(item:MessageView,
                        onSuccess:()->Unit){
        if(!mlistMessagesCache.contains(item)){
            mlistMessagesCache.add(item)
            notifyItemInserted(mlistMessagesCache.size)  // метод предлагает обновить элемент из списка(в данном случае последний элемент)
        }
    onSuccess()
    }

    fun addItemToTop(item:MessageView,
                        onSuccess:()->Unit){
        if(!mlistMessagesCache.contains(item)){
            mlistMessagesCache.add(item)
            mlistMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mListHolders.forEach{
            it.onDetach()
        }
    }

//    fun addItem(item:CommonModel,
//                flag: Boolean,
//    onSuccess:()->Unit){  // теперь это коллбак функция
////        val newList = mutableListOf<CommonModel>()
////        newList.addAll(mlistMessagesCache)
////        if(!newList.contains(item))newList.add(item)
////        newList.sortBy { it.timeStamp.toString() }
////        mDiffResult = DiffUtil.calculateDiff(DiffUtilCalback(mlistMessagesCache, newList)) // сравниваем списки сообщений
////        mDiffResult.dispatchUpdatesTo(this)  // в ресайкл отображает только новые добавленные сообщения
////        mlistMessagesCache = newList
//    if(flag) {
//        if(!mlistMessagesCache.contains(item)){
//            mlistMessagesCache.add(item)
//            notifyItemInserted(mlistMessagesCache.size)  // метод предлагает обновить элемент из списка(в данном случае последний элемент)
//            }
//        } else{
//        if(!mlistMessagesCache.contains(item)){
//            mlistMessagesCache.add(item)
//            mlistMessagesCache.sortBy { it.timeStamp.toString() }
//            notifyItemInserted(0)
//        }
//        onSuccess()
//    }
//}
}