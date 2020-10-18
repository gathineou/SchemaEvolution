package com.schemaEvolution.schemaEvolution.service

import com.schemaEvolution.schemaEvolution.model.*
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.alter.Alter
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex
import net.sf.jsqlparser.statement.create.table.NamedConstraint
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.logging.Logger

@Service
class SchemaConstructor {
    private val logger = Logger.getLogger(SchemaConstructor::class.toString())


    fun constructSchema(sqlCommands: List<String>, fileName: String): Schema{

        var schema = Schema(
                name = fileName,
                // todo: filename parsing to be changed
                date = LocalDate.parse(fileName.split("_")[1]),
                author = fileName.split("_")[2],
                commit = fileName.split("_")[3]
        )

        sqlCommands.forEach {
            val statement = CCJSqlParserUtil.parse(it)
            schema = addStatementToSchema(schema, statement)
        }
        return schema
    }

    private fun addStatementToSchema(schema: Schema, statement: Statement?): Schema {
        if(statement is CreateTable){
            val schemaTable = createTable(statement)
            schema.schemaTables.add(schemaTable)
        }
        else if(statement is Alter){
            val table = schema.schemaTables.find { it.tableName == statement.table.name }
            if(table == null) {
                logger.warning("table does not exist ${statement.table.name}")
                return schema
            }
            alterTable(table, statement)
        }
        return schema
    }

    private fun alterTable(table: SchemaTable, statement: Alter) {
        statement.alterExpressions.forEach { expression ->
            val index = expression.index
            if(index is NamedConstraint){
                if (index.type == "PRIMARY KEY"){
                    val column = table.columns.find { it.columnName ==  index.columnsNames.first()}
                    column?.constraints?.add(Constraint(
                            name = index.name,
                            type = ConstraintType.PRIMARY_KEY
                    ))
                }
                if(index is ForeignKeyIndex){
                    val column = table.columns.find { it.columnName ==  index.columnsNames.first()}
                    column?.constraints?.add(Constraint(
                            name = index.name,
                            type = ConstraintType.FOREIGN_KEY,
                            fkDestinationTable = index.table.name,
                            fkDestinationColumn = index.referencedColumnNames.first()
                    ))
                }
            }

        }
    }

    private fun createTable(statement: CreateTable): SchemaTable {
        val columns = createColumns(statement)
        return SchemaTable(
                tableName = statement.table.name,
                dbSchema = statement.table.schemaName,
                columns = columns
        )
    }

    private fun createColumns(statement: CreateTable): List<Column> {
        val columns = mutableListOf<Column>()
        statement.columnDefinitions.forEach {
            val defaultValue = createDefaultValueFromColumnSpecs(it.columnSpecs)
            val constraints = createConstraintsFromColumnSpecs(it.columnSpecs, it.columnName)
            columns.add(Column(
                    columnName=it.columnName,
                    type = it.colDataType.toString(),
                    defaultValue = defaultValue,
                    constraints = constraints,
                    tableName = statement.table.name
            ))
        }
        return columns
    }

    private fun createDefaultValueFromColumnSpecs(columnSpecs: List<String>?): String? {
        val indexOfDefaultValue = columnSpecs?.indexOf("DEFAULT")
        if(indexOfDefaultValue==-1){
            return null
        }
        return indexOfDefaultValue?.let { columnSpecs[it+1] }
    }

    private fun createConstraintsFromColumnSpecs(columnSpecs: List<String>?, columnName:String): MutableList<Constraint> {
        val constraints = mutableListOf<Constraint>()
        val columnSpecsString = columnSpecs?.joinToString(" ")?: return mutableListOf()
        if(columnSpecsString.contains("NOT NULL")){
            constraints.add(Constraint(
                    type = ConstraintType.NOT_NULL
            ))
        }
        if(columnSpecsString.contains("PRIMARY KEY")){
            constraints.add(Constraint(
                    name = columnName+"_pkey",
                    type = ConstraintType.PRIMARY_KEY
            ))
        }
        return constraints
    }
}
