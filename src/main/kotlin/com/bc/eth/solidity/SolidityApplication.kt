package com.bc.eth.solidity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SolidityApplication

fun main(args: Array<String>) {
	runApplication<SolidityApplication>(*args)
}
