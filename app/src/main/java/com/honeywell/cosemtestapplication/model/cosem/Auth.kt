package com.honeywell.cosemtestapplication.model.cosem

import fr.andrea.libcosemclient.auth.*
import fr.andrea.libcosemclient.auth.AuthLowLevel

sealed class Auth(protected val clientId: Short, protected val logicalDeviceId: Short){
    abstract fun setupAuth(logPath: String?) : IAuth
}

class AuthLowest(clientId: Short, logicalDeviceId: Short) : Auth(clientId, logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthLowestLevel.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
        }
        val auth = AuthLowestLevel(logPath)
        auth.initialize(parameters)
        return auth
    }
}

class AuthLow(clientId: Short, logicalDeviceId: Short, private val password: String) : Auth(clientId, logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthLowLevel.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.password = password
        }
        val auth = AuthLowLevel(logPath)
        auth.initialize(parameters)
        return auth
    }
}

class AuthHighEcdsa(clientId: Short, logicalDeviceId: Short, private val clientToServerChallenge: String?, private val clientPrivateKey: ByteArray, private val serverPublicKey: ByteArray) : Auth(clientId,logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthHighLevel_Ecdsa.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.clientToServerChallenge = clientToServerChallenge
            it.clientPrivateKey = clientPrivateKey
            it.serverPublicKey = serverPublicKey
        }
        val auth = AuthHighLevel_Ecdsa(logPath)
        auth.initialize(parameters)
        return auth
    }
}
class AuthHighGmac(clientId: Short, logicalDeviceId: Short, private val clientToServerChallenge: String?) : Auth(clientId,logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthHighLevel_Gmac.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.clientToServerChallenge = clientToServerChallenge
        }
        val auth = AuthHighLevel_Gmac(logPath)
        auth.initialize(parameters)
        return auth
    }
}
class AuthHighMd5(clientId: Short, logicalDeviceId: Short, private val clientToServerChallenge: String?, private val secret: String) : Auth(clientId,logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthHighLevel_Md5.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.clientToServerChallenge = clientToServerChallenge
            it.secret = secret
        }
        val auth = AuthHighLevel_Md5(logPath)
        auth.initialize(parameters)
        return auth
    }
}
class AuthHighSha1(clientId: Short, logicalDeviceId: Short, private val clientToServerChallenge: String?, private val secret: String) : Auth(clientId,logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthHighLevel_Sha1.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.clientToServerChallenge = clientToServerChallenge
            it.secret = secret
        }
        val auth = AuthHighLevel_Sha1(logPath)
        auth.initialize(parameters)
        return auth
    }
}
class AuthHighSha256(clientId: Short, logicalDeviceId: Short, private val clientToServerChallenge: String?, private val secret: String) : Auth(clientId,logicalDeviceId){
    override fun setupAuth(logPath: String?): IAuth {
        val parameters = IAuthHighLevel_Sha256.Parameters().also {
            it.clientId = clientId
            it.logicalDeviceId = logicalDeviceId
            it.clientToServerChallenge = clientToServerChallenge
            it.secret = secret
        }
        val auth = AuthHighLevel_Sha256(logPath)
        auth.initialize(parameters)
        return auth
    }
}