package com.martfish.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.martfish.model.ModelPemesanan
import com.martfish.model.ModelProduk
import com.martfish.model.ModelUsers


object SavedData {
    lateinit var sharedPref: SharedPreferences
    private val gson = Gson()

    fun init(activity: FragmentActivity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
    }

    fun saveDataUsers(data: ModelUsers) {
        val json = gson.toJson(data)
        with (sharedPref.edit()) {
            putString(Constant.users, json)
            commit()
        }
    }

    fun getDataUsers(): ModelUsers? {
        val json: String? = sharedPref.getString(Constant.users, "")
        return gson.fromJson(json, ModelUsers::class.java)
    }

    fun saveDataPemesanan(data: ModelPemesanan) {
        val json = gson.toJson(data)
        with (sharedPref.edit()) {
            putString(Constant.pemesanan, json)
            commit()
        }
    }

    fun getDataPemesanan(): ModelPemesanan? {
        val json: String? = sharedPref.getString(Constant.pemesanan, "")
        return gson.fromJson(json, ModelPemesanan::class.java)
    }

    fun saveDataProduk(data: ModelProduk) {
        val json = gson.toJson(data)
        with (sharedPref.edit()) {
            putString(Constant.produk, json)
            commit()
        }
    }

    fun getDataProduk(): ModelProduk? {
        val json: String? = sharedPref.getString(Constant.produk, "")
        return gson.fromJson(json, ModelProduk::class.java)
    }
}