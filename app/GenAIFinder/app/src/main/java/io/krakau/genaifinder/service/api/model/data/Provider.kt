package io.krakau.genaifinder.service.api.model.data

data class Provider(
    val name: String,
    val prompt: String,
    val timestamp: Long,
    val credentials: Credentials
)