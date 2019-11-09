package com.honeywell.cosemtestapplication.model.cosem

import com.honeywell.cosemtestapplication.model.cosem.exceptions.*
import com.honeywell.cosemtestapplication.model.cosem.exceptions.result.*
import fr.andrea.libcosemclient.common.CosemException
import fr.andrea.libcosemclient.common.ErrorCode
import fr.andrea.libcosemclient.cosem.DataAccessResult
import fr.andrea.libcosemclient.cosem.DataAccessStatus
import fr.andrea.libcosemclient.datatypes.CosemObject

object BleCosemErrorProcessor {

    fun getCosemErrorException(throwable: Throwable): CosemErrorException {
        return when (throwable) {
            is CosemException -> when (throwable.error) {
                ErrorCode.ERROR_ABORTED -> CosemAbortedErrorException(throwable)
                ErrorCode.ERROR_CIPHERING -> CosemCipheringErrorException(throwable)
                ErrorCode.ERROR_DECIPHERING -> CosemDecipheringErrorException(throwable)
                ErrorCode.ERROR_GENERAL_PROTECTION_NEEDED -> CosemGeneralProtectionNeededErrorException(throwable)
                ErrorCode.ERROR_HLS_AUTHENTICATION -> CosemHighLevelSecurityAuthenticationErrorException(throwable)
                ErrorCode.ERROR_INTERNAL -> CosemInternalErrorException(throwable)
                ErrorCode.ERROR_PARAMETER -> CosemParameterErrorException(throwable)
                ErrorCode.ERROR_PORT_COMMUNICATION -> CosemPortCommunicationErrorException(throwable)
                ErrorCode.ERROR_PORT_NOT_CONNECTED -> CosemPortNotConnectedErrorException(throwable)
                ErrorCode.ERROR_SERVER_ASSOCIATION_ALREADY_OPENED -> CosemServerAssociationAlreadyOpenedException(
                    throwable
                )
                ErrorCode.ERROR_SERVER_AUTHENTICATION -> CosemAuthenticationErrorException(throwable)
                ErrorCode.ERROR_SERVER_INVALID_RESPONSE -> CosemInvalidResponseErrorException(throwable)
                ErrorCode.ERROR_SERVER_NO_RESPONSE -> CosemNoResponseErrorException(throwable)
                ErrorCode.ERROR_SERVER_OPERATION_NOT_POSSIBLE -> CosemNotPossibleOperationException(throwable)
                ErrorCode.ERROR_SERVER_OPERATION_NOT_SUPPORTED -> CosemNotSupportedOperationException(throwable)
                ErrorCode.ERROR_SYSTEM_RESOURCES -> CosemSystemResourcesErrorException(throwable)
                ErrorCode.ERROR_SERVER_OTHER_ERROR -> CosemErrorException(throwable)
                else -> CosemErrorException(throwable)
            }
            is CosemErrorException -> throwable
            else -> CosemErrorException(throwable)
        }
    }

    fun <T> throwCosemErrorException(throwable: Throwable): T {
        throw  getCosemErrorException(throwable)
    }

    fun Array<DataAccessResult>.throwIfResultFailed(): Array<CosemObject> {
        return Array(size) {
            get(it).throwIfResultFailed()
        }
    }

    fun DataAccessResult.throwIfResultFailed(): CosemObject {
        if (status != null) {
            status.throwIfStatusFailed()
            return data ?: throw CosemResultException("Data is null")
        } else {
            throw CosemResultException("Status is null")
        }
    }

    fun Array<DataAccessStatus>.throwIfStatusFailed() {
        for (status in this) status.throwIfStatusFailed()
    }

    fun DataAccessStatus.throwIfStatusFailed() {
        when (this) {
            DataAccessStatus.SUCCESS -> return
            DataAccessStatus.HARDWARE_FAULT -> throw CosemResultHardwareFaultException()
            DataAccessStatus.OBJECT_UNAVAILABLE -> throw CosemResultObjectUnavailableException()
            DataAccessStatus.READ_WRITE_DENIED -> throw CosemResultReadWriteDeniedException()
            DataAccessStatus.SCOPE_OF_ACCESS_VIOLATED -> throw CosemResultScopeOfAccessViolatedException()
            DataAccessStatus.TEMPORARY_FAILURE -> throw CosemResultTypeUnmatchedException()
            DataAccessStatus.TYPE_UNMATCHED -> throw CosemResultTypeUnmatchedException()
            DataAccessStatus.OTHER_REASON -> throw CosemResultException()
        }
    }
}