package com.springboot.boilerplate_webflux.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

object Json {

	private val gson = GsonBuilder().create()
	fun isJsonValid(str: String?) : Boolean {
		return try {
			gson.fromJson(str, Any::class.java)
			true
		} catch (e: JsonSyntaxException) {
			false
		}
	}
}
