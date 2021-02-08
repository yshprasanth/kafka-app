package com.ssscl.kafka.common

data class Author(var fName: String, var lName: String) {
    constructor(): this("myFName", "myLname")

    override fun toString(): String {
        return fName + "," + lName;
    }
}
