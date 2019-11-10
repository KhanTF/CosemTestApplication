package com.honeywell.cosemtestapplication.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.honeywell.cosemtestapplication.R
import com.honeywell.cosemtestapplication.isGranted
import com.honeywell.cosemtestapplication.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scanner.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.honeywell.cosemtestapplication.ui.main.MainActivity


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

    private val adapter = ScannerAdapter().apply {
        listener = { MainActivity.start(this@ScannerActivity, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scanner"
        device_list.adapter = adapter
        device_list.layoutManager = LinearLayoutManager(this)
        viewModel.getScannerDevicesList().observe {
            adapter.data = it
        }
        viewModel.getScannerError().observe {
            error_layout.visibility = if (it) View.VISIBLE else View.GONE
        }
        retry.setOnClickListener {
            viewModel.onStartScan()
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

}