package com.honeywell.cosemtestapplication.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.honeywell.cosemtestapplication.R
import com.honeywell.cosemtestapplication.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scanner.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name


class ScannerActivity : BaseActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val REQUEST_CODE_PERMISSIONS = 1000
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                viewModel.onStartScan()
            }
        }
    }

    private val viewModel: ScannerViewModel by viewModel()

    private val adapter = ScannerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        device_list.adapter = adapter
        device_list.layoutManager = LinearLayoutManager(this)
        viewModel.getScannerViewModelState().observe(this::updateViewState)
    }

    private fun updateViewState(scannerViewModelState: ScannerViewModelState): Unit = when (scannerViewModelState) {
        is ScannerViewModelState.ReceivedScannerListViewState -> {
            error_layout.visibility = View.GONE
            adapter.data = scannerViewModelState.devises
        }
        is ScannerViewModelState.StartScanUnavailableState -> {
            error_layout.visibility = View.VISIBLE
            adapter.data = emptyList()

        }
    }

    override fun onStart() {
        super.onStart()
        if (isGranted(PERMISSIONS)) {
            viewModel.onStartScan()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        try {
            registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStopScan()
        try {
            unregisterReceiver(bluetoothReceiver)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isGranted(grantResults)) {
                viewModel.onStartScan()
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
    }

    private fun isGranted(grantResult: IntArray): Boolean {
        for (result in grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    @Suppress("SameParameterValue")
    private fun isGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


}