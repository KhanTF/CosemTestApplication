package com.honeywell.cosemtestapplication.model.cosem.port.manager;

public interface BleDataListener {

    void onReceived(byte[] data);

    void onSend(byte[] data);

}
