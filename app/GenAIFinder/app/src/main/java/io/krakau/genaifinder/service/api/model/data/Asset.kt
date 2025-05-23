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

data class Metadata(
    val provider: Provider,
    val iscc: IsccData
)

data class Provider(
    val name: String,
    val prompt: String,
    val timestamp: Long,
    val credentials: Credentials
)

data class Credentials(
    val message: String,
    val encryptedMessage: String,
    val publicKey: String
)

data class IsccData(
    val data: Iscc,
    val explained: ExplainedIscc
)

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

data class ExplainedIscc(
    val iscc: String,
    val readable: String,
    val multiformat: String,
    val decomposed: String,
    val units: List<Unit>
)

data class Unit(
    val readable: String,
    val hash_hex: String,
    val iscc_unit: String,
    val hash_bits: String,
    val hash_unit: String
)
