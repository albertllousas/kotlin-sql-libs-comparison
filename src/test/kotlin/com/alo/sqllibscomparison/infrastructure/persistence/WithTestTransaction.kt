package com.alo.sqllibscomparison.infrastructure.persistence

interface WithTestTransaction {
    fun withTestTransaction(testBlock: () -> Unit)
}