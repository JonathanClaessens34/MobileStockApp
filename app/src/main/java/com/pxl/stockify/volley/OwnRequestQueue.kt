package com.pxl.stockify.volley

import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.ResponseDelivery

class OwnRequestQueue : RequestQueue {
    constructor(
        cache: Cache?,
        network: Network?,
        threadPoolSize: Int,
        delivery: ResponseDelivery?
    ) : super(cache, network, 1, delivery) {
    }

    constructor(cache: Cache?, network: Network?, threadPoolSize: Int) : super(cache, network, 1) {}
    constructor(cache: Cache?, network: Network?) : super(cache, network, 1) {}
}