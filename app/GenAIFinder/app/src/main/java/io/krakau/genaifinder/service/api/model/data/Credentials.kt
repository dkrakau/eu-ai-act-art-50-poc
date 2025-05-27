package io.krakau.genaifinder.service.api.model.data

data class Credentials(
    val message: String,
    val encryptedMessage: String,
    val publicKey: String
)