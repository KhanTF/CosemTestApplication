package com.honeywell.cosemtestapplication.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    protected fun Disposable.disposeWhenCleared(){
        disposables.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}