package com.example.telelgram.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.telelgram.R
import com.example.telelgram.database.*
import com.example.telelgram.models.CommonModel
import com.example.telelgram.ui.screens.base.BaseFragment
import com.example.telelgram.utilits.*
import kotlinx.android.synthetic.main.fragment_add_contacts.*

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        listContacts.clear() // очищаем каждый раз при входе вво вью
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"

        hideKeyboard()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if(listContacts.isEmpty()){
                showToast("Добавьте участников !")
            } else{
                replaceFragment(CreateGroupFragment(listContacts))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contacts_recycle_view
        mAdapter = AddContactsAdapter()

        // 1 запрос
        mRefContactsList.addListenerForSingleValueEvent(AppValueEventListener { data ->
            mListItems = data.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
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
        })
        mRecyclerView.adapter = mAdapter
    }

    companion object{
        val listContacts = mutableListOf<CommonModel>()
    }
}