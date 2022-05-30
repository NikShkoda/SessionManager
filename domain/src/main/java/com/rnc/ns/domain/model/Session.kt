package com.rnc.ns.domain.model

data class Session(
    val sessionCount: Int = 0,
    val lastSessionTime: Long = System.currentTimeMillis()
)