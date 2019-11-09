package com.honeywell.cosemtestapplication.model.cosem.exceptions

open class CosemErrorException : Exception{
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}