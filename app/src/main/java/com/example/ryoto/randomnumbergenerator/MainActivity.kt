package com.example.ryoto.randomnumbergenerator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.R.id.edit




class MainActivity : AppCompatActivity(), View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    private var minTextView: TextView? = null
    private var maxTextView: TextView? = null
    private var minSeekBar: SeekBar? = null
    private var maxSeekBar: SeekBar? = null
    private var randNumText: TextView? = null
    private val rand = Random(System.currentTimeMillis());
    private var minValue = 0
    private var maxValue = 1
    var format: DateFormat = SimpleDateFormat("yyyy MM dd hh:mm:ss.SSS")
    var sharedPref: SharedPreferences? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_start).setOnClickListener(this)  //リスナーをボタンに登録
        minSeekBar = findViewById<View>(R.id.seekBar_min) as SeekBar?
        minSeekBar?.setOnSeekBarChangeListener(this)
        maxSeekBar = findViewById<View>(R.id.seekBar_max) as SeekBar?
        maxSeekBar?.setOnSeekBarChangeListener(this)
        minTextView = findViewById(R.id.textView_minNum)
        maxTextView = findViewById(R.id.textView_maxNum)
        randNumText = findViewById<View>(R.id.textView_randNum) as TextView?

        sharedPref= PreferenceManager.getDefaultSharedPreferences(this)
        minValue = sharedPref!!.getInt("min_value",0)
        maxValue = sharedPref!!.getInt("max_value",1)
        textView_minNum.setText(minValue.toString())
        textView_maxNum.setText(maxValue.toString())
        minSeekBar?.setProgress( minValue )
        maxSeekBar?.setProgress( maxValue )

    }

    override fun onClick(view: View) {
        //ここに遷移するための処理を追加する
        if (view != null) {
            when(view.id){
                R.id.button_start-> {
//                    val intent = Intent(this, SampleActivity::class.java)
//                    startActivity(intent)
                    minValue = minSeekBar?.progress!!
                    maxValue = maxSeekBar?.progress!!

                    if(minValue == maxValue){
                        maxValue+=1
                        textView_randNum.setText(minValue.toString())
                    }else{
                        if(minValue > maxValue){
                            var tmp = minValue
                            minValue = maxValue
                            maxValue = tmp
                        }
                        textView_randNum.setText((rand.nextInt(maxValue - minValue +1) + minValue).toString())
                    }
                    textView_time.setText(format.format(Date()))
                    writePreference()
                }
            }
        }
    }
    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        println("TEST")
        if(seekBar != null){
            when(seekBar.id){
                R.id.seekBar_min->{
                    println("TEST2")
                    textView_minNum.setText(progress.toString())
                    println("TEST2")
                }
                R.id.seekBar_max->{
                    textView_maxNum.setText(progress.toString())
                }
            }
        }
        println("TEST")
        // called when progress is changed
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is started
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is stopped
    }
    private fun writePreference(){
        val prefEditor = sharedPref?.edit()
        prefEditor?.putInt("min_value",minValue)
        prefEditor?.putInt("max_value",maxValue)
        prefEditor?.commit()
    }
}

//private fun View.setOnSeekBarChangeListener(mainActivity: MainActivity) {}
