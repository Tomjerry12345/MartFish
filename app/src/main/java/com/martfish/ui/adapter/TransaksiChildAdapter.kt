package com.martfish.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelPemesanan
import com.martfish.utils.showLogAssert

class TransaksiChildAdapter(
    private val pemesanan: List<ModelPemesanan>,
) : RecyclerView.Adapter<TransaksiChildHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TransaksiChildHolder {
        return TransaksiChildHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_child_transaksi, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = pemesanan.size

    override fun onBindViewHolder(holder: TransaksiChildHolder, position: Int) {
        showLogAssert("pemesanan", "$pemesanan")
        holder.bindProduk(pemesanan[position])
    }
}

class TransaksiChildHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val mtvNamaPemesan = view.findViewById<MaterialTextView>(R.id.mtvNamaPemesan)
    private val mtvNamaProduk = view.findViewById<MaterialTextView>(R.id.mtvNamaProduk)
    private val mtvHarga = view.findViewById<MaterialTextView>(R.id.mtvHarga)

    @SuppressLint("SetTextI18n")
    fun bindProduk(
        pemesanan: ModelPemesanan
    ) {
        mtvNamaPemesan.text = pemesanan.namaPemesan
        mtvNamaProduk.text = pemesanan.namaProduk
        mtvHarga.text = pemesanan.harga.toString()
    }
}