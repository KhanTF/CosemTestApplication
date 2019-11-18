package com.honeywell.cosemtestapplication.ui.main

import android.bluetooth.BluetoothDevice
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.honeywell.cosemtestapplication.model.cosem.CosemManager
import com.honeywell.cosemtestapplication.model.cosem.port.manager.BleDataListener
import com.honeywell.cosemtestapplication.model.excel.ExcelReader
import com.honeywell.cosemtestapplication.ui.base.BaseViewModel
import fr.andrea.libcosemclient.common.HexConvert
import fr.andrea.libcosemclient.cosem.LogicalName
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val logPath: File?,
    private val device: BluetoothDevice,
    private val cosemManager: CosemManager,
    private val excelReader: ExcelReader
) : BaseViewModel() {

    @Volatile
    private var builder = StringBuilder()

    private val progressMutableLiveData = MutableLiveData<String>()

    private val deviceNameLiveData = MutableLiveData<String>()

    private val isProgressMutableLiveDate = MutableLiveData<Boolean>()

    private val formatter = SimpleDateFormat("HH:mm:ss:SS", Locale.US)

    fun getLogLiveData(): LiveData<String> = progressMutableLiveData

    fun getIsProgressLiveData(): LiveData<Boolean> = isProgressMutableLiveDate

    fun getDeviceNameLiveData(): LiveData<String> = deviceNameLiveData

    private val dataListener = object : BleDataListener {
        override fun onReceived(data: ByteArray?) {
            if (data != null)
                setProgress("Received : [ ${HexConvert.bytesToHex(data)} ]")
        }

        override fun onSend(data: ByteArray?) {
            if (data != null)
                setProgress("Send : [ ${HexConvert.bytesToHex(data)} ]")
        }
    }

    init {
        if (logPath != null)
            cosemManager.setLogPath(logPath.absolutePath)
        deviceNameLiveData.value = device.name ?: device.address
    }

    @Synchronized
    private fun setProgress(progress: String) {
        builder.append(formatter.format(Date(System.currentTimeMillis()))).append(" - ").append(progress).append("\n")
        progressMutableLiveData.postValue(builder.toString())
    }

    fun onStartExcelGetTest(uri: Uri) {
        Completable
            .fromAction {
                setProgress("Connecting...")
                cosemManager.connect(device, dataListener)
                setProgress("Connected")
            }
            .andThen(excelReader.getExcelLogicalNames(uri))
            .map {
                try {
                    cosemManager.execute { get(it) }
                    builder
                        .append("Success ${it.name}, classId = ${it.classId}, obis = ${HexConvert.bytesToHex(it.instanceId)}, attributeId = ${it.attributeId}, version = ${it.version}")
                        .append("\n")
                } catch (e: Throwable) {
                    val bytes = ByteArrayOutputStream()
                    val print = PrintStream(bytes)
                    e.printStackTrace(print)
                    e.printStackTrace()
                    builder
                        .append("Fail ${it.name}, classId = ${it.classId}, obis = ${HexConvert.bytesToHex(it.instanceId)}, attributeId = ${it.attributeId}, version = ${it.version}")
                        .append("\n")
                        .append("Caused by : ${e.message}, Stacktrace : ")
                        .append(String(bytes.toByteArray()))
                        .append("\n")
                }
            }
            .doOnSubscribe {
                builder = StringBuilder()
                isProgressMutableLiveDate.postValue(true)
            }
            .doFinally {
                setProgress("Disconnecting...")
                cosemManager.disconnect()
                setProgress("Disconnected")
                isProgressMutableLiveDate.postValue(false)
            }
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressMutableLiveData.value = it.toString()
            }, {
                it.printStackTrace()
            })
            .disposeWhenCleared()
    }

    fun onGetCosem(classId: Int, obisHex: String, attributeId: Int) {
        Completable
            .fromAction {
                setProgress("Connecting...")
                cosemManager.connect(device, dataListener)
                setProgress("Connected")
            }
            .andThen(Single.fromCallable {
                setProgress("Execute get request, classId = $classId, obis = $obisHex, attributeId = $attributeId")
                cosemManager.execute {
                    get(
                        LogicalName(
                            classId,
                            HexConvert.hexToBytes(obisHex),
                            attributeId.toByte()
                        )
                    )
                }
            })
            .doOnSubscribe {
                isProgressMutableLiveDate.postValue(true)
            }
            .doFinally {
                setProgress("Disconnecting...")
                cosemManager.disconnect()
                setProgress("Disconnected")
                isProgressMutableLiveDate.postValue(false)
            }
            .doOnSubscribe {
                builder = StringBuilder()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setProgress("Response : $it")
            }, {
                val bytes = ByteArrayOutputStream()
                val print = PrintStream(bytes)
                it.printStackTrace(print)
                setProgress("Error : ${it.message}")
                setProgress("Stack Trace : ${String(bytes.toByteArray())}")
            })
            .disposeWhenCleared()
    }

    fun onActionCosem(classId: Int, obisHex: String, attributeId: Int) {
        Completable
            .fromAction {
                setProgress("Connecting...")
                cosemManager.connect(device, dataListener)
                setProgress("Connected")
            }
            .andThen(Single.fromCallable {
                setProgress("Execute action request, classId = $classId, obis = $obisHex, attributeId = $attributeId")
                cosemManager.execute {
                    get(
                        LogicalName(
                            classId,
                            HexConvert.hexToBytes(obisHex),
                            attributeId.toByte()
                        )
                    )
                }
            })
            .doOnSubscribe {
                isProgressMutableLiveDate.postValue(true)
            }
            .doFinally {
                setProgress("Disconnecting...")
                cosemManager.disconnect()
                setProgress("Disconnected")
                isProgressMutableLiveDate.postValue(false)
            }
            .doOnSubscribe {
                builder = StringBuilder()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setProgress("Response : $it")
            }, {
                val bytes = ByteArrayOutputStream()
                val print = PrintStream(bytes)
                it.printStackTrace(print)
                setProgress("Error : ${it.message}")
                setProgress("Stack Trace : ${String(bytes.toByteArray())}")
            })
            .disposeWhenCleared()
    }

}