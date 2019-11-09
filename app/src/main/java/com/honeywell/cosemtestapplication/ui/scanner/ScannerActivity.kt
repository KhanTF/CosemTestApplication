package com.honeywell.cosemtestapplication.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.honeywell.cosemtestapplication.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScannerActivity : BaseActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val REQUEST_CODE_PERMISSIONS = 1000
    }

    private val viewModel: ScannerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getScnnerViewModelState().observe(this::updateViewState)
    }

    private fun updateViewState(scannerViewModelState: ScannerViewModelState) : Unit = when(scannerViewModelState){
        is ScannerViewModelState.ReceivedScannerListViewState ->{

        }
        is ScannerViewModelState.BluetoothDisabledViewState -> {

        }
        is ScannerViewModelState.StartScanUnavailableState ->{

        }
    }

    override fun onResume() {
        super.onResume()
        if (isGranted(PERMISSIONS)) {
            viewModel.onStartScan()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onStopScan()
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