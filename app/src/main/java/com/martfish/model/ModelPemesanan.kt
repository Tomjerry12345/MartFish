package com.martfish.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelPemesanan(
    val namaPemesan: String? = null,
    val namaProduk: String? = null,
    val jumlah: Int? = null,
    val harga: Double? = null,
    val totalBayar: Double? = null,
    val kecamatan: String? = null,
    val kelurahan: String? = null,
    val alamat: String? = null,
    val usernamePemesan: String? = null,
    val usernamePenjual: String? = null,
    val statusPengiriman: Boolean? = null,
    val image: String? = null,
    val idPemesan: String? = null,
    val idProduk: String? = null,
    var idTransaction: String? = null,
    var statusPembayaran: String = "",
    var transactionTime: String? = null,
    var week: Int? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var stok: Int? = null,
    var jumlahKilo: Int? = null,
    var waktu: String? = null
): Parcelable
