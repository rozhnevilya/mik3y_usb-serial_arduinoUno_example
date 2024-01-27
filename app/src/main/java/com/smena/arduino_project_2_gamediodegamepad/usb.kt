package com.smena.arduino_project_2_gamediodegamepad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.Toast

class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            // USB-устройство подключено
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Toast.makeText(context, "USB устройство подключено: ${device?.deviceName}", Toast.LENGTH_SHORT).show()
        } else if (intent?.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
            // USB-устройство отключено
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Toast.makeText(context, "USB устройство отключено: ${device?.deviceName}", Toast.LENGTH_SHORT).show()
        }
    }
}