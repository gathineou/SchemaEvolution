package com.schemaEvolution.schemaEvolution.service

import com.schemaEvolution.schemaEvolution.model.Schema
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.util.logging.Logger

@Service
class SchemaLoader(
        private val schemaConstructor: SchemaConstructor
) {

    private val logger = Logger.getLogger(SchemaLoader::class.toString())

    fun loadSchemas(): MutableList<Schema> {
        val schemas = mutableListOf<Schema>()
        val schemasFiles = getSchemaFiles()
        schemasFiles.forEach {
            try {
                val sqlCommands = getSqlCommands(it)
                val schema = schemaConstructor.constructSchema(sqlCommands, it)
                schemas.add(schema)
            }
            catch (e: Exception){
                logger.warning(e.toString())
            }
        }
        return schemas
    }

    private fun getSchemaFiles(): MutableList<String> {
        val fileNames = mutableListOf<String>()
        File("schemas").walk().forEach {
            fileNames.add(it.toString())
        }
        fileNames.sort()

        return fileNames
    }

    private fun getSqlCommands(file: String): List<String> {
        val filereader = FileReader(file)
        var lines = filereader.readLines()
        var cleanLines = lines.filter { !(it.startsWith("--"))  }
        cleanLines = cleanLines.filter { it.isNotBlank() }

        var sqlCommands = createSqlCommandsStrings(cleanLines)
        sqlCommands = removeUnsupportedCommands(sqlCommands)
        return sqlCommands
    }

    private fun createSqlCommandsStrings(lines: List<String>):List<String>{
        var result = mutableListOf<String>()
        var tmpString = ""
        lines.forEachIndexed { index, s ->
            tmpString = tmpString.plus(s)
            if(s.contains(";")){
                result.add(tmpString)
                tmpString = ""
            }
        }
        return result
    }

    private fun removeUnsupportedCommands(commands: List<String>):List<String>{
        var result = commands
        result = result.filter { !it.contains("OWNER") }
        result = result.filter { !it.contains("OWNED") }
        result = result.filter { !it.contains("SEQUENCE") }
        result = result.filter { !it.contains("nextval") }
        result = result.filter { !it.contains("SET") }
        result = result.filter { !it.contains("SELECT") }
        result = result.map {
            var tmp = it.replace("true", "\"True\"", true)
            tmp = tmp.replace("False", "\"False\"", true)
            return@map tmp
        }

        return result
    }

}