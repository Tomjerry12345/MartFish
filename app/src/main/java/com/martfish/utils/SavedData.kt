package com.martfish.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
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
}