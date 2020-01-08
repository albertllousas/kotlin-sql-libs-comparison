package com.alo.sqllibscomparison.infrastructure.persistence

import org.testcontainers.containers.PostgreSQLContainer

class KtPostgreSQLContainer() : PostgreSQLContainer<KtPostgreSQLContainer>("postgres:11.1")