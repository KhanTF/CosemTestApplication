package com.honeywell.cosemtestapplication.model.cosem

import fr.andrea.libcosemclient.auth.IAuth
import fr.andrea.libcosemclient.common.IDisposable
import fr.andrea.libcosemclient.cosem.*
import fr.andrea.libcosemclient.datatypes.CosemObject
import fr.andrea.libcosemclient.port.IPort

interface CosemWrapper : IDisposable {

    fun getVersion(): String

    fun setConfigurationParameter(var1: ConfigurationParameter, var2: Any)

    fun getConfigurationParameter(var1: ConfigurationParameter): Any

    fun setSecurityContext(var1: SecurityContext)

    fun getSecurityContext(): SecurityContext

    fun setMessageSecurity(var1: MessageSecurity)

    fun getMessageSecurity(): MessageSecurity

    fun initialize(var1: ICosem.Parameters, var2: IPort, var3: IAuth)

    fun addDataNotificationListener(var1: IDataNotificationListener)

    fun removeDataNotificationListener(var1: IDataNotificationListener)

    fun addProgressCancelListener(var1: IProgressCancelListener)

    fun removeProgressCancelListener(var1: IProgressCancelListener)

    fun open()

    fun open(var1: Int)

    fun close()

    fun get(var1: LogicalName): CosemObject

    fun get(var1: LogicalName, var2: SelectiveAccessDescriptor): CosemObject

    fun get(var1: Array<LogicalName>): Array<CosemObject>

    fun get(var1: Array<LogicalName>, var2: Array<SelectiveAccessDescriptor>): Array<CosemObject>

    fun set(var1: LogicalName, var2: CosemObject)

    fun set(var1: LogicalName, var2: SelectiveAccessDescriptor, var3: CosemObject)

    fun set(var1: Array<LogicalName>, var2: Array<CosemObject>)

    fun set(var1: Array<LogicalName>, var2: Array<SelectiveAccessDescriptor>, var3: Array<CosemObject>)

    fun action(var1: LogicalName): CosemObject

    fun action(var1: LogicalName, var2: CosemObject): CosemObject

    fun action(var1: Array<LogicalName>, var2: Array<CosemObject>): Array<CosemObject>

    fun access(var1: AccessRequest): CosemObject

    fun access(var1: Array<AccessRequest>): Array<CosemObject>

    fun read(var1: Short): CosemObject

    fun read(var1: Short, var2: SelectiveAccessDescriptor): CosemObject

    fun read(var1: ShortArray): Array<CosemObject>

    fun read(var1: ShortArray, var2: Array<SelectiveAccessDescriptor>): Array<CosemObject>

    fun write(var1: Short, var2: CosemObject)

    fun write(var1: Short, var2: SelectiveAccessDescriptor, var3: CosemObject)

    fun write(var1: ShortArray, var2: Array<CosemObject>)

    fun write(var1: ShortArray, var2: Array<SelectiveAccessDescriptor>, var3: Array<CosemObject>)

}