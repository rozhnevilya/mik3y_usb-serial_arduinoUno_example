package com.smena.arduino_project_2_gamediodegamepad

import com.hoho.android.usbserial.driver.FtdiSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialProber

internal object CustomProber {
    fun getCustomProber(): UsbSerialProber {
        val customTable = ProbeTable()
        customTable.addProduct(0x1234, 0x0001, FtdiSerialDriver::class.java)
        customTable.addProduct(0x1234, 0x0002, FtdiSerialDriver::class.java)
        return UsbSerialProber(customTable)
    }
}