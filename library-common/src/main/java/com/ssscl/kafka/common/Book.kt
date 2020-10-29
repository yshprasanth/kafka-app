package com.ssscl.kafka.common

import java.io.Serializable

data class Book(val name: String) : Serializable {

    companion object {
        private val serialVersionUID: Long = 11L
    }

    override fun toString(): String {
        return name;
    }
}