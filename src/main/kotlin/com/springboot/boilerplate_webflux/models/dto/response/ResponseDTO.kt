package com.springboot.boilerplate_webflux.models.dto.response

data class ResponseDTO (
	var status: Int?,
	var responseTime: String?,
	var headers: Map<String, Any?>?,
	var body: Any?,
	var exception: String?,
	var traceid: String?
) {
	constructor() : this(null, null, null, null, null,null)
}
