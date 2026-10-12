package com.example.autoredial

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val perms = mutableListOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS
        )
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        ActivityCompat.requestPermissions(this, perms.toTypedArray(), 1)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val numberInput = EditText(this).apply {
            hint = "電話號碼"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val intervalInput = EditText(this).apply {
            hint = "重撥間隔秒數"
            setText("18")
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val maxInput = EditText(this).apply {
            hint = "最大撥打次數"
            setText("20")
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val startBtn = Button(this).apply { text = "開始撥打" }
        val stopBtn = Button(this).apply { text = "停止" }

        startBtn.setOnClickListener {
            val number = numberInput.text.toString().trim()
            if (number.isEmpty()) {
                Toast.makeText(this, "請輸入電話號碼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val interval = intervalInput.text.toString().toIntOrNull()?.coerceAtLeast(10) ?: 18
            val max = maxInput.text.toString().toIntOrNull()?.coerceIn(1, 50) ?: 20

            val intent = Intent(this, RedialService::class.java).apply {
                putExtra("number", number)
                putExtra("interval", interval)
                putExtra("max", max)
            }
            startForegroundService(intent)
        }

        stopBtn.setOnClickListener {
            startService(Intent(this, RedialService::class.java).setAction("STOP"))
        }

        listOf(numberInput, intervalInput, maxInput, startBtn, stopBtn).forEach { layout.addView(it) }
        setContentView(layout)
    }
}
