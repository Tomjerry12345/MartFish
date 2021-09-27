package com.martfish.utils

object Constant {

    val reffNelayan = "Nelayan"
    val reffPembeli = "Pelayan"

    // Saved Data Key
    val users = "users"
    val pemesanan = "pemesanan"
    val produk = "produk"

    // key parameters bundle
    val produkBundle = "produkBundle"
    val listDataPembeliBundle = "listDataPembeli"
    val pemesananBundle = "pemesananBundle"

    val typeAccount = "typeAccount"

    val listKategori = listOf(
        "Kakap/Ikan Merah/Bambangan/Kurisi",
        "Layang",
        "Banyara",
        "Kembung",
        "Peperek/Bete-bete",
        "Tembang",
        "Tawes",
        "Gabus",
        "Sepat Slam",
        "Tambakan+Lele",
        "Mas",
        "Nila",
        "Mujair",
        "Betutu",
        "Bandeng",
        "Udang Windu",
        "Udang Vannamae",
    )

    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102
    const val PERMISSIONS_REQUEST_ACCESS_READ_PHONE_STATE = 101
    const val PICK_IMAGE_REQUEST = 1
    const val PICK_CAMERA_REQUEST = 2
}