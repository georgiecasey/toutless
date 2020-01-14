package com.georgiecasey.toutless.ui

sealed class BuyingOrSellingField(val roomString: String) {
    object Buying : BuyingOrSellingField("buying")
    object Selling : BuyingOrSellingField("selling")
}