package com.springboot.boilerplate_webflux.models.dto

data class ApiDTO(
    var serviceName: String?,
    var retries: Int?
) {
    constructor() : this(null, null)
}

