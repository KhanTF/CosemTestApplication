package com.honeywell.cosemtestapplication.model.cosem

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.honeywell.cosemtestapplication.model.cosem.exceptions.CosemPortNotConnectedErrorException
import com.honeywell.cosemtestapplication.model.cosem.port.PhyPortBleAndroid
import fr.andrea.libcosemclient.auth.IAuth
import fr.andrea.libcosemclient.cosem.*
import fr.andrea.libcosemclient.port.PortHdlcClient
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException
import no.nordicsemi.android.ble.exception.InvalidRequestException
import no.nordicsemi.android.ble.exception.RequestFailedException
import timber.log.Timber
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CosemManagerImpl constructor(
    private val context: Context
) : CosemManager {

    private val executor = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())

    companion object {
        private const val EXECUTION_TIMEOUT = 8L
        private const val CONNECTION_TIMEOUT = 10L
        private const val PAIRING_CONNECTION_TIMEOUT = 15L
        private val AUTH_KEY = byteArrayOf(
            0xd0.toByte(),
            0xd1.toByte(),
            0xd2.toByte(),
            0xd3.toByte(),
            0xd4.toByte(),
            0xd5.toByte(),
            0xd6.toByte(),
            0xd7.toByte(),
            0xd8.toByte(),
            0xd9.toByte(),
            0xda.toByte(),
            0xdb.toByte(),
            0xdc.toByte(),
            0xdd.toByte(),
            0xde.toByte(),
            0xdf.toByte()
        )
        private val BROAD_KEY = byteArrayOf(
            0x0F.toByte(),
            0x0E.toByte(),
            0x0D.toByte(),
            0x0C.toByte(),
            0x0B.toByte(),
            0x0A.toByte(),
            0x09.toByte(),
            0x08.toByte(),
            0x07.toByte(),
            0x06.toByte(),
            0x05.toByte(),
            0x04.toByte(),
            0x03.toByte(),
            0x02.toByte(),
            0x01.toByte(),
            0x00.toByte()
        )
        private val UNICAST_KEY = byteArrayOf(
            0x00.toByte(),
            0x01.toByte(),
            0x02.toByte(),
            0x03.toByte(),
            0x04.toByte(),
            0x05.toByte(),
            0x06.toByte(),
            0x07.toByte(),
            0x08.toByte(),
            0x09.toByte(),
            0x0A.toByte(),
            0x0B.toByte(),
            0x0C.toByte(),
            0x0D.toByte(),
            0x0E.toByte(),
            0x0F.toByte()
        )
    }

    private var phyPort: PhyPortBleAndroid? = null
    private var port: PortHdlcClient? = null
    private var cosem: CosemWrapper? = null
    private var authLevel: IAuth? = null
    private var isPhyPortConnected = false
    private var isPortConnected = false
    private var isCosemAssociationOpened = false
    private var connectedAuth: Auth? = null
    private var connectedDevice: BluetoothDevice? = null
    private val isConnected: Boolean
        get() = isPhyPortConnected && isPortConnected && isCosemAssociationOpened

    private fun connectInternal(device: BluetoothDevice, auth: Auth) {
        Timber.i("Start connect")
        if (isConnected) {
            if (connectedDevice != device) {
                connectedDevice = null
                closeInternal()
                Timber.i("Close before connect")
            } else {
                Timber.i("Already connected")
                return
            }
        }
        val phyPort = PhyPortBleAndroid()
        try {
            this.phyPort = phyPort
            phyPort.connect(device, context)
            isPhyPortConnected = true
            Timber.i("Ble connect")
        } catch (var4: DeviceDisconnectedException) {
            throw CosemPortNotConnectedErrorException(var4)
        } catch (var4: RequestFailedException) {
            throw CosemPortNotConnectedErrorException(var4)
        } catch (var4: InvalidRequestException) {
            throw CosemPortNotConnectedErrorException(var4)
        } catch (var4: BluetoothDisabledException) {
            throw CosemPortNotConnectedErrorException(var4)
        } catch (var4: InterruptedException) {
            throw CosemPortNotConnectedErrorException(var4)
        }

        val logPath = setupLogPath()
        val authLevel = auth.setupAuth(logPath)
        this.authLevel = authLevel
        val port = PortHdlcClient(logPath)
        try {
            this.port = port
            port.initialize(PortHdlcClient.Parameters().also {
                it.serverAddressLength = 2
                it.responseTimeout = 5
                it.keepAliveTimer = 60
                it.serverPhysicalAddress = 17
                it.maxNumberOfRetries = 2
                it.maxInformationFieldReceive = 1024
                it.maxInformationFieldTransmit = 1024
            }, phyPort)
            port.connect(authLevel)
            isPortConnected = true
            Timber.i("HDLC connect")
        } catch (e: Throwable) {
            throw BleCosemErrorProcessor.getCosemErrorException(e)
        }
        val cosem = CosemWrapperImpl()
        if (this.cosem != null) this.cosem?.dispose()
        this.cosem = cosem

        val securityContext = SecurityContext()
        securityContext.securitySuite = SecuritySuite.AES_GCM_128

        val globarBroadcasrKey = SymmetricSecurityKey()
        globarBroadcasrKey.key = BROAD_KEY
        globarBroadcasrKey.rxFrameCounter = 0
        globarBroadcasrKey.txFrameCounter = 0
        securityContext.globalBroadcastEncryptionKey = globarBroadcasrKey
        val unicastBroadcasrKey = SymmetricSecurityKey()
        unicastBroadcasrKey.key = UNICAST_KEY
        unicastBroadcasrKey.rxFrameCounter = 0
        unicastBroadcasrKey.txFrameCounter = 0
        securityContext.globalUnicastEncryptionKey = unicastBroadcasrKey
        securityContext.authenticationKey = AUTH_KEY
        cosem.initialize(ICosem.Parameters().also {
            it.cosemMode = CosemMode.LOGICAL_NAME_REFERENCING_NO_CIPHERING
        }, port, authLevel)
        cosem.setSecurityContext(securityContext)
        cosem.open()

        Timber.i("Cosem connect")
        isCosemAssociationOpened = true

        connectedDevice = device
        connectedAuth = auth
        Timber.i("End connect")
    }

    private fun reconnectInternal(): Boolean {
        closeInternal()
        val auth = connectedAuth
        val device = connectedDevice
        return if (auth != null && device != null) {
            connectInternal(device, auth)
            true
        } else {
            false
        }
    }

    private fun closeInternal() = executor.execute {
        Timber.i("Start close")
        if (isCosemAssociationOpened) try {
            isCosemAssociationOpened = false
            cosem?.close()
            cosem?.dispose()
            Timber.i("Close cosem")
        } catch (e: Throwable) {
            Timber.d(BleCosemErrorProcessor.getCosemErrorException(e).message)
        }
        if (isPortConnected) try {
            isPortConnected = false
            port?.disconnect(authLevel)
            port?.dispose()
            Timber.i("Close HDLC port")
        } catch (e: Throwable) {
            throw BleCosemErrorProcessor.getCosemErrorException(e)
        }
        if (isPhyPortConnected) {
            isPhyPortConnected = false
            phyPort?.close()
            phyPort?.dispose()
            Timber.i("Close PhyPort port")
        }

        authLevel?.dispose()
        Timber.i("Release resources")

        cosem = null
        port = null
        phyPort = null
        authLevel = null
        Timber.i("End close")
    }

    private fun setupLogPath(): String? {
        val file = context.getExternalFilesDir(null)
        if (file != null) {
            val logDir = File(file, "logs-" + System.currentTimeMillis())
            if (logDir.mkdirs()) {
                return logDir.absolutePath
            }
        }
        Timber.w("Failed to create logs folder.")
        return null
    }

    override fun connect(device: BluetoothDevice, auth: Auth) {
        executor.submit {
            connectInternal(device, auth)
        }.get(PAIRING_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
    }

    override fun disconnect() {
        executor.queue.clear()
        executor.submit {
            closeInternal()
        }.get(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
    }

    override fun isConnect(): Boolean = isConnected

    override fun <T> execute(f: CosemWrapper.() -> T): T {
        return executor.submit(
            Callable { cosem?.f() ?: throw IllegalArgumentException("Illegal return value") }
        ).get(EXECUTION_TIMEOUT, TimeUnit.SECONDS)
    }
}