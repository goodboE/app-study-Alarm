package com.example.app_study_alarm

import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initOnOffButton()
        initChangeAlarmTimeButton()



        // TODO 1. 데이터 가져오기

        val model = fetchDataFromSharedPreferences()
        renderView(model)


        // TODO 2. 뷰에 데이터 그려주기

    }

    private fun initOnOffButton() {
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener {
            // TODO 저장한 데이터 확인
            // TODO ON/OFF 에 따라 작업 처리 (OFF -> 알람 제거, ON -> 알람 등록)
            // TODO 데이터를 저장
        }
    }

    private fun initChangeAlarmTimeButton() {
        val changeAlarmButton: Button = findViewById(R.id.changeAlarmTimeButton)
        changeAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            TimePickerDialog(this, { picker, hour, minute ->

                val model = savaAlarmModel(hour, minute, false)
                renderView(model)

                // TODO 기존에 있던 알람 삭제
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false )
                .show()

        }
    }

    private fun savaAlarmModel(
        hour: Int,
        minute: Int,
        onOff: Boolean
    ): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = false
        )

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }


        return model
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정, 예외 처리
//        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
//
//        if ((pendingIntent == null) and alarmModel.onOff) { // 펜딩인텐트는 없는데 알람이 켜져있는 경우
//            alarmModel.onOff = false
//        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
//            pendingIntent.cancel()
//        }

        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) {
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model
        }


    }

    companion object {
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOFF"
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_REQUEST_CODE = 1000
    }

}