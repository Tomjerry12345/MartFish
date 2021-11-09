package com.martfish.ui.nelayan.myProduk.adapter

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.martfish.ui.nelayan.myProduk.MyProdukNelayanViewModel
import com.martfish.utils.showLogAssert

class MyProdukAdapter(
    private val produk: List<ModelProduk>,
    val viewModel: MyProdukNelayanViewModel
) : RecyclerView.Adapter<MyProdukHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MyProdukHolder {
        return MyProdukHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_my_produk, viewGroup, false))
    }

    override fun getItemCount(): Int = produk.size

    override fun onBindViewHolder(holder: MyProdukHolder, position: Int) {
        holder.bindProduk(produk[position], viewModel)
    }
}

class MyProdukHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val ratingProduk = view.findViewById<AppCompatRatingBar>(R.id.ratingProduk)
    private val mtvRating = view.findViewById<MaterialTextView>(R.id.mtvRating)
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvTitle = view.findViewById<MaterialTextView>(R.id.mtvTitle)
    private val mtvKilo = view.findViewById<MaterialTextView>(R.id.mtvKilo)
    private val mtvHarga = view.findViewById<MaterialTextView>(R.id.mtvHarga)
    private val mtvKategori = view.findViewById<MaterialTextView>(R.id.mtvKategori)
    private val mtvStok = view.findViewById<MaterialTextView>(R.id.mtvStok)
    private val mtvKecamatan = view.findViewById<MaterialTextView>(R.id.mtvKecamatan)
    private val mtvKelurahan = view.findViewById<MaterialTextView>(R.id.mtvKelurahan)
    private val mtvAlamat = view.findViewById<MaterialTextView>(R.id.mtvAlamat)
    private val mbDelete = view.findViewById<MaterialButton>(R.id.mbDeleteProduk)
    private val mbEdit = view.findViewById<MaterialButton>(R.id.mbEditProduk)


    @SuppressLint("SetTextI18n")
    fun bindProduk(produk: ModelProduk, viewModel: MyProdukNelayanViewModel) {
        Glide
            .with(view.context)
            .load(produk.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(imgProduk)
        mtvRating.text = "(${produk.rating!!})"
        ratingProduk.rating = produk.rating!!
        mtvTitle.text = produk.nama
        mtvKilo.text = "${produk.kilo} KG"
        mtvHarga.text = "Rp. ${produk.harga}"
        mtvKategori.text = produk.kategori
        mtvStok.text = "Stok tersedia : ${produk.stok}"
        mtvKecamatan.text = "${produk.kecamatan}"
        mtvKelurahan.text = "${produk.kelurahan}"
        mtvAlamat.text = "${produk.alamat}"

        mbEdit.setOnClickListener {
            val bundleProduk = bundleOf("produk" to produk)
            it.findNavController().navigate(R.id.action_myProdukNelayanFragment_to_editProdukNelayanFragment, bundleProduk)
        }

        mbDelete.setOnClickListener {
            viewModel.btnDelete(produk.idProduk)
        }

    }
}