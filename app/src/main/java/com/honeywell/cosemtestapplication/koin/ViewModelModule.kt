package com.honeywell.cosemtestapplication.koin

import com.honeywell.cosemtestapplication.ui.scanner.ScannerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun prepareViewModelModule() = module {
    viewModel { ScannerViewModel(bluetoothScanner = get()) }
}