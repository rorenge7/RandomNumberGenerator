package com.example.ryoto.randomnumbergenerator

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Spinner


class MainActivity : AppCompatActivity(), View.OnClickListener,SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {


    private var alphaTextView: TextView? = null
    private var betaTextView: TextView? = null
    private var minNumTextView: TextView? = null
    private var maxNumTextView: TextView? = null
    private var alphaSeekBar: SeekBar? = null
    private var betaSeekBar: SeekBar? = null
    private var randNumText: TextView? = null
    private var distributionSpinner: Spinner? = null
    private val rand = Random(System.currentTimeMillis())
    private var alphaValue = 0
    private var betaValue = 1
    var format: DateFormat = SimpleDateFormat("yyyy MM dd hh:mm:ss.SSS")
    var sharedPref: SharedPreferences? = null
    var distributionList: Array<String>? = null

    val UNIFORM_DIST_NUM = 0
    val NORMAL_DIST_NUM = 1
    val EXPONENTIAL_DIST_NUM = 2

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_start).setOnClickListener(this)  //リスナーをボタンに登録

        alphaTextView = findViewById(R.id.text_alpha)
        betaTextView = findViewById(R.id.text_beta)

        alphaSeekBar = findViewById<View>(R.id.seekBar_alpha) as SeekBar?
        alphaSeekBar?.setOnSeekBarChangeListener(this)
        betaSeekBar = findViewById<View>(R.id.seekBar_beta) as SeekBar?
        betaSeekBar?.setOnSeekBarChangeListener(this)
        minNumTextView = findViewById(R.id.textView_alphaNum)
        maxNumTextView = findViewById(R.id.textView_betaNum)
        randNumText = findViewById<View>(R.id.textView_randNum) as TextView?
        distributionSpinner = findViewById<View>(R.id.spinner_distribution) as Spinner?
        distributionSpinner!!.onItemSelectedListener = this
        distributionList = resources.getStringArray(R.array.list)

        sharedPref= PreferenceManager.getDefaultSharedPreferences(this)
        alphaValue = sharedPref!!.getInt("min_value",0)
        betaValue = sharedPref!!.getInt("max_value",1)
        textView_alphaNum.text = alphaValue.toString()
        textView_betaNum.text = betaValue.toString()
        alphaSeekBar?.progress = alphaValue
        betaSeekBar?.progress = betaValue
    }

    override fun onClick(view: View) {
        //ここに遷移するための処理を追加する
        var randNum = ""
        if (view != null) {
            when(view.id){
                R.id.button_start-> {
//                    val intent = Intent(this, SampleActivity::class.java)
//                    startActivity(intent)
                    alphaValue = alphaSeekBar?.progress!!
                    betaValue = betaSeekBar?.progress!!

                    val prefEditor = sharedPref?.edit()
                    when(distributionSpinner!!.selectedItem){
                        "一様分布"->{
                            randNum = uniformDistribution(alphaValue,betaValue)
                            prefEditor?.putInt("min_value",alphaValue)
                            prefEditor?.putInt("max_value",betaValue)

                        }
                        "正規分布"->{
                            randNum = normalDistribution(mean = alphaValue.toDouble(), variance = betaValue.toDouble())
                            prefEditor?.putInt("mean_value",alphaValue)
                            prefEditor?.putInt("variance_value",betaValue)

                        }
                        "指数分布"->{
                            randNum = exponentialDistribution(lambda = alphaValue.toDouble())
                            prefEditor?.putInt("lambda_value",alphaValue)

                        }
                    }
                    prefEditor?.commit()

                    textView_randNum.text = randNum

                    textView_time.text = format.format(Date())
                }
            }
        }
    }
    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        // called when progress is changed

        if(seekBar != null){
            when(seekBar.id){
                R.id.seekBar_alpha->{
                    textView_alphaNum.text = progress.toString()
                }
                R.id.seekBar_beta->{
                    textView_betaNum.text = progress.toString()
                }
            }
        }
    }
    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is started
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is stopped
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
            UNIFORM_DIST_NUM->{
                alphaValue = sharedPref!!.getInt("min_value",0)
                betaValue = sharedPref!!.getInt("max_value",1)
                alphaTextView?.text = "min"
                betaTextView?.text = "max"
            }
            NORMAL_DIST_NUM->{
                alphaValue = sharedPref!!.getInt("mean_value",0)
                betaValue = sharedPref!!.getInt("variance_value",1)
                alphaTextView?.text = "mean"
                betaTextView?.text = "variance"
            }
            EXPONENTIAL_DIST_NUM->{
                alphaValue = sharedPref!!.getInt("lambda_value",1)
                alphaTextView?.text = "lambda"
                betaTextView?.text = "unnecessary"
            }
        }
        seekBar_alpha.progress = alphaValue
        seekBar_beta.progress = betaValue
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun uniformDistribution(localMinValue: Int, localMaxValue: Int): String {
        var a = localMinValue
        var b = localMaxValue
        if(a == b){
            return a.toString()
        }else{
            if(a > b){
                var tmp = a
                a = b
                b = tmp
            }
            return (rand.nextInt(b - a + 1) + a).toString()
        }
    }

    private fun normalDistribution(mean: Double = 0.0, variance : Double = 1.0): String {
//        ボックス=ミュラー法を使用
        return (Math.sqrt(-2.0 * Math.log(rand.nextDouble())) * Math.cos(2.0 * Math.PI * rand.nextDouble()) * Math.sqrt(variance) + mean ).toString()
    }

    private fun exponentialDistribution(lambda: Double = 1.0): String{
//        if(lambda == 0.0)return "ERROR"
        return (- Math.log(rand.nextDouble())/lambda).toString()
    }
}


//private fun View.setOnSeekBarChangeListener(mainActivity: MainActivity) {}
