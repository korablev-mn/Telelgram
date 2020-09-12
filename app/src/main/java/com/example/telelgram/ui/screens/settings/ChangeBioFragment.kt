package com.example.telelgram.ui.screens.settings

import com.example.telelgram.R
import com.example.telelgram.database.*
import com.example.telelgram.ui.screens.base.BaseChangeFragment
import kotlinx.android.synthetic.main.fragment_change_bio.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()
        setBioDatabase(newBio)
    }
}