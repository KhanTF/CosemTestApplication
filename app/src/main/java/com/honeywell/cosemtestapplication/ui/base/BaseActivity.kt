package com.honeywell.cosemtestapplication.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseActivity : AppCompatActivity() {

    protected inline fun <T> LiveData<T>.observe(crossinline f: (T) -> Unit) {
        return observe(this@BaseActivity, Observer { f(it) })
    }

}