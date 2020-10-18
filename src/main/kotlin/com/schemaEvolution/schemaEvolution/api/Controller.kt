package com.schemaEvolution.schemaEvolution.api

import com.schemaEvolution.schemaEvolution.model.Schema
import com.schemaEvolution.schemaEvolution.model.SchemaModificationOperationWrapper
import com.schemaEvolution.schemaEvolution.service.SchemaLoader
import com.schemaEvolution.schemaEvolution.service.SchemaModificationOperationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@Controller
class Controller(
        private val schemaLoader: SchemaLoader
) {
    @GetMapping(value = ["dashboard"])
    fun dashBoard(model: Model): String {
        val schemas = schemaLoader.loadSchemas()
        model.addAttribute("schemas", schemas)
        return "index2"
    }
}

@RestController
class RestController(
        private val schemaLoader: SchemaLoader,
        private val smoService: SchemaModificationOperationService
){
    @PostMapping(value = ["data"])
    fun data(@RequestBody dataRequest: DataRequest): DataResponse {
        println(dataRequest)
        val allSchemas = schemaLoader.loadSchemas()
        val sourceSchema = allSchemas.find { it.name == dataRequest.source }!!
        val destinationSchema = allSchemas.find { it.name == dataRequest.destination }!!
        val schemas = listOf(sourceSchema, destinationSchema)
        val operationsWrapper = smoService.getOperations(sourceSchema!!, destinationSchema!!)
        println(operationsWrapper)
        operationsWrapper.operations.forEach { println(it) }
        val dataResponse = DataResponse(
                operationsWrapperList = listOf(operationsWrapper),
                schemas = schemas
        )
//        model.addAttribute("dataImports", dataImports)
        return dataResponse
    }
}

data class DataResponse(
        val operationsWrapperList: List<SchemaModificationOperationWrapper>,
        val schemas: List<Schema>

        )
data class DataRequest(
        val source: String,
        val destination: String

)


@Configuration
class MyConfiguration {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            }
        }
    }
}