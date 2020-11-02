package com.ssscl.kafka.common

data class Book(var name: String) {

    constructor(): this("default")

    override fun toString(): String {
        return name;
    }
}