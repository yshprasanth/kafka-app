package com.ssscl.kafka.common.serializer

import com.ssscl.kafka.common.Author
import org.apache.kafka.common.serialization.Deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.Exception


class AuthorKafkaDeserializer : Deserializer<Author> {
    val mapper = ObjectMapper()

    override fun deserialize(topic: String?, authorByteArray: ByteArray?): Author? {
        var author: Author? = null
        try {
            System.out.println("Deserializing authorByteArray: " + authorByteArray)
            author = mapper.readValue(authorByteArray, Author::class.java)
            System.out.println("after Deserialized author: " + author)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return author
    }
}