package com.honeywell.cosemtestapplication.ui.scanner

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.honeywell.cosemtestapplication.R
import kotlinx.android.synthetic.main.item_device.view.*

class ScannerAdapter : RecyclerView.Adapter<ScannerAdapter.ScannerViewHolder>() {

    var data: List<BluetoothDevice> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: (BluetoothDevice) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannerViewHolder {
        return ScannerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ScannerViewHolder, position: Int) {
        holder.bind()
    }


    inner class ScannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() = itemView.apply {
            val device = data[adapterPosition]
            val text = "${device.name} [${device.address}]"
            device_name.text = text

            setOnClickListener {
                listener(device)
            }
        }

    }

}