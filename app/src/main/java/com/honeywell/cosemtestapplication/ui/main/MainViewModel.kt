package com.honeywell.cosemtestapplication.ui.main

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.honeywell.cosemtestapplication.model.cosem.CosemManager

sealed class MainViewModelState{

}

class MainViewModel(
    private val device: BluetoothDevice,
    private val cosemManager: CosemManager) : ViewModel(){

    init {

    }

    fun onGetCosem(classId: Int, obis: String, attributeId: Int){

    }

    fun onActionCosem(classId: Int, obis: String, attributeId: Int){

    }

    override fun onCleared() {
        super.onCleared()
    }

}