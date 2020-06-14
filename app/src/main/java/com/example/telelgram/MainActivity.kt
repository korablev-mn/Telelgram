package com.example.telelgram

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.telelgram.activities.RegisterActivity
import com.example.telelgram.databinding.ActivityMainBinding
import com.example.telelgram.models.User
import com.example.telelgram.ui.fragments.ChatsFragment
import com.example.telelgram.ui.objects.AppDrawer
import com.example.telelgram.utilits.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {   //  сначала выполняется инициализация юзера, затем запустится все приложение/ т.к обращение к базе идет в одтдельном потоке
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(mToolbar)
            mAppDrawer.create()
//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.dataContainer,
//                    ChatsFragment()
//                ).commit()
            //заменили на
            replaceFragment(ChatsFragment(), false)
        } else {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
            replaceActivity(RegisterActivity())
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }
}