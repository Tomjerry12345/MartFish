package com.martfish.model

import java.io.Serializable

data class ModelProduk(
    val idProduk: String? = null,
    val image: String? = null,
    val kategori: String? = null,
    val hargaPerEkor: Int? = null,
    val hargaPerGompo: Int? = null,
    val hargaPerKg: Int? = null,
    val kecamatan: String? = null,
    val kelurahan: String? = null,
    val alamat: String? = null,
    var rating: Float? = null,
    val namaPenjual: String? = null,
    val usernamePenjual: String? = null
): Serializable
