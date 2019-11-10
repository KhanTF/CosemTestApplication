package com.honeywell.cosemtestapplication.koin

import com.honeywell.cosemtestapplication.model.cosem.CosemManager
import com.honeywell.cosemtestapplication.model.cosem.CosemManagerImpl
import com.honeywell.cosemtestapplication.model.scanner.BluetoothScanner
import com.honeywell.cosemtestapplication.model.scanner.BluetoothScannerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.bind
import org.koin.dsl.module

fun prepareCosemModule() = module {
    single { CosemManagerImpl(context = androidApplication(), executor = get()) } bind CosemManager::class
    single { BluetoothScannerImpl(context = androidApplication()) } bind BluetoothScanner::class
}