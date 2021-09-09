package com.martfish.ui.activity.succes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.martfish.R
import com.martfish.databinding.ActivitySuccesBinding
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.Constant
import com.martfish.utils.SavedData

class SuccesActivity : AppCompatActivity() {

    val dataUsers = SavedData.getDataUsers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySuccesBinding = DataBindingUtil.setContentView(this, R.layout.activity_succes)

        binding.mbKembali.setOnClickListener {
            val intent = if (dataUsers?.jenisAkun.equals("Nelayan"))
                Intent(this, NelayanActivity::class.java)
            else
                Intent(this, PembeliActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}