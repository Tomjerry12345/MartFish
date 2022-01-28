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

    val listJenisAkun = listOf("Pembeli", "Nelayan")
    val listJenisBank = listOf("BNI", "BRI", "MANDIRI")
    val listKecamatan = listOf("Bajeng",  "Barombong", "Biringbulu",  "Bontomarannu", "Bontonompo",
        "Bontonompo Selatan", "Bungaya", "Pallangga", "Parangloe",  "Somba Opu", "Tinggimoncong", "Tompobulu", "Tombolo Pao")

    val kelurahanBajeng = listOf("Kalebajeng", "Limbung", "Mata Allo", "Tubajeng")
    val kelurahanBarombong = listOf("Benteng Somba Opu", "Lembang Parang")
    val kelurahanBiringbulu = listOf("Lauwa", "Tonrorita")
    val kelurahanBontomarannu = listOf("Bontomanai", "Borongloe", "Romang Lompoa")
    val kelurahanBontonompo = listOf("Bontonompo", "Kalaserena", "Tamallayang")
    val kelurahanBontonompoSelatan = listOf("Bontoramba")
    val kelurahanBungaya = listOf("Je'nebatu", "Sapaya")
    val kelurahanPallangga = listOf("Mangalli", "Pangkabinanga", "Parangbanoa", "Tetebatu")
    val kelurahanParangloe = listOf("Bontoparang", "Lanna")
    val kelurahanSombaOpu = listOf(
        "Batangkaluku", "Bonto-Bontoa", "Bontoramba", "Kalegowa",
        "Katangka", "Mawang", "Paccinongang", "Pandang-Pandang", "Romangpolong",
        "Samata", "Sungguminasa", "Tamarunang", "Tombolo", "Tompobalang")
    val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")
    val kelurahanTinggimoncong = listOf("Bonto Lerung", "Bulutana", "Gantarang", "Garassi", "Malino", "Pattapang")
    val kelurahanTompobulu = listOf("Cikoro", "Malakaji")
    val kelurahanTomboloPao = listOf("Tamaona")

    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102
    const val PERMISSIONS_REQUEST_ACCESS_READ_PHONE_STATE = 101
    const val PICK_IMAGE_REQUEST = 1
    const val PICK_CAMERA_REQUEST = 2
}