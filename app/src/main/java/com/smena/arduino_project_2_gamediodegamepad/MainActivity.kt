package com.smena.arduino_project_2_gamediodegamepad

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val usbReceiver = UsbReceiver()
    val filter = IntentFilter()

    private val usbManager: UsbManager by lazy {
        getSystemService(Context.USB_SERVICE) as UsbManager
    }
    private lateinit var usbSerialManager: UsbSerialManager

    var portNum: Int = 0
    private var usbSerialPort: UsbSerialPort? = null

    private enum class UsbPermission {
        Unknown, Requested, Granted, Denied
    }

    private var usbPermission = UsbPermission.Unknown
    private val INTENT_ACTION_GRANT_USB = "${BuildConfig.APPLICATION_ID}.GRANT_USB"
    private val withIoManager = false
    private var usbIoManager: SerialInputOutputManager? = null
    private var connected = false

    private val responseList = mutableListOf<DC_recyclerView_item>()
    private lateinit var adapter: recyclerViewAdapter

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализация recyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = recyclerViewAdapter(responseList)
        recyclerView.adapter = adapter

        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener{
            usbConnect()
        }

//        val buttonWrite: Button = findViewById(R.id.buttonWrite)
//        buttonWrite.setOnClickListener{
//            sendData()
//        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("test seekbar", progress.toString())
                onOrOffLightImage(progress)
                sendData((progress*10).toString())
                Toast.makeText(applicationContext, (progress*10).toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Вызывается при начале перемещения ползунка
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    fun onOrOffLightImage(seekBarValue: Int){

        runOnUiThread{

            val red1 = findViewById<ImageView>(R.id.red1)
            val red2 = findViewById<ImageView>(R.id.red2)
            val red3 = findViewById<ImageView>(R.id.red3)
            val yellow1 = findViewById<ImageView>(R.id.yellow1)
            val yellow2 = findViewById<ImageView>(R.id.yellow2)
            val yellow3 = findViewById<ImageView>(R.id.yellow3)
            val green1 = findViewById<ImageView>(R.id.green1)
            val green2 = findViewById<ImageView>(R.id.green2)
            val green3 = findViewById<ImageView>(R.id.green3)

            if (seekBarValue > 0 && seekBarValue < 10) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }

            }
            else if (seekBarValue > 10 &&seekBarValue < 20) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 20 &&seekBarValue < 30) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 30 &&seekBarValue < 40) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 40 &&seekBarValue < 50) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 50 &&seekBarValue < 60) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 60 &&seekBarValue < 70) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 70 &&seekBarValue < 80) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 80 &&seekBarValue < 90) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
            else if (seekBarValue > 90 &&seekBarValue < 100) {
                runOnUiThread{
                    red1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    red3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_red))
                    yellow1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    yellow3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_yellow))
                    green1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_green))
                    green2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_off_green))
                    green3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.led_on_green))
                }
            }
        }
    }

    fun usbConnect() {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDevices: List<UsbDevice> = usbManager.deviceList.values.toList()

        if (usbDevices.isEmpty()) {
            showToast("Connection failed: no devices found")
            return
        }

        val device = usbDevices[0]
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)

        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device)
        }

        if (driver == null) {
            showToast("Connection failed: no driver for device")
            return
        }

        if (driver.ports.size < portNum) {
            showToast("Connection failed: not enough ports at device")
            return
        }

        usbSerialPort = driver.ports[portNum]
        val usbConnection = usbManager.openDevice(driver.device)

        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.device)) {
            usbPermission = UsbPermission.Requested
            val flags = PendingIntent.FLAG_MUTABLE
            val usbPermissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(INTENT_ACTION_GRANT_USB),
                flags
            )

            usbManager.requestPermission(driver.device, usbPermissionIntent)
            showToast("Приложение может работать с устройством")
            return
        }

        if (usbSerialPort != null && usbSerialPort!!.isOpen) {
            showToast("Successful")
        } else {
            try {
                usbSerialPort?.open(usbConnection)

                Thread {
                    // Проверяем, что порт открыт и соединение успешно
                }.start()

                val thread = Thread {
                    try {
                        while (connected) {

                            if (usbSerialPort != null && usbSerialPort!!.isOpen) {
                                val buffer = ByteArray(1024)
                                val bytesRead = usbSerialPort?.read(buffer, 50)

                                if (bytesRead != null && bytesRead > 0) {
                                    val receivedData = String(buffer, 0, bytesRead)
                                    runOnUiThread {
                                        handleReceivedData(receivedData)
                                    }
                                } else {
                                    runOnUiThread {
                                        showToast("No data received")
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    showToast("USB port is not open or not connected")
                                }
                            }
                        }
                    } catch (e: IOException) {
                        runOnUiThread {
                            showToast("Error reading data: ${e.message}")
                        }
                    }
                }

                thread.start()
                runOnUiThread {
                    showToast("Приложение может работать с устройством")
                }
                connected = true
            } catch (e: Exception) {
                showToast("Connection failed: ${e.message}")
                connected = false
                try {
                    usbSerialPort?.close()
                } catch (ignored: IOException) {
                }
                usbSerialPort = null
            }
        }

        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.device)) {
                showToast("Connection failed: permission denied")
            } else {
                showToast("Connection failed: open failed")
            }
        }
    }

    // Добавьте функцию sendData в ваш класс активности
    fun sendData(data: String) {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDevices: List<UsbDevice> = usbManager.deviceList.values.toList()

        if (usbDevices.isEmpty()) {
            showToast("Connection failed: no devices found")
            return
        }

        val device = usbDevices[0]
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)

        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device)
        }

        if (driver == null) {
            showToast("Connection failed: no driver for device")
            return
        }

        if (driver.ports.size < portNum) {
            showToast("Connection failed: not enough ports at device")
            return
        }

        usbSerialPort = driver.ports[portNum]
        val usbConnection = usbManager.openDevice(driver.device)

        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.device)) {
            usbPermission = UsbPermission.Requested
            val flags = PendingIntent.FLAG_MUTABLE
            val usbPermissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(INTENT_ACTION_GRANT_USB),
                flags
            )

            usbManager.requestPermission(driver.device, usbPermissionIntent)
            showToast("Приложение может работать с устройством")
            return
        }

        if (usbSerialPort != null && usbSerialPort!!.isOpen) {
            showToast("Successful")
        } else {
            try {
                usbSerialPort?.open(usbConnection)

                Thread {
                    // Проверяем, что порт открыт и соединение успешно
                }.start()

                val thread = Thread {
                    try {

                        val byteArray = ByteArray(4)
                        byteArray[0] = (data.toInt() and 0xFF).toByte()
                        byteArray[1] = ((data.toInt() shr 8) and 0xFF).toByte()
                        byteArray[2] = ((data.toInt() shr 16) and 0xFF).toByte()
                        byteArray[3] = ((data.toInt() shr 24) and 0xFF).toByte()

                        usbSerialPort?.write(byteArray, 50)
                    } catch (e: IOException) {
                        runOnUiThread {
                            showToast("Error reading data: ${e.message}")
                        }
                    }
                }

                thread.start()
                runOnUiThread {
                    showToast("Приложение может работать с устройством")
                }
                connected = true
            } catch (e: Exception) {
                showToast("Connection failed: ${e.message}")
                connected = false
                try {
                    usbSerialPort?.close()
                } catch (ignored: IOException) {
                }
                usbSerialPort = null
            }
        }

        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.device)) {
                showToast("Connection failed: permission denied")
            } else {
                showToast("Connection failed: open failed")
            }
        }
    }


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun handleReceivedData(data: String) {

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val time = simpleDateFormat.format(calendar.time)
        val responseItem = DC_recyclerView_item(time, data)
        // Обработка полученных данных
        runOnUiThread {
            responseList.add(0, responseItem)  // Вставляем новый элемент в начало списка
            adapter.notifyItemRangeInserted(0, 5)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.itemAnimator = null
        recyclerView.scrollToPosition(0)
    }
}