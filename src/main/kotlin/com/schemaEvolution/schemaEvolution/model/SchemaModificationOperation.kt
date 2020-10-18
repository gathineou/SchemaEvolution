package com.schemaEvolution.schemaEvolution.model

data class SchemaModificationOperationWrapper(
        val operations: List<SchemaModificationOperation> = emptyList() ,
        val sourceSchema: String,
        val destinationSchema: String
)

abstract class SchemaModificationOperation (
        open val type: SchemaModificationOperationType
)

data class AddTableOperation(
        val table: SchemaTable,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.ADD_TABLE
) : SchemaModificationOperation(type)

data class RemoveTableOperation(
        val table: SchemaTable,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.REMOVE_TABLE
) : SchemaModificationOperation(type)

data class RemoveColumnOperation(
        val column: Column,
        val table: String,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.REMOVE_COLUMN
): SchemaModificationOperation(type)

data class AddColumnOperation(
        val column: Column,
        val table: String,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.ADD_COLUMN
): SchemaModificationOperation(type)

data class AlterColumnType(
        val oldColumn: Column,
        val newColumn: Column,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.ALTER_COLUMN_TYPE
): SchemaModificationOperation(type)


data class AddConstraintOperation(
        val constraint: Constraint,
        val column: Column,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.ADD_CONSTRAINT
): SchemaModificationOperation(SchemaModificationOperationType.ADD_CONSTRAINT)

data class RemoveConstraintOperation(
        val constraint: Constraint,
        val column: Column,
        override val type: SchemaModificationOperationType = SchemaModificationOperationType.REMOVE_CONSTRAINT
): SchemaModificationOperation(SchemaModificationOperationType.REMOVE_CONSTRAINT)

enum class SchemaModificationOperationType(value: String){
    ADD_TABLE("ADD TABLE"),
    REMOVE_TABLE("REMOVE TABLE"),
    ADD_COLUMN("ADD COLUMN"),
    REMOVE_COLUMN("REMOVE COLUMN"),
    ALTER_COLUMN_TYPE("ALTER_COLUMN TYPE"),
    ADD_CONSTRAINT("ADD CONSTRAINT"),
    REMOVE_CONSTRAINT("REMOVE CONSTRAINT"),
}
