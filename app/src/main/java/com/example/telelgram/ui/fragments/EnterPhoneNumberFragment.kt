package com.example.telelgram.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telelgram.MainActivity
import com.example.telelgram.R
import com.example.telelgram.activities.RegisterActivity
import com.example.telelgram.utilits.AUTH
import com.example.telelgram.utilits.replaceActivity
import com.example.telelgram.utilits.replaceFragment
import com.example.telelgram.utilits.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    } else showToast(task.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
        register_btn_next.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (register_input_phone_number.text.toString().isEmpty()) {
//            Toast.makeText(
//                activity,
//                getString(R.string.register_toast_enter_phone),
//                Toast.LENGTH_SHORT
//            ).show()
            // заменили на
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
//            fragmentManager?.beginTransaction()
//                ?.replace(R.id.DataContainer, EnterCodeFragment())?.addToBackStack(null)
//                ?.commit()
            //   replaceFragment(EnterCodeFragment())
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = register_input_phone_number.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS, activity as RegisterActivity,
            mCallback
        )
    }
}