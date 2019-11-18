package com.honeywell.cosemtestapplication.ui.main

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import com.honeywell.cosemtestapplication.R
import com.honeywell.cosemtestapplication.isGranted
import com.honeywell.cosemtestapplication.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class MainActivity : BaseActivity() {

    companion object {
        private const val KEY_DEVICE = "KEY_DEVICE"
        private const val REQUEST_CODE_SELECT_TEST = 1
        fun start(activity: Activity, bluetoothDevice: BluetoothDevice) {
            activity.startActivity(Intent(activity, MainActivity::class.java).putExtra(KEY_DEVICE, bluetoothDevice))
        }

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val viewModel: MainViewModel by viewModel {
        val device = requireNotNull(intent.getParcelableExtra<BluetoothDevice>(KEY_DEVICE))
        parametersOf(device, getExternalFilesDir(null))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { back() }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getLogLiveData().observe {
            response.text = it
        }
        viewModel.getIsProgressLiveData().observe {
            progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.getDeviceNameLiveData().observe {
            supportActionBar?.title = it
        }

        get.setOnClickListener {
            val classId = class_id.text.toString().toIntOrNull()
            val obis = obis.text.toString().replace("-", "")
            val attrId = attribute_id.text.toString().toIntOrNull()

            hideKeyboard()

            if (classId != null && attrId != null && obis.length == 12)
                viewModel.onGetCosem(classId, obis, attrId)
            else
                Toast.makeText(this, "Неверные параметры", Toast.LENGTH_SHORT).show()
        }
        action.setOnClickListener {
            val classId = class_id.text.toString().toIntOrNull()
            val obis = obis.text.toString().replace("-", "")
            val attrId = attribute_id.text.toString().toIntOrNull()

            hideKeyboard()

            if (classId != null && attrId != null && obis.length == 12)
                viewModel.onActionCosem(classId, obis, attrId)
            else
                Toast.makeText(this, "Неверные параметры", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            runIfSessionEnded {
                val data = viewModel.getLogLiveData().value.orEmpty()
                val logs = File(filesDir, "logs").also { it.mkdir() }
                val log = File(
                    logs,
                    "logs-${System.currentTimeMillis()}-${viewModel.getDeviceNameLiveData().value.orEmpty()}.txt"
                )
                BufferedWriter(FileWriter(log)).use { it.write(data) }
                val uri = getUriForFile(this, "com.honeywell.cosemtestapplication", log)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)
            }
        } else if (item.itemId == R.id.test) {
            runIfSessionEnded {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, REQUEST_CODE_SELECT_TEST)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_TEST) {
            val uri = data?.data
            if (uri != null) viewModel.onStartExcelGetTest(uri)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isGranted(PERMISSIONS)) {
            finish()
        }
    }

    private inline fun runIfSessionEnded(f: () -> Unit) {
        if (viewModel.getIsProgressLiveData().value == true) {
            Toast.makeText(this, "Дождитесь окончания сессии", Toast.LENGTH_SHORT).show()
        } else {
            f()
        }
    }

    private fun back() {
        runIfSessionEnded { finish() }
    }

    override fun onSupportNavigateUp(): Boolean {
        back()
        return true
    }

    override fun onBackPressed() {
        back()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
