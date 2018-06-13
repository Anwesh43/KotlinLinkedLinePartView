package com.anwesh.uiprojects.kotlinlinkedlinepartview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedlinepartview.LinkedLinePartView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedLinePartView.create(this)
    }
}
