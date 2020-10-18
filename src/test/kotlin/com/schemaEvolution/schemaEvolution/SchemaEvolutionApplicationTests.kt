package com.schemaEvolution.schemaEvolution

import com.schemaEvolution.schemaEvolution.model.AddConstraintOperation
import com.schemaEvolution.schemaEvolution.model.Constraint
import com.schemaEvolution.schemaEvolution.model.ConstraintType
import com.schemaEvolution.schemaEvolution.service.SchemaLoader
import com.schemaEvolution.schemaEvolution.service.SchemaModificationOperationService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
class SchemaEvolutionApplicationTests @Autowired constructor(
        private val schemaLoader: SchemaLoader,
        private val smoService: SchemaModificationOperationService
) {

    @Test
    fun `SmoService getOperations should work and identify a list of operations from a source to a destination schema`() {
        File("schemas").walk().forEach {
            println(it)
        }
        val schemas = schemaLoader.loadSchemas()
        val operationsWrapper = smoService.getOperations(schemas[1], schemas[0])
        operationsWrapper.operations.forEach { println(it) }
    }

    @Test
    fun `SmoService getOperations should identify constraint operations`() {
        val schemas = schemaLoader.loadSchemas()

        val newConstraint = Constraint(name = "test_primary_key_constraint", type = ConstraintType.PRIMARY_KEY)

        schemas[0].schemaTables[0].columns[0].constraints.add(newConstraint)
        val operationsWrapper = smoService.getOperations(schemas[1], schemas[0])

        val result = operationsWrapper.operations.find {
            it == AddConstraintOperation(
                    constraint = newConstraint,
                    column = schemas[0].schemaTables[0].columns[0])
        }
        println(result)
        Assertions.assertThat(result).isNotNull
    }
}
