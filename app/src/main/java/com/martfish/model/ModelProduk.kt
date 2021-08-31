package com.martfish.model

data class ModelProduk(
    val idProduk: String? = null,
    val image: String? = null,
    val nama: String? = null,
    val harga: Int? = null,
    val stok: Int? = null,
    val kategori: String? = null,
    val kecamatan: String? = null,
    val kelurahan: String? = null,
    val alamat: String? = null,
    val rating: Float? = null,
    val namaPenjual: String? = null,
    val usernamePenjual: String? = null
)
