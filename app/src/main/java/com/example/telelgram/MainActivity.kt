package com.example.telelgram

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.telelgram.database.AUTH
import com.example.telelgram.database.initFirebase
import com.example.telelgram.database.initUser
import com.example.telelgram.databinding.ActivityMainBinding
import com.example.telelgram.ui.screens.main_list.MainListFragment
import com.example.telelgram.ui.screens.register.EnterPhoneNumberFragment
import com.example.telelgram.ui.objects.AppDrawer
import com.example.telelgram.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        /* Функция запускается один раз, при создании активити */
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {   //  сначала выполняется инициализация юзера, затем запустится все приложение/ т.к обращение к базе идет в одтдельном потоке
//            GlobalScope.launch {// самый простой вариант запуска карутины в котлине
//                initContacts()  // не рекомендовано. надо следить за жизненнным циклом корутины
//            }
            // main  в главном потоке IO инпут отпут
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        /* Функция инициализирует функциональность приложения */
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {

            mAppDrawer.create()
//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.dataContainer,
//                    ChatsFragment()
//                ).commit()
            //заменили на
            replaceFragment(MainListFragment(), false)
        } else {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
            replaceFragment(EnterPhoneNumberFragment(), false)
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}