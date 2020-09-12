package com.example.telelgram.ui.screens.single_chat

import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telelgram.R
import com.example.telelgram.database.*
import com.example.telelgram.models.CommonModel
import com.example.telelgram.models.UserModel
import com.example.telelgram.ui.screens.base.BaseFragment
import com.example.telelgram.ui.message_recycler_view.views.AppViewFactory
import com.example.telelgram.ui.screens.main_list.MainListFragment
import com.example.telelgram.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener  //слушатель
    private lateinit var mReceivingUser: UserModel //пользователь
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
  //  private lateinit var mMessagesListener: AppValueEventListener  // слушатель изменений
    private lateinit var mMessagesListener: AppChildEventListener  // слушатель добавлений
    private var mCountMessages = 3
    private var mIsScrolling = false
    private var mSmoothScrollToPozition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    private fun initFields() {
        setHasOptionsMenu(true) // разрешение выпадающего меню
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_input_message.addTextChangedListener(AppTextWatcher{
            val string = chat_input_message.text.toString()
            if(string.isEmpty()||string=="Запись"){
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })
        chat_btn_attach.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {// корутина
            chat_btn_voice.setOnTouchListener { v, event ->
                if(checkPermissions(RECORD_AUDIO)){
                    if(event.action == MotionEvent.ACTION_DOWN){
                        // начать запись аудио
                        chat_input_message.setText("Запись")
                        chat_btn_voice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, R.color.colorRed))
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if(event.action == MotionEvent.ACTION_UP){
                        // закончить запись аудио
                        chat_input_message.setText("")
                        chat_btn_voice.colorFilter = null  // цветовой фильтр
                        mAppVoiceRecorder.stopRecord() {file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey, contact.id, TYPE_MESSAGE_VOICE)
                            mSmoothScrollToPozition = true // оускает сообщения вниз
                        }
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // выводим меню
        btn_attach_file.setOnClickListener { attachFile() }
        btn_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycler_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)  // нода в базе
            .child(UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true) // все вьюхолдеры одного размера, для уменьшения нагрузки за счет уменьшения вычислений
        mRecyclerView.isNestedScrollingEnabled = false  // отключаем, его нет (все это ускоритель работы)
        mRecyclerView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener{
            val message = it.getCommonModel()

            if(mSmoothScrollToPozition){
                mAdapter.addItemToBottom(AppViewFactory.getView(message)){
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)){
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
//                mAdapter.addItem(it.getCommonModel(), mSmoothScrollToPozition){
//                    if(mSmoothScrollToPozition){  // условие чтобы при скроллинге вверх, затем подгружает +10 сообщений скролл не опускался в самый низ
//                        mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
//                    }
//                    mSwipeRefreshLayout.isRefreshing = false // отключаем swipe(иконка загрузки)
//                }
        }
//        mMessagesListener = AppValueEventListener { data ->  // отрабатывает если в базе есть изменение + новое сообщение
//            mListMessages = data.children.map { it.getCommonModel() } // сохраняет все ноды в лист
//            mAdapter.setList(mListMessages)
//            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)  // скроллит view вниз при написании сообщения
//        }
      //  mRefMessages.addValueEventListener(mMessagesListener)  // слушатель к ноде сообщений
          mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)  // слушатель только добавлений в чайлд сообщений limitToLast(mCountMessages) - ограничивает загрузку 10 последними

        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                println(mRecyclerView.recycledViewPool.getRecycledView(0))
                if(mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition()<=3){
                    println("updateData") // последнее условие для уменьшения частоты подгрузки при скроллинге вверх
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {// отслеживает движение скроллинга
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){  // скроллится ли сейчас вью
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPozition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(
            NODE_USERS
        ).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {// срабатывает после нажатия кнопки отправить сообщение
            mSmoothScrollToPozition = true // чтобы при новом сообщении скролл опускался в самый низ
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Ведите сообщение")
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
                saveToMainList(contact.id, TYPE_CHAT)
                chat_input_message.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_chat_fullname.text = contact.fullname
        } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname
        mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri  //получаем uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPozition = true // оускает сообщения вниз
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFilenameFromUri(uri!!) // точно не нулл uri
                    uri?.let { uploadFileToStorage(it, messageKey, contact.id, TYPE_MESSAGE_FILE, filename) }
                    mSmoothScrollToPozition = true // оускает сообщения вниз
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.mToolbar.toolbar_info.visibility = View.GONE  // делаем видимым
        mRefUser.removeEventListener(mListenerInfoToolbar)
            mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        Создание выпадающего меню
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Слушатель выбора выпадающего меню
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact.id){
                showToast("Чат очищен")
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(contact.id){
                showToast("Чат удален")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }
}