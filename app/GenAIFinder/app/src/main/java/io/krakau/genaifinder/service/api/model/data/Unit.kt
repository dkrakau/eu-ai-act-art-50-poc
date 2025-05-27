package io.krakau.genaifinder.service.api.model.data

data class Unit(
    val readable: String,
    val hash_hex: String,
    val iscc_unit: String,
    val hash_bits: String,
    val hash_unit: String
)