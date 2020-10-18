# Schema evolution history application

A Kotlin app for structuring, analysing and visualizing the evolution history of a PostgreSQL database schema.
This exploration started as part of the PAV ITOpenSpace and is a work in progress.
The app can parse historical database schemas and identify the history of changes (schema modification operations) 
from a source schema version to another.
The tool offers a simple interactive dashboard for visualization and further explorations.

## Schema evolution
Schema evolution and schema versioning techniques, deal with the need to retain current data and software system 
functionality in the face of changing database structure. The problem is not limited to the modification of the schema. 
It can affect the data stored under the given schema, and the queries posed on that schema (Wikipedia).

## Build and run
A directory named `schema-evolution-history/schemas` is required for storing the historical schemas of the database.
The schema SQL files' names should be formatted as following:

template: `<project-db-schema>_<date>_<author-name>_<commit-hash>.sql`

example: `project-db-schema_2020-04-15_gathineou_27ded297d461f5.sql`

Spin up the required container: `docker-compose up -d`

You can build the app with Gradle: `./gradlew build`

Once the application starts, a dashboard is exposed at: `localhost:8080/dashboard`

## Our approach
* Extract schema versions based on relevant commits
    * ```git log --date-order --date=short  --format="%H,%aN,%cd"  -- project_sql_migrations_directory/ > schema-changes-commits```
    * ```pg_dump --schema-only > schemas/project-db-schema_"$date"_"$name"_"$commit".sql```
* Parse each schema version and identify the modification operations (SMOS)
* On demand arbitrary schema comparison -> Identify SMOS as the changes introduced from the source to the destination schema.
* Retrieve history of a specific element (table or field)

## Supported SMOS (schema modification operations)
Currently, we can identify the following schema modification operations:
* ADD_TABLE
* REMOVE_TABLE
* ADD_COLUMN
* REMOVE_COLUMN
* ALTER_COLUMN_TYPE
* ADD_CONSTRAINT
* REMOVE_CONSTRAINT

## Tool
* Kotlin
* JsqlParser: SQL statement parser. Translates SQLs in a traversable hierarchy of Java classes. (supporting PostgreSQL, Oracle, SqlServer, MySQL)
* Logic: Build history structure. Identify SMOS and build comparison logic
* Interactive dashboard

## Why?
* Understanding the evolution history of a complex software system can significantly aid and inform 
current and future development initiatives of that system.
* Schema version control and changes introduced from a version to another in a more structure 
way as SMOS. Aim to improve the schema overview for easing development.
* Interactive history explorer and visualization of the schema components and history comparison among two arbitrary schemas.

## More on the topic
* Understanding database schema evolution: A case study
https://www.sciencedirect.com/science/article/pii/S0167642313003092
* VESEL: VisuaL Exploration of Schema Evolution UsingProvenance Queries
http://ceur-ws.org/Vol-2322/BigVis_9.pdf
* https://en.wikipedia.org/wiki/Schema_evolution
* Schema Evolution in Wikipedia - Toward a Web Information System Benchmark.
Schema Modification Operators
https://www.researchgate.net/publication/220710372_Schema_Evolution_in_Wikipedia_-_Toward_a_Web_Information_System_Benchmark
* Automating the database schema evolution process
https://www.researchgate.net/publication/257457826_Automating_the_database_schema_evolution_process
