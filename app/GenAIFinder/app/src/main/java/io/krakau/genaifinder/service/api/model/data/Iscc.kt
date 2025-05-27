package io.krakau.genaifinder.service.api.model.data

import com.google.gson.annotations.SerializedName

data class Iscc(
    val thumbnail: String,
    @SerializedName("\$schema") val schema: String,
    @SerializedName("@type") val type: String,
    val filesize: Long,
    @SerializedName("@context") val context: String,
    val content: String,
    val mode: String,
    val filename: String,
    val datahash: String,
    val name: String,
    val width: Int,
    val media_id: String,
    val iscc: String,
    val metahash: String,
    val mediatype: String,
    val height: Int
)
