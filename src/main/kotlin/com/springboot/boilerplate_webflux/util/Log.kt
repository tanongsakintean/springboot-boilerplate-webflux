package com.springboot.boilerplate_webflux.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Log {
	val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}
