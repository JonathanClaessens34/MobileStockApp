package com.pxl.stockify

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.pxl.stockify.databinding.ActivityMainBinding
import com.pxl.stockify.domain.StockListDisplay
import org.json.JSONObject


class FirstFragment : Fragment() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentview: View

    private lateinit var stocksView: RecyclerView
    private lateinit var stockListDisplayList: List<StockListDisplay>
    private lateinit var adapter: RecyclerView.Adapter<StockAdapter.StockViewHolder>
    private lateinit var urls: List<String>
    private lateinit var keys: List<String>
    private lateinit var queue: RequestQueue
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentview = inflater.inflate(R.layout.fragment_first, container, false)

        /*

        mChildEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //var stocksText: TextView = fragmentview.findViewById(R.id.textTestview)
                var stock: Stock =
                    snapshot.getValue(Stock::class.java) as Stock //idk of dit correct is dus als er een fout is ist wss hier
                //stocksText.setText(stocksText.text.toString() + "\n" + stock.name)


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
                TODO("Not yet implemented")
            }


        }


        mDatabaseReference.addChildEventListener(mChildEventListener)
        */

        stocksView = fragmentview.findViewById(R.id.stocksRecV)
        searchView = fragmentview.findViewById(R.id.searchView)
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                searchCompany(s, "06MYTUCAKIJ37T6E")
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        setUrls()

        stockListDisplayList = ArrayList<StockListDisplay>().toMutableList()
        adapter = StockAdapter(this.requireContext(), stockListDisplayList) //requireContext, niet zeker of juist

        stocksView.adapter = adapter
        var stocksLayoutManager: LinearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        stocksView.layoutManager = stocksLayoutManager

        // Instantiate the cache
        val cache = DiskBasedCache(this.requireContext().cacheDir, 1024 * 1024) // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        //queue = OwnRequestQueue(cache, network)
        queue = RequestQueue(cache, network, 1).apply {
            start()
        }

        
        var key : String = "RD9SZ2KDJRY14WE2";
        for (url in urls) {
            makeCall(url, key)
            makeCall2(url, key)
        }

        return fragmentview


    }

    private fun setUrls() {
        //API laat max 5 calls per minuut toe, 500 calls elke dag
        urls = ArrayList<String>().toMutableList()
        urls += "IBM"
        urls += "AAPL"
        urls += "TSLA"
        urls += "ZM"
        urls += "COIN"
        /*urls += "DELL"
        urls += "MMAT"
        urls += "PYPL"*/
    }


    private fun searchCompany(url: String, apiKey: String) {
        //TODO: If nothing is found return already loaded recyclerview
        stockListDisplayList = ArrayList<StockListDisplay>().toMutableList()
        setAdapter(stockListDisplayList)
        val urlcompanyInfoRequest =
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=$url&apikey=$apiKey"
        // Request a string response from the provided URL.
        val companyInfoRequest = StringRequest(Request.Method.GET, urlcompanyInfoRequest,
            { response ->
                var found = JSONObject(response)
                //val jsonMeta: JSONObject = found.getJSONObject("Global Quote")
                val jsonFound = found.getJSONArray("bestMatches")

                for (i in 0 until jsonFound.length()) {
                    var it : JSONObject = jsonFound.get(i) as JSONObject
                    var stockListDisplay = StockListDisplay()
                    stockListDisplay.ticker = it.getString("1. symbol")
                    stockListDisplay.name = it.getString("2. name")
                    //stock.price = jsonMeta.getString("05. price").toDouble()
                    //stock.change = jsonMeta.getString("10. change percent")
                    stockListDisplayList += stockListDisplay
                }
                setAdapter(stockListDisplayList)
            },
            {
                it.printStackTrace()
            })
        // Add the request to the RequestQueue.
        queue.add(companyInfoRequest)
    }

    private fun makeCall(url: String, apiKey: String) {
            val urlRequest =
                "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$url&apikey=$apiKey"
            // Request a string response from the provided URL.
            val stringRequest = StringRequest(Request.Method.GET, urlRequest,
                { response ->
                    var found = JSONObject(response)
                    val jsonMeta: JSONObject = found.getJSONObject("Global Quote")

                    var stockListDisplay = StockListDisplay()
                    stockListDisplay.ticker = jsonMeta.getString("01. symbol")
                    stockListDisplay.price = jsonMeta.getString("05. price").toDouble()
                    stockListDisplay.change = jsonMeta.getString("10. change percent")
                    stockListDisplayList += stockListDisplay
                    setAdapter(stockListDisplayList)
                },
                {
                    it.printStackTrace()
                })
            // Add the request to the RequestQueue.
            queue.add(stringRequest)
    }

    private fun makeCall2(url: String, apiKey: String) {
            val urlcompanyInfoRequest =
                "https://www.alphavantage.co/query?function=OVERVIEW&symbol=$url&apikey=$apiKey"
            // Request a string response from the provided URL.
            val companyInfoRequest = StringRequest(Request.Method.GET, urlcompanyInfoRequest,
                { response ->
                    var found = JSONObject(response)
                    var stock = stockListDisplayList.first { it.ticker == url}
                    stock.name = found.getString("Name")
                    setAdapter(stockListDisplayList)
                },
                {
                    it.printStackTrace()
                })
            // Add the request to the RequestQueue.
            queue.add(companyInfoRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapter(results: List<StockListDisplay>) {
        adapter = StockAdapter(this.requireContext(), results)
        stocksView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /*override fun OnStockClicked(stock: Stock?) {
        startActivity(Intent(this.requireContext(), DetailsActivity::class.java)
            .putExtra("data", stock)) //this@FirstFragment
    }*/
}

