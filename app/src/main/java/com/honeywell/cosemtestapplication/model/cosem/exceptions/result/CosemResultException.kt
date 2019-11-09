package com.honeywell.cosemtestapplication.model.cosem.exceptions.result

import com.honeywell.cosemtestapplication.model.cosem.exceptions.CosemErrorException

class CosemResultException : CosemErrorException{
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}