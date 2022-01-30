package com.martfish.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelProduk
import com.martfish.utils.Constant
import com.martfish.utils.SavedData

class ProdukAdapter(private val produk: List<ModelProduk>) : RecyclerView.Adapter<ProdukHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ProdukHolder {
        return ProdukHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_card_produk, viewGroup, false))
    }

    override fun getItemCount(): Int = produk.size

    override fun onBindViewHolder(holder: ProdukHolder, position: Int) {
        holder.bindProduk(produk[position])
    }
}

class ProdukHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvRating = view.findViewById<MaterialTextView>(R.id.mtvRating)
    private val ratingProduk = view.findViewById<AppCompatRatingBar>(R.id.ratingProduk)
    private val mtvNama = view.findViewById<MaterialTextView>(R.id.mtvNama)
    private val mtvHargaPerEkor = view.findViewById<MaterialTextView>(R.id.mtvHargaPerEkor)
    private val mtvHargaPerGompo = view.findViewById<MaterialTextView>(R.id.mtvHargaPerGompo)
    private val mtvHargaPerKg = view.findViewById<MaterialTextView>(R.id.mtvHargaPerKg)
    private val mtvKecamatan = view.findViewById<MaterialTextView>(R.id.mtvKecamatan)
    private val mtvKelurahan = view.findViewById<MaterialTextView>(R.id.mtvKelurahan)
    private val mtvAlamat = view.findViewById<MaterialTextView>(R.id.mtvAlamat)
    private val mbDetail = view.findViewById<MaterialButton>(R.id.mbDetail)

    val dataUsers = SavedData.getDataUsers()

    @SuppressLint("SetTextI18n")
    fun bindProduk(produk: ModelProduk) {
        Glide
            .with(view.context)
            .load(produk.image)
            .centerCrop()
            .into(imgProduk);
        mtvRating.text = "(${produk.rating!!})"
        ratingProduk.rating = produk.rating!!
        mtvNama.text = produk.kategori
        mtvHargaPerEkor.text = "Harga / ekor : Rp. ${produk.hargaPerEkor}"
        mtvHargaPerGompo.text = "Harga / gompo : Rp. ${produk.hargaPerGompo}"
        mtvHargaPerKg.text = "Harga / kg : Rp. ${produk.hargaPerKg}"
        mtvKecamatan.text = "${produk.kecamatan}"
        mtvKelurahan.text = "${produk.kelurahan}"
        mtvAlamat.text = "${produk.alamat}"

        mbDetail.setOnClickListener {
            if (dataUsers?.jenisAkun == "Nelayan")
                it.findNavController().navigate(R.id.action_homeNelayanFragment_to_detailProdukFragment)
            else
                it.findNavController().navigate(R.id.action_homePembeliFragment_to_detailProdukFragment2)

            SavedData.saveDataProduk(produk)
        }
    }
}