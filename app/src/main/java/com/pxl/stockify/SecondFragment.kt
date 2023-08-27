package com.pxl.stockify

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.pxl.stockify.domain.Company
import com.pxl.stockify.domain.DailyPrice
import com.pxl.stockify.domain.StockListDisplay
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


private const val ARG_PARAM1 = "stock"
private const val ARG_PARAM2 = "aaa"


class SecondFragment : Fragment() {

    val args: SecondFragmentArgs by navArgs()

    private var stockListDisplay: StockListDisplay? = null

    private lateinit var fragmentview: View
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var  mDatabaseReference: DatabaseReference

    lateinit var nameLbl: TextView
    lateinit var valueLbl: TextView
    lateinit var tickerLbl: TextView
    lateinit var changeLbl: TextView

    //te verwijderen wnr testbaar
    private lateinit var txtName : EditText
    private lateinit var txtAmount : EditText

    private lateinit var test:String

    private lateinit var chart: CandleStickChart
    private lateinit var stockJson: JSONObject
    private lateinit var company: Company
    private lateinit var queue: RequestQueue

    //TODO:functioneel 100% nog maken en landscape layout toevoegen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            test = it.getString(ARG_PARAM2).toString()
            var gson: Gson = Gson()
            stockListDisplay = gson.fromJson(it.getString(ARG_PARAM1).toString(), StockListDisplay::class.java)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentview = inflater.inflate(R.layout.fragment_second, container, false)

        // Instantiate the cache
        val cache = DiskBasedCache(this.requireContext().cacheDir, 1024 * 1024) // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        //queue = OwnRequestQueue(cache, network)
        queue = RequestQueue(cache, network, 1).apply {
            start()
        }

        makeCall("IBM", "RD9SZ2KDJRY14WE2")


        //Database settings
        FirebaseUtil.openFbReference()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        nameLbl = fragmentview.findViewById(R.id.stockName_lbl)
        valueLbl = fragmentview.findViewById(R.id.stockValue_lbl)
        tickerLbl = fragmentview.findViewById(R.id.stockTicker_lbl)
        changeLbl = fragmentview.findViewById(R.id.stockChange_lbl)

        txtAmount =  fragmentview.findViewById(R.id.editAmount)


        var stockString = ""
        try {
            stockString = args.stockAsJsonString.toString()
        } catch (e: Exception) {


        }

        if (stockString != ""){
            var gson: Gson = Gson()
            stockListDisplay = gson.fromJson(stockString, StockListDisplay::class.java)
        }

        if (stockListDisplay != null){
            if (stockListDisplay!!.change[0] == '-'){
                changeLbl.setTextColor(Color.parseColor("#ba0909"))
            }else{
                changeLbl.setTextColor(Color.parseColor("#069c2e"))
            }
            changeLbl.setText(stockListDisplay!!.change)
            tickerLbl.setText(stockListDisplay!!.ticker)
            nameLbl.setText(stockListDisplay!!.name) // dees nog aanpassen
            valueLbl.setText( "${stockListDisplay!!.price} € / stock")

        }

        //inflater.inflate(R.menu.save_menu, menu);
        var saveButton : Button = fragmentview.findViewById(R.id.button)
        saveButton.setOnClickListener {
            saveDeal()
            Toast.makeText(context, "deal saved", Toast.LENGTH_SHORT).show()
            clean()
        }
        return fragmentview
    }

    fun saveDeal(){

        if (stockListDisplay != null){
            val aantal: Int
            var i: Int = 0
            //nu me loop ma maybe in var zette da moette we nog zien
            if(txtAmount.text.toString() == ""){
                aantal = 1
            }else {
                aantal = txtAmount.text.toString().toInt()
            }
            while (i < aantal){
                mDatabaseReference.push().setValue(stockListDisplay)
                i++
            }


        }else{
            //veranderen naar lable wnr testbaar
            var title: String = "no stock"
            var price: String = "0.1"
            var hulpStockListDisplay: StockListDisplay = StockListDisplay((title))
            hulpStockListDisplay.price = price.toDouble()
            mDatabaseReference.push().setValue(hulpStockListDisplay)
        }


    }

    fun clean(){
        //txtName.setText("");
        //txtPrice.setText("");
    }

    //kinda wel nu
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stockListDisplay Parameter 1.
         * @return A new instance of fragment thirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(stockListDisplay: StockListDisplay) =
            thirdFragment().apply {
                arguments = Bundle().apply {
                    putStock(ARG_PARAM1, stockListDisplay)
                }
            }
        //idk of dit werkt
        private fun putStock(argParam1: String, stockListDisplay: StockListDisplay) {
            throw RuntimeException("Stub!")
        }
    }

    private fun makeCall(url: String, apiKey: String) {
        val urlRequest =
            "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=5min&symbol=$url&apikey=$apiKey"
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, urlRequest,
            { response ->
                var found = JSONObject(response)
                /*val jsonMeta: JSONObject = found.getJSONObject("Global Quote")

                var stockListDisplay = StockListDisplay()
                stockListDisplay.ticker = jsonMeta.getString("01. symbol")
                stockListDisplay.price = jsonMeta.getString("05. price").toDouble()
                stockListDisplay.change = jsonMeta.getString("10. change percent")*/

                val jsonMeta: JSONObject = found.getJSONObject("Time Series (5min)")

                val fiveMinutesEntries = ArrayList<DailyPrice>()
                for (key in jsonMeta.keys()) {
                    val stockTime = key
                    val value: JSONObject = jsonMeta[key] as JSONObject
                    val temp = value["1. open"]
                    val open = value.getDouble("1. open")
                    val high = value.getDouble("2. high")
                    val low = value.getDouble("3. low")
                    val close = value.getDouble("4. close")
                    val volume = value.getInt("5. volume")
                    fiveMinutesEntries.add(DailyPrice(stockTime, open, high, low, close, volume))
                }
                company = Company(url, fiveMinutesEntries)
                setGraphData()
            },
            {
                it.printStackTrace()
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun setGraphData() {
        chart = fragmentview.findViewById(R.id.stockCandleStickChart)

        queue.start()
        //Added
        val arrayIndexName = arrayOf("S&P/ASX 200 Index", "S&P/ASX 50 Index")
        val index: Company = company

        chart.setHighlightPerDragEnabled(true)
        chart.setDrawBorders(true)
        chart.setBorderColor(Color.LTGRAY)

        val yAxis: YAxis = chart.getAxisLeft()
        val rightAxis: YAxis = chart.getAxisRight()
        yAxis.setDrawGridLines(true)
        rightAxis.setDrawGridLines(true)
        chart.requestDisallowInterceptTouchEvent(true)

        val xAxis: XAxis = chart.getXAxis()

        xAxis.setDrawGridLines(true) // disable x axis grid lines

        xAxis.setDrawLabels(true)
        rightAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val l: Legend = chart.getLegend()
        l.setEnabled(true)

        val candleValues: ArrayList<CandleEntry> = ArrayList()

        val dateIndex = arrayOfNulls<String>(index.companyStockPrices.size)
        try {
            for (j in index.companyStockPrices.indices) {
                //System.out.println((float)index.getCompanyStockPrices().get(j).getDailyHigh());
                if (index.companyStockPrices[j].dailyClose != 0.0) {
                    dateIndex[j] = index.companyStockPrices[j].dailyDate.toString()
                    candleValues.add(
                        CandleEntry(
                            j.toFloat() * 1f,
                            index.companyStockPrices[j].dailyHigh.toFloat() * 1f,
                            index.companyStockPrices[j].dailyLow.toFloat() * 1f,
                            index.companyStockPrices[j].dailyOpen.toFloat() * 1f,
                            index.companyStockPrices[j].dailyClose.toFloat() * 1f
                        )
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }

        val indexAxisValueFormatter = IndexAxisValueFormatter(dateIndex)
        xAxis.valueFormatter = indexAxisValueFormatter
        xAxis.labelCount = 4

        //System.out.println(candleValues.toString());

        //System.out.println(candleValues.toString());
        val set1 = CandleDataSet(candleValues, "Stock Prices")
        set1.color = Color.rgb(80, 80, 80)
        set1.shadowColor = Color.GRAY
        set1.shadowWidth = 0.8f
        set1.decreasingColor = Color.RED
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.GREEN
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = Color.LTGRAY
        set1.setDrawValues(false)

        val description = Description()
        description.setText(arrayIndexName[0]) //TODO: make correct

        val data = CandleData(set1)
        chart.setDescription(description)
        chart.setData(data)
        chart.notifyDataSetChanged()
        chart.invalidate()
        refreshFragment()
    }

    fun refreshFragment() {
        fragmentManager?.beginTransaction()
            ?.detach(this)
            ?.attach(this)
            ?.commit()
    }
}

