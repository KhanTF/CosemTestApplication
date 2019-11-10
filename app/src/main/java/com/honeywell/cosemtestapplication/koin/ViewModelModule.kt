package com.honeywell.cosemtestapplication.koin

import android.bluetooth.BluetoothDevice
import com.honeywell.cosemtestapplication.ui.main.MainViewModel
import com.honeywell.cosemtestapplication.ui.scanner.ScannerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

fun prepareViewModelModule() = module {
    viewModel { ScannerViewModel(bluetoothScanner = get()) }
    viewModel { (device: BluetoothDevice, logPath: File?) -> MainViewModel(device = device, cosemManager = get(), logPath = logPath) }
}