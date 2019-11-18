package com.honeywell.cosemtestapplication.koin

import com.honeywell.cosemtestapplication.model.excel.ExcelReader
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun prepareCommonModule() = module {
    single { ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue()) }
    single { ExcelReader(context = androidApplication()) }
}