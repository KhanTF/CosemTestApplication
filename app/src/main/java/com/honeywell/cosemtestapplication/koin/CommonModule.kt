package com.honeywell.cosemtestapplication.koin

import org.koin.dsl.module
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun prepareCommonModule() = module {
    single { ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue()) }
}