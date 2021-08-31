package com.martfish.model

import java.io.Serializable

data class ModelKomentar(
    val idKomentar: String? = null,
    val image: String? = null,
    val nama: String? = null,
    val komentar: String? = null,
): Serializable
