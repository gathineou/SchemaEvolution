package com.schemaEvolution.schemaEvolution.model

import java.time.LocalDate

data class Schema(
        val name: String,
        val schemaTables: MutableList<SchemaTable> = mutableListOf(),
        val date: LocalDate,
        val author: String?,
        val commit: String?
)

data class SchemaTable (
        val tableName: String,
        val dbSchema: String,
        val columns: List<Column>
)

data class Column(
        val columnName: String,
        val tableName: String,
        val type: String,
        val defaultValue: String?,
        val constraints : MutableList<Constraint>
)

data class Constraint(
        val name: String? = null,
        val type: ConstraintType,
        val fkDestinationTable: String? = null,
        val fkDestinationColumn: String? = null
)

enum class ConstraintType(value: String){
    FOREIGN_KEY("FOREIGN KEY"),
    PRIMARY_KEY("PRIMARY KEY"),
    NOT_NULL("NOT NULL")
}
