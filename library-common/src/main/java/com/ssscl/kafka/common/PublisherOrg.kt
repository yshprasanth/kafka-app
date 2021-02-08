package com.ssscl.kafka.common

import java.io.Serializable

data class PublisherOrg(val name: String) : Serializable {

    companion object {
        private val serialVersionUID: Long = 22L
    }

    override fun toString(): String {
        return name
    }
}