package com.alo.sqllibscomparison.infrastructure.persistence.jooq

import com.alo.sqllibscomparison.infrastructure.persistence.KtPostgreSQLContainer
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target

class JooqCodeGen {

    private val dbSchema = "public"
    private val dbUser = "user"
    private val dbPassword = "password"

    protected val postgresqlContainer = KtPostgreSQLContainer()
        .withUsername(dbUser)
        .withPassword(dbPassword)
        .withInitScript("todo-list-database.sql")

    fun generate() {
        postgresqlContainer.start()
        codeGen()
        postgresqlContainer.stop()
    }

    private fun codeGen() {
        val configuration = Configuration()
            .withJdbc(
                Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl(postgresqlContainer.jdbcUrl)
                    .withUser(dbUser)
                    .withPassword(dbPassword)
                    .withSchema(dbSchema)
            )
            .withGenerator(Generator()
                .withDatabase(Database()
                    .withName("org.jooq.meta.postgres.PostgresDatabase")
                    .withIncludes(".*")
                    .withExcludes("")
                    .withInputSchema(dbSchema))
                .withTarget(Target()
                    .withPackageName("com.alo.sqllibscomparison.infrastructure.jooq.generated")
                    .withDirectory("src/main/java"))
            )

        GenerationTool.generate(configuration)
    }

}

fun main(args: Array<String>) {
    JooqCodeGen().generate()
}
