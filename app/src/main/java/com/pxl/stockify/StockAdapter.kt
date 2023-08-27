package com.pxl.stockify

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.pxl.stockify.domain.StockListDisplay

class StockAdapter : RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    lateinit var context: Context
    lateinit var stockListDisplays: List<StockListDisplay>
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mChildEventListener: ChildEventListener

    constructor(context: Context, listStockListDisplays: List<StockListDisplay>) {
        FirebaseUtil.openFbReference()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        stockListDisplays = listStockListDisplays;
        this.context = context

        //stocks += Stock("International Business Machines", "IBM", "€ 3.4", "45");

        /*mChildEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var stock: Stock =
                    snapshot.getValue(Stock::class.java) as Stock //idk of dit correct is dus als er een fout is ist wss hier
                Log.d("Stock", stock.name)
                stock.id = snapshot.key.toString()
                stocks += stock //mess niet geinitialliseerd
                notifyItemInserted(stocks.size - 1)
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
        mDatabaseReference.addChildEventListener(mChildEventListener)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        this.context = parent.context
        var itemView: View = LayoutInflater.from(context).inflate(R.layout.stock_row, parent, false)

        return StockViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        var stockListDisplay: StockListDisplay = stockListDisplays.get(position)
        holder.bind(stockListDisplay)

        holder.itemView.setOnClickListener( object : View.OnClickListener {
            override fun onClick(v: View?) {
                //ga naar save deal gaan
                val activity = v!!.context as AppCompatActivity
                val dealFragment = SecondFragment() //fixe naar dat stock wordt meegegeve


                //framelayout in horizontal mode NIET AANKOMEN DIT IS BELANGERIJK
                var detailFrame: View? = activity.findViewById(R.id.details)
                var bundle: Bundle = Bundle()

                var gson: Gson = Gson()
                var hulp:String = gson.toJson(stockListDisplay)
                bundle.putString("stock", hulp)
                bundle.putString("aaa", "test")
                dealFragment.arguments = bundle;

                if (detailFrame != null) {
                    detailFrame.visibility = View.VISIBLE
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.details, dealFragment).addToBackStack(null).commit()
                }else{
                    //TODO: nog altijd stoem probleem
                    //activity.supportFragmentManager.beginTransaction()
                      //  .replace(R.id.nav_host_fragment, dealFragment).addToBackStack(null).commit()
                    val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(hulp)
                    Navigation.findNavController(v).navigate(action)
                }

            }

        })


    }

    override fun getItemCount(): Int {
        return stockListDisplays.size
    }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stockTicker: TextView = itemView.findViewById(R.id.stockTicker) //mess een converter
        var stockChange: TextView = itemView.findViewById(R.id.stockChange) //mess een converter
        var stockPrice: TextView = itemView.findViewById(R.id.stockPrice) //mess een converter
        var stockName: TextView = itemView.findViewById(R.id.stockName) //mess een converter



        fun bind(stockListDisplay: StockListDisplay) {
            stockName.setText(stockListDisplay.name)
            stockTicker.setText(stockListDisplay.ticker)
            stockChange.setText(stockListDisplay.change)
            stockPrice.setText("€ " + stockListDisplay.price) //Moet even kijken of de api het in € of $ returned
        }



    }
}


