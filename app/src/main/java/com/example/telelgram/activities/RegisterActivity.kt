//package com.example.telelgram.activities
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import com.example.telelgram.R
//import com.example.telelgram.databinding.ActivityRegisterBinding
//import com.example.telelgram.ui.fragments.register.EnterPhoneNumberFragment
//import com.example.telelgram.database.initFirebase
//import com.example.telelgram.utilits.replaceActivity
//import com.example.telelgram.utilits.replaceFragment
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var mBinding: ActivityRegisterBinding
//    private lateinit var mToolbar: Toolbar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
//        setContentView(mBinding.root)
//        initFirebase()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mToolbar = mBinding.registerToolbar
//        setSupportActionBar(mToolbar)
//        title = getString(R.string.register_title_your_phone)
//        replaceFragment(EnterPhoneNumberFragment(), false)
//    }
//}

//удалено для реализации паттерна Single Activitu Architekt