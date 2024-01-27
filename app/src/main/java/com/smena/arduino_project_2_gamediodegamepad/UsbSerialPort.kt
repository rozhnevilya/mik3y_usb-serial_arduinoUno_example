package com.smena.arduino_project_2_gamediodegamepad

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import java.io.UnsupportedEncodingException

class UsbSerialManager(private val context: Context) {

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbSerialDevice: UsbSerialDevice? = null

    fun requestPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_USB_PERMISSION), 0
        )
        usbManager.requestPermission(device, permissionIntent)
    }

     fun openConnection(device: UsbDevice) {
        val connection = usbManager.openDevice(device)
        if (connection != null) {
            // Открываем соединение с USB-устройством
            usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(device, connection)
            if (usbSerialDevice != null) {
                if (usbSerialDevice!!.open()) {
                    // Настройка параметров соединения, например, скорости передачи данных
                    usbSerialDevice!!.setBaudRate(9600)
                    // Добавьте другие необходимые настройки

                    // Установите слушатель для обработки входящих данных
                    usbSerialDevice!!.read(mCallback)
                }
            }
        }
    }

    // Закрываем соединение с USB-устройством
    fun closeConnection() {
        usbSerialDevice?.close()
    }

    // Обработчик для входящих данных
    private val mCallback = UsbSerialInterface.UsbReadCallback { arg0 ->
        try {
            val data = String(arg0, Charsets.UTF_8)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val ACTION_USB_PERMISSION = "com.your.package.USB_PERMISSION"
    }
}