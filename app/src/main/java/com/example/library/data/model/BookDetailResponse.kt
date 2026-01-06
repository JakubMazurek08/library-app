package com.example.library.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class BookDetailResponse(
    @SerializedName("key")
    val key: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("description")
    @JsonAdapter(DescriptionAdapter::class)
    val description: String?,
    
    @SerializedName("covers")
    val covers: List<Int>?,
    
    @SerializedName("first_publish_date")
    val firstPublishDate: String?,
    
    @SerializedName("subjects")
    val subjects: List<String>?
) {
    val descriptionText: String
        get() = description ?: "No description available"
}

// Inline deserializer - handles both string and object {"value": "..."}
class DescriptionAdapter : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        return when {
            json.isJsonPrimitive -> json.asString
            json.isJsonObject -> json.asJsonObject.get("value")?.asString ?: ""
            else -> ""
        }
    }
}

