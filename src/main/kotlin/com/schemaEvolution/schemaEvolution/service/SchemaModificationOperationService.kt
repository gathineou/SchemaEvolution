package com.schemaEvolution.schemaEvolution.service

import com.schemaEvolution.schemaEvolution.model.*
import org.springframework.stereotype.Service

@Service
class SchemaModificationOperationService {
    fun getOperations(source: Schema, destination: Schema): SchemaModificationOperationWrapper {
        if (source.date > destination.date){
            throw IllegalArgumentException("""
                Source schema can not be older than the destination one.
                Source schema date: ${source.date}
                Destination schema date: ${destination.date}
            """.trimIndent())
        }
        val operations = createSchemaModificationOperations(source, destination)
        return SchemaModificationOperationWrapper(
                sourceSchema = source.name,
                destinationSchema = destination.name,
                operations = operations
        )
    }

    private fun createSchemaModificationOperations(source: Schema, destination: Schema): List<SchemaModificationOperation> {
        val operations = mutableListOf<SchemaModificationOperation>()
        source.schemaTables.forEach {sourceTable ->
            val destinationTable = destination.schemaTables.find { it.tableName == sourceTable.tableName }
            if(destinationTable == null){
                val operation = RemoveTableOperation(sourceTable)
                operations.add(operation)
                return@forEach
            }
            else{
                val columnOperations = createOperationsFromColumns(
                        sourceTable,
                        destinationTable
                )
                operations.addAll(columnOperations)
            }
        }
        destination.schemaTables.forEach {destinationTable ->
            val sourceTable = source.schemaTables.find { it.tableName == destinationTable.tableName }
            if(sourceTable == null){
                val operation = AddTableOperation(destinationTable)
                operations.add(operation)
                return@forEach
            }
        }
        return operations
    }

    private fun createOperationsFromColumns(sourceTable: SchemaTable, destinationTable: SchemaTable): List<SchemaModificationOperation> {
        val operations = mutableListOf<SchemaModificationOperation>()
        sourceTable.columns.forEach { sourceColumn ->
            val destinationColumn = destinationTable.columns.find { it.columnName == sourceColumn.columnName }
            if(destinationColumn == null){
                val operation = RemoveColumnOperation(sourceColumn,sourceTable.tableName)
                operations.add(operation)
                return@forEach
            }
            else {
                if(destinationColumn.type != sourceColumn.type){
                    val operation = AlterColumnType(sourceColumn, destinationColumn)
                    operations.add(operation)
                    return@forEach
                }

                val constraintOperations = createOperationsFromConstraints(sourceColumn, destinationColumn)
                operations.addAll(constraintOperations)
            }
        }

        destinationTable.columns.forEach { destinationColumn ->
            val sourceColumn = sourceTable.columns.find { it.columnName == destinationColumn.columnName }
            if (sourceColumn == null) {
                val operation = AddColumnOperation(destinationColumn, destinationTable.tableName)
                operations.add(operation)
                return@forEach
            }
        }

        return operations
    }

    private fun createOperationsFromConstraints(sourceColumn: Column, destinationColumn: Column): List<SchemaModificationOperation>{
        val operations = mutableListOf<SchemaModificationOperation>()
        sourceColumn.constraints.forEach { sourceConstraint ->
            val destinationConstraint = destinationColumn.constraints.find { it.type == sourceConstraint.type && it.name == sourceConstraint.name}
            if(destinationConstraint == null){
                val operation = RemoveConstraintOperation(sourceConstraint, sourceColumn)
                operations.add(operation)
                return@forEach
            }
        }

        destinationColumn.constraints.forEach { destinationConstraint ->
            val sourceConstraint = sourceColumn.constraints.find { it.type == destinationConstraint.type && it.name == destinationConstraint.name}
            if (sourceConstraint == null) {
                val operation = AddConstraintOperation(destinationConstraint, destinationColumn)
                operations.add(operation)
                return@forEach
            }
        }

        return operations
    }
}
