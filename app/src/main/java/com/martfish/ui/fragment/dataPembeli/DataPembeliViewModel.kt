package com.martfish.ui.fragment.dataPembeli

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelPemesanan
import com.martfish.ui.activity.pembayaran.PembayaranActivity
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.utils.*
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("StaticFieldLeak")
class DataPembeliViewModel(
    val firestoreDatabase: FirestoreDatabase,
    val activity: FragmentActivity
) : ViewModel() {

    val namaPenerima = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val jumlahBeli = MutableLiveData<String>()
    val jumlahKilo = MutableLiveData<String>()
    val harga = MutableLiveData<String>()
    val totalBayar = MutableLiveData<String>()
    val namaProduk = MutableLiveData<String>()
    val longitude = MutableLiveData<Double>()
    val latitude = MutableLiveData<Double>()
    val tanggal = MutableLiveData<String>()
    val waktu = MutableLiveData<String>()

    var total = 0
    var hargaProduk = 0
    var usernamePenjual = ""
    var metodePembayaran: String? = null
    var image = ""
    var idProduk = ""

    private val dataUser = SavedData.getDataUsers()
    private val dataProduk = SavedData.getDataProduk()

    fun onKonfirmasi(view: View) {
        try {
            val namaPenerima = namaPenerima.value ?: throw Exception("Nama Penerima tidak boleh kosong")
            val kecamatan = kecamatan.value ?: throw Exception("Kecamatan tidak boleh kosong")
            val kelurahan = kelurahan.value ?: throw Exception("Kelurahan tidak boleh kosong")
            val alamat = alamat.value ?: throw Exception("ALamat tidak boleh kosong")
            val metodePembayaran1 = metodePembayaran ?: throw Exception("Metode pembayaran tidak boleh kosong")

            val pemesan = ModelPemesanan(
                namaPemesan = namaPenerima,
                namaProduk = namaProduk.value,
                jumlah = jumlahBeli.value?.toInt(),
                harga = hargaProduk.toDouble(),
                totalBayar = total.toDouble(),
                kecamatan = kecamatan,
                kelurahan = kelurahan,
                alamat = alamat,
                usernamePemesan = dataUser?.username,
                usernamePenjual = usernamePenjual,
                statusPengiriman = false,
                image = image,
                idPemesan = null,
                idProduk = idProduk,
                idTransaction = null,
                week = getOfWeeks(),
                longitude = longitude.value,
                latitude = latitude.value,
                stok = dataProduk?.stok,
                jumlahKilo = jumlahKilo.value?.toInt(),

            )

            if (jumlahBeli.value?.toInt()!! > dataProduk?.stok!!) {
                showSnackbar(view, "Jumlah beli melebihi stok yang tersedia", "error")
            } else {
                if (metodePembayaran1 == "cod") {
                    viewModelScope.launch {
                        saveDataFirestore(pemesan)
                    }
                } else {
                    val intent = Intent(activity, PembayaranActivity::class.java)
                    intent.putExtra(Constant.listDataPembeliBundle, pemesan)
                    activity.startActivity(intent)
                }
            }




        } catch (e: Exception) {
            showSnackbar(view, "${e.message}", "error")
        }

    }

    private suspend fun saveDataFirestore(dataPemesan: ModelPemesanan) {
        val response = firestoreDatabase.saveDataReference("pemesanan", dataPemesan, "")
        showLogAssert("response", "$response")
        when (response) {
            is Response.Changed -> {
                firestoreDatabase.updateReferenceCollectionOne(
                    "pemesanan",
                    response.data.toString(),
                    "idPemesan",
                    response.data.toString()
                )

                val intent = Intent(activity, SuccesActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }

            is Response.Error -> {
                showLogAssert("error", response.error)
            }

            is Response.Success -> {
            }
        }
    }

    private fun getOfWeeks(): Int {
        val calender: Calendar = Calendar.getInstance()
        return calender.get(Calendar.WEEK_OF_MONTH)
    }

    fun onTambah() {
        jumlahBeli.value = jumlahBeli.value?.toInt()?.plus(1).toString()
    }

    fun onKurang() {
        if (jumlahBeli.value?.toInt()!! > 1)
            jumlahBeli.value = jumlahBeli.value?.toInt()?.minus(1).toString()
    }

    fun onWaktu() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()
        picker.show(activity.supportFragmentManager, "tag 1")

//        picker.addOnPositiveButtonClickListener {
//            // call back code
//            showDialog(activity, "Jam : ${picker.hour}, Menit: ${picker.minute}")
//        }

    }

    fun onTanggal() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.JANUARY
        val janThisYear = calendar.timeInMillis

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calendar.timeInMillis

// Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(janThisYear)
                .setEnd(decThisYear)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
        datePicker.show(activity.supportFragmentManager, "tag")
    }

    class Factory(
        private val firestoreDatabase: FirestoreDatabase,
        private val activity: FragmentActivity
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DataPembeliViewModel(firestoreDatabase, activity) as T
        }
    }
}