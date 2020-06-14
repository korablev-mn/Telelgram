package com.example.telelgram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.telelgram.MainActivity
import com.example.telelgram.R
import com.example.telelgram.utilits.APP_ACTIVITY
import com.example.telelgram.utilits.hideKeyboard

open class BaseChangeFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)   //параметр создающий меню
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(
            R.menu.settings_menu_confirm,
            menu
        ) // надувает меню
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_change -> change() // клик
        }
        return true
    }

    open fun change() {

    }
}
