package com.example.telelgram.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.R
import com.example.telelgram.database.*
import com.example.telelgram.models.CommonModel
import com.example.telelgram.utilits.*
import kotlinx.android.synthetic.main.fragment_main_list.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чат Телеграм"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycle_view
        mAdapter = MainListAdapter()

        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { data ->
            mListItems = data.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
                when(model.type){
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
             }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun showGroup(model: CommonModel) {
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            val newModel = dataSnapshot.getCommonModel()

            // 3 запррс
                REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                .addListenerForSingleValueEvent(AppValueEventListener {data2->
                    val tempList = data2.children.map { it.getCommonModel() }
                    if(tempList.isEmpty()) {
                        newModel.lastMessage = "Чат очищен"
                    } else newModel.lastMessage = tempList[0].text
                    mAdapter.updateListItems(newModel)
                })
        })
    }

    private fun showChat(model: CommonModel) {
        // 2 запрос
        mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {dataSnapshot ->
            val newModel = dataSnapshot.getCommonModel()

            // 3 запррс
            mRefMessages.child(model.id).limitToLast(1)
                .addListenerForSingleValueEvent(AppValueEventListener {data2->
                    val tempList = data2.children.map { it.getCommonModel() }
                    if(tempList.isEmpty()) {
                        newModel.lastMessage = "Чат очищен"
                    } else newModel.lastMessage = tempList[0].text

                    if(newModel.fullname.isEmpty()){
                        newModel.fullname = newModel.phone
                    }
                    mAdapter.updateListItems(newModel)
                })
        })
    }
}