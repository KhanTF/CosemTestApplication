package com.honeywell.cosemtestapplication.model.cosem.port

class TransmitException : Exception {

    val state: TransmitState

    enum class TransmitState {
        ON, OFF
    }

    constructor(state: TransmitState) {
        this.state = state
    }

    constructor(message: String, state: TransmitState) : super(message) {
        this.state = state
    }

    constructor(message: String, cause: Throwable, state: TransmitState) : super(message, cause) {
        this.state = state
    }

    constructor(cause: Throwable, state: TransmitState) : super(cause) {
        this.state = state
    }
}
