package com.iam_client.repostories.data

import java.io.Serializable

class Address (
    val id : String? = null  ,
    var country : String ,
    var city : String,
    var block : String,
    var street : String,
    var numAtStreet : String,
    var apartment : String ,
    var zipCode : String
): Serializable
{
    fun toStringFormat(format:String="c  S ,ns a"):String {
        var formatTemp = format
        arrayOf("c","C","B","S","ns","a","z").forEach {
            formatTemp = formatTemp.replace(it,"@$it@")
        }
        return formatTemp
            .replace("@C@", country)
            .replace("@c@", city)
            .replace("@B@", block)
            .replace("@S@", street)
            .replace("@ns@", numAtStreet)
            .replace("@a@", apartment)
            .replace("@z@", zipCode)
    }
}