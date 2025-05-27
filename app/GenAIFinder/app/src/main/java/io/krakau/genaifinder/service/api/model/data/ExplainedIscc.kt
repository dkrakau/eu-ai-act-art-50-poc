package io.krakau.genaifinder.service.api.model.data

data class ExplainedIscc(
    val iscc: String,
    val readable: String,
    val multiformat: String,
    val decomposed: String,
    val units: List<Unit>
)