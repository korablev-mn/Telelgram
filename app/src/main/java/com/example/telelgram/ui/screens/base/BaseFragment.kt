package com.example.telelgram.ui.screens.base

import androidx.fragment.app.Fragment
import com.example.telelgram.utilits.APP_ACTIVITY
import com.example.telelgram.utilits.hideKeyboard

open class BaseFragment(layout:Int) : Fragment(layout) {

//    private lateinit var mRootView: View
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        mRootView = inflater.inflate(layout, container, false)
//        return mRootView
//    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

//    override fun onStop() {
//        super.onStop()
//        APP_ACTIVITY.mAppDrawer.enableDrawer()
//    }
}
