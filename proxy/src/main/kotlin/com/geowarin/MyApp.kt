package com.geowarin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyApp

fun main(args: Array<String>) {
	runApplication<MyApp>(*args)
}
