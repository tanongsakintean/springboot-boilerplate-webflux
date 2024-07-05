package com.springboot.boilerplate_webflux.util


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

object JSONUtils {

    private val gson = Gson()

    fun isJSONValid(jsonInString: String?): Boolean {
        return try {
            gson.fromJson(jsonInString, Any::class.java)
            true
        } catch (ex: JsonSyntaxException) {
            false
        }
    }

    fun convertToListString(str: String?): List<String?> {
        return try {
            gson.fromJson(str, List::class.java) as List<String>
        } catch (ex: JsonSyntaxException) {
            arrayListOf()
        }
    }

    fun covertToJson(t: Any?) : String? {
        return gson.toJson(t)
    }
}