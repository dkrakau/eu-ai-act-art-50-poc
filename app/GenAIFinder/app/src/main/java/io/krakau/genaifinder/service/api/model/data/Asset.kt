package io.krakau.genaifinder.service.api.model.data

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("_id") val id: MongoId,
    val metadata: Metadata,
    val nnsId: Long,
    val distance: Int
)

data class MongoId(
    @SerializedName("\$oid") val oid: String
)
