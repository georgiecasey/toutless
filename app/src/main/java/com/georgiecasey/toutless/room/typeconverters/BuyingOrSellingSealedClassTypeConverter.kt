package com.georgiecasey.toutless.room.typeconverters

import androidx.room.TypeConverter
import com.georgiecasey.toutless.ui.BuyingOrSellingField

class BuyingOrSellingSealedClassTypeConverter {
    @TypeConverter
    fun fromString(buyingOrSelling: String?): BuyingOrSellingField? {
        if (buyingOrSelling == BuyingOrSellingField.Buying.roomString) {
            return BuyingOrSellingField.Buying
        } else if (buyingOrSelling == BuyingOrSellingField.Selling.roomString) {
            return BuyingOrSellingField.Selling
        } else {
            return BuyingOrSellingField.Buying
        }
    }

    @TypeConverter
    fun sealedClassToString(buyingOrSelling: BuyingOrSellingField?): String? {
        return buyingOrSelling?.roomString
    }
}