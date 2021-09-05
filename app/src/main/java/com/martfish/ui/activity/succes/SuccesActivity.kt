package com.martfish.ui.activity.succes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.martfish.R
import com.martfish.databinding.ActivitySuccesBinding
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.utils.Constant

class SuccesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySuccesBinding = DataBindingUtil.setContentView(this, R.layout.activity_succes)

        val typeAccount = intent.getStringExtra(Constant.typeAccount)

        binding.mbKembali.setOnClickListener {
            val intent = if (typeAccount.equals("nelayan"))
                Intent(this, NelayanActivity::class.java)
            else
                Intent(this, NelayanActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}