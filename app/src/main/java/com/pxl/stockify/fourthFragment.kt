package com.pxl.stockify

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.pxl.stockify.domain.StockListDisplay
import org.json.JSONObject
import java.lang.Math.round

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fourthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class fourthFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentview: View
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var  mDatabaseReference: DatabaseReference
    private lateinit var  mChildEventListener: ChildEventListener
    private lateinit var lblTotalValue: TextView
    private lateinit var lblPercentage: TextView
    private var valueWhenBought: Double = 0.0
    private var valueNow: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //TODO: Extra detail in de adapter/ apparte adapter.
        //TODO: winst/verlies berekenen

        // Inflate the layout for this fragment
        fragmentview = inflater.inflate(R.layout.fragment_fourth, container, false)

        //Database settings
        FirebaseUtil.openFbReference()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference


        mChildEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //var stocksText: TextView = fragmentview.findViewById(R.id.textTestview)
                var stockListDisplay: StockListDisplay =
                    snapshot.getValue(StockListDisplay::class.java) as StockListDisplay //idk of dit correct is dus als er een fout is ist wss hier
                //stocksText.setText(stocksText.text.toString() + "\n" + stock.name)
                getTotalValueData(stockListDisplay)


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")

            }


        }


        lblTotalValue = fragmentview.findViewById(R.id.totalValue_lbl)
        lblPercentage = fragmentview.findViewById(R.id.winst_loss_lbl)


        mDatabaseReference.addChildEventListener(mChildEventListener)


        var stocksView: RecyclerView = fragmentview.findViewById(R.id.boughtStocks_rv)
        val adapter: HoldStockAdapter = HoldStockAdapter()  //was STockAdapter checken als nog werkt
        stocksView.adapter = adapter
        var stocksLayoutManager: LinearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        stocksView.layoutManager = stocksLayoutManager


        return fragmentview
    }

    fun getTotalValueData(stockListDisplayVar: StockListDisplay){
        valueWhenBought += stockListDisplayVar.price
        valueNow += 1.0//getValueNow(stockVar.ticker)

        var hulp: Double  =  round((1.0 - (valueNow / valueWhenBought)) * 10000.0)/100.0



        //totale waarden een nr geven?
        lblTotalValue.setText(valueNow.toString())

        if((valueWhenBought / valueNow) < 1){
            lblPercentage.setTextColor(Color.parseColor("#069c2e"))
            lblPercentage.setText("+" + hulp.toString() + "%")
        }else{
            lblPercentage.setTextColor(Color.parseColor("#ba0909"))
            lblPercentage.setText("-" + hulp.toString() + "%")

        }
    }

    fun getValueNow(ticker: String): Double{

        var hulp: Double  = 1.0
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this.context)

        val urlcompanyInfoRequest = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + ticker + "&apikey=RD9SZ2KDJRY14WE2"
        // Request a string response from the provided URL.
        val companyInfoRequest = StringRequest(
            Request.Method.GET, urlcompanyInfoRequest,
            { response ->
                // Display the first 500 characters of the response string.
                //textView.text = "Response is: ${response.substring(0, 500)}"
                var found = JSONObject(response)
                //val jsonMeta: JSONObject = found.getJSONObject("Meta Data")
                //var stock = stockList.find {x -> x.ticker == url}
                hulp = found.getString("05. price").toDouble()
            },
            {
                it.printStackTrace()
            })
        // Add the request to the RequestQueue.
        queue.add(companyInfoRequest)

        return hulp
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fourthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fourthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}