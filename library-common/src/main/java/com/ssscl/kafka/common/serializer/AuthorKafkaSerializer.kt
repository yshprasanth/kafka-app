package com.ssscl.kafka.common.serializer

import com.ssscl.kafka.common.Author
import org.apache.kafka.common.serialization.Serializer
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.Exception


class AuthorKafkaSerializer : Serializer<Author> {
    val objectMapper = ObjectMapper()
    override fun serialize(topic: String?, author: Author?): ByteArray {
        var retVal: ByteArray? = null
        try {
            System.out.println("Serializing Author: " + author)
            retVal = objectMapper.writeValueAsString(author).toByteArray()
            System.out.println("after Serializing Author: " + retVal)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retVal!!
    }

}