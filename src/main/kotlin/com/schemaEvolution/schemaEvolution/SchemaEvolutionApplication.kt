package com.schemaEvolution.schemaEvolution

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchemaEvolutionApplication

fun main(args: Array<String>) {
	runApplication<SchemaEvolutionApplication>(*args)
}
