package com.ssscl.kafka.common

import org.apache.kafka.common.serialization.StringSerializer

data class Book(val name: String) : StringSerializer() {

    companion object {
        private val serialVersionUID: Long = 11L
    }

    override fun toString(): String {
        return name.toString()
    }
}