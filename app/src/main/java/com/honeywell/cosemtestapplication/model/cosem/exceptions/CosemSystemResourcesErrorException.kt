package com.honeywell.cosemtestapplication.model.cosem.exceptions

class CosemSystemResourcesErrorException : CosemErrorException{
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}