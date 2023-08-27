package com.pxl.stockify

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.pxl.stockify.domain.StockListDisplay

class HoldStockAdapter: RecyclerView.Adapter<HoldStockAdapter.StockViewHolder> {

    lateinit var stockListDisplays: List<StockListDisplay>
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var  mDatabaseReference: DatabaseReference
    private lateinit var mChildEventListener: ChildEventListener



    constructor(){
        FirebaseUtil.openFbReference()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        stockListDisplays = FirebaseUtil.mStockListDisplays

        mChildEventListener = object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var stockListDisplay : StockListDisplay = snapshot.getValue(StockListDisplay::class.java) as StockListDisplay //idk of dit correct is dus als er een fout is ist wss hier
                Log.d("Stock", stockListDisplay.name)
               // stock.id = snapshot.key.toString()
                stockListDisplays += stockListDisplay //mess niet geinitialliseerd
                notifyItemInserted(stockListDisplays.size-1)

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        var context: Context = parent.context
        var itemView: View = LayoutInflater.from(context).inflate(R.layout.holdstock_row, parent, false)

        return StockViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        var stockListDisplay: StockListDisplay = stockListDisplays.get(position)
        holder.bind(stockListDisplay)

    }

    override fun getItemCount(): Int {
        return stockListDisplays.size

    }




    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var stockName: TextView = itemView.findViewById(R.id.stockName) //mess een converter



        fun bind(stockListDisplay: StockListDisplay){
            stockName.setText(stockListDisplay.name)
        }

    }


}