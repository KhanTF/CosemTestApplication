package com.honeywell.cosemtestapplication.model.cosem

import com.honeywell.cosemtestapplication.model.cosem.BleCosemErrorProcessor.throwCosemErrorException
import com.honeywell.cosemtestapplication.model.cosem.BleCosemErrorProcessor.throwIfResultFailed
import com.honeywell.cosemtestapplication.model.cosem.BleCosemErrorProcessor.throwIfStatusFailed
import com.honeywell.cosemtestapplication.model.cosem.exceptions.CosemErrorException
import fr.andrea.libcosemclient.auth.IAuth
import fr.andrea.libcosemclient.cosem.*
import fr.andrea.libcosemclient.datatypes.CosemObject
import fr.andrea.libcosemclient.port.IPort

class CosemWrapperImpl(logPath: String? = null) : CosemWrapper {

    private val cosem: ICosem = Cosem(logPath)

    override fun setLogPath(path: String?) {
        cosem.setLogPath(path)
    }

    override fun addDataNotificationListener(var1: IDataNotificationListener) {
        cosem.addDataNotificationListener(var1)
    }

    override fun removeDataNotificationListener(var1: IDataNotificationListener) {
        cosem.removeDataNotificationListener(var1)
    }

    override fun addProgressCancelListener(var1: IProgressCancelListener) {
        cosem.addProgressCancelListener(var1)
    }

    override fun removeProgressCancelListener(var1: IProgressCancelListener) {
        cosem.removeProgressCancelListener(var1)
    }

    override fun getVersion(): String {
        return cosem.version
    }

    override fun setConfigurationParameter(var1: ConfigurationParameter, var2: Any) {
        cosem.setConfigurationParameter(var1, var2)
    }

    override fun getConfigurationParameter(var1: ConfigurationParameter): Any {
        return cosem.getConfigurationParameter(var1)
    }

    override fun setSecurityContext(var1: SecurityContext) {
        cosem.securityContext = var1
    }

    override fun getSecurityContext(): SecurityContext {
        return cosem.securityContext
    }

    override fun setMessageSecurity(var1: MessageSecurity) {
        cosem.messageSecurity = var1
    }

    override fun getMessageSecurity(): MessageSecurity {
        return cosem.messageSecurity
    }

    @Throws(CosemErrorException::class)
    override fun initialize(var1: ICosem.Parameters, var2: IPort, var3: IAuth): Unit = try {
        cosem.initialize(var1, var2, var3)
    } catch (t: Throwable) {
        throwCosemErrorException(t)
    }

    @Throws(CosemErrorException::class)
    override fun open(): Unit = try {
        cosem.open()
    } catch (t: Throwable) {
        throwCosemErrorException(t)
    }

    @Throws(CosemErrorException::class)
    override fun open(var1: Int): Unit = try {
        cosem.open(var1)

    } catch (t: Throwable) {
        throwCosemErrorException(t)
    }

    @Throws(CosemErrorException::class)
    override fun close(): Unit = try {
        cosem.close()
    } catch (t: Throwable) {
        throwCosemErrorException(t)
    }

    override fun get(var1: LogicalName): CosemObject {
        return try {
            return cosem.get(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun get(var1: LogicalName, var2: SelectiveAccessDescriptor): CosemObject {
        return try {
            return cosem.get(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun get(var1: Array<LogicalName>): Array<CosemObject> {
        return try {
            return cosem.get(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun get(var1: Array<LogicalName>, var2: Array<SelectiveAccessDescriptor>): Array<CosemObject> {
        return try {
            return cosem.get(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun set(var1: LogicalName, var2: CosemObject) {
        return try {
            return cosem.set(var1, var2).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun set(var1: LogicalName, var2: SelectiveAccessDescriptor, var3: CosemObject) {
        return try {
            return cosem.set(var1, var2, var3).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun set(var1: Array<LogicalName>, var2: Array<CosemObject>) {
        return try {
            return cosem.set(var1, var2).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun set(var1: Array<LogicalName>, var2: Array<SelectiveAccessDescriptor>, var3: Array<CosemObject>) {
        return try {
            return cosem.set(var1, var2, var3).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun action(var1: LogicalName): CosemObject {
        return try {
            return cosem.action(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun action(var1: LogicalName, var2: CosemObject): CosemObject {
        return try {
            return cosem.action(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun action(var1: Array<LogicalName>, var2: Array<CosemObject>): Array<CosemObject> {
        return try {
            return cosem.action(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun access(var1: AccessRequest): CosemObject {
        return try {
            return cosem.access(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun access(var1: Array<AccessRequest>): Array<CosemObject> {
        return try {
            return cosem.access(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun read(var1: Short): CosemObject {
        return try {
            return cosem.read(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun read(var1: Short, var2: SelectiveAccessDescriptor): CosemObject {
        return try {
            return cosem.read(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun read(var1: ShortArray): Array<CosemObject> {
        return try {
            return cosem.read(var1).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun read(var1: ShortArray, var2: Array<SelectiveAccessDescriptor>): Array<CosemObject> {
        return try {
            return cosem.read(var1, var2).throwIfResultFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun write(var1: Short, var2: CosemObject) {
        return try {
            return cosem.write(var1, var2).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun write(var1: Short, var2: SelectiveAccessDescriptor, var3: CosemObject) {
        return try {
            return cosem.write(var1, var2, var3).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun write(var1: ShortArray, var2: Array<CosemObject>) {
        return try {
            return cosem.write(var1, var2).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun write(var1: ShortArray, var2: Array<SelectiveAccessDescriptor>, var3: Array<CosemObject>) {
        return try {
            return cosem.write(var1, var2, var3).throwIfStatusFailed()
        } catch (e: Exception) {
            throwCosemErrorException(e)
        }
    }

    override fun dispose() {
        cosem.dispose()
    }

}