package com.martfish.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelPemesanan
import com.martfish.ui.nelayan.pesanan.belumTerkirim.BelumTerkirimViewModel
import com.martfish.ui.nelayan.pesanan.terkirim.TerkirimViewModel
import com.martfish.ui.pembeli.pesanan.PesananPembeliViewModel
import com.martfish.utils.Maps
import com.martfish.utils.SavedData

class PemesananAdapter(
    private val pemesanan: List<ModelPemesanan>,
    val belumTerkirimViewModel: BelumTerkirimViewModel?,
    val terkirimViewModel: TerkirimViewModel?,
    val pesananPembeliViewModel: PesananPembeliViewModel?,
) : RecyclerView.Adapter<PemesananHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PemesananHolder {
        return PemesananHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_pesanan, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = pemesanan.size

    override fun onBindViewHolder(holder: PemesananHolder, position: Int) {
        holder.bindProduk(pemesanan[position], belumTerkirimViewModel, terkirimViewModel)
    }
}

class PemesananHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvNamaPemesan = view.findViewById<MaterialTextView>(R.id.mtvNamaPemesan)
    private val mtvNamaProduk = view.findViewById<MaterialTextView>(R.id.mtvNamaProduk)
    private val mtvJumlahPesan = view.findViewById<MaterialTextView>(R.id.mtvJumlahPesan)
    private val mtvHarga = view.findViewById<MaterialTextView>(R.id.mtvHarga)
    private val mtvJumlahBayar = view.findViewById<MaterialTextView>(R.id.mtvJumlahBayar)
    private val mtvKecamatan = view.findViewById<MaterialTextView>(R.id.mtvKecamatan)
    private val mtvKelurahan = view.findViewById<MaterialTextView>(R.id.mtvKelurahan)
    private val mtvStatusPembayaran = view.findViewById<MaterialTextView>(R.id.mtvStatusPembayaran)
    private val mtvAlamat = view.findViewById<MaterialTextView>(R.id.mtvAlamat)
    private val mbPesananTerkirim = view.findViewById<MaterialButton>(R.id.mbPesananTerkirim)
    private val mbPesan = view.findViewById<MaterialButton>(R.id.mbPesan)
    private val mbLokasi = view.findViewById<MaterialButton>(R.id.mbLokasi)

    val dataUsers = SavedData.getDataUsers()

    @SuppressLint("SetTextI18n")
    fun bindProduk(
        pemesanan: ModelPemesanan,
        belumTerkirimViewModel: BelumTerkirimViewModel?,
        terkirimViewModel: TerkirimViewModel?
    ) {
        Glide
            .with(view.context)
            .load(pemesanan.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(imgProduk);
        mtvNamaPemesan.text = "Nama pemesan : ${pemesanan.namaPemesan}"
        mtvNamaProduk.text = "Nama produk : ${pemesanan.namaProduk}"
        mtvJumlahPesan.text = "Jumlah : ${pemesanan.jumlah.toString()}"
        mtvHarga.text = "Harga : ${pemesanan.harga.toString()}"
        mtvJumlahBayar.text = "Jumlah bayar : ${pemesanan.totalBayar.toString()}"
        mtvKecamatan.text = "Kecamatan : ${pemesanan.kecamatan}"
        mtvKelurahan.text = "Kelurahan : ${pemesanan.kelurahan}"
        mtvAlamat.text = "Alamat : ${pemesanan.alamat}"
        mtvStatusPembayaran.text = pemesanan.statusPembayaran

        when(pemesanan.statusPembayaran) {
            "pending" -> mtvStatusPembayaran.background = ContextCompat.getDrawable(view.context, R.drawable.rounded_bg_pending)
            "invalid" -> mtvStatusPembayaran.background = ContextCompat.getDrawable(view.context, R.drawable.rounded_bg_failed)
            "failed" -> mtvStatusPembayaran.background = ContextCompat.getDrawable(view.context, R.drawable.rounded_bg_failed)
            "settlement" -> mtvStatusPembayaran.background = ContextCompat.getDrawable(view.context, R.drawable.rounded_bg_succes)
        }

        if (dataUsers?.jenisAkun == "Pembeli") {
            mbPesananTerkirim.visibility = View.GONE
            mbLokasi.visibility = View.GONE
        }

        mbPesananTerkirim.setOnClickListener {
            val newStok = pemesanan.stok?.minus(pemesanan.jumlah!!)
            belumTerkirimViewModel?.onPesananTerkirim(pemesanan.idPemesan, view, pemesanan.idProduk, newStok)
        }

        mbPesan.setOnClickListener {
            SavedData.saveDataPemesanan(pemesanan)

            if (dataUsers?.jenisAkun == "Nelayan")
                it.findNavController().navigate(R.id.action_pesananNelayanFragment_to_chatFragment)
            else
                it.findNavController().navigate(R.id.action_pesananPembeliFragment_to_chatFragment2)
        }

        mbLokasi.setOnClickListener {
            Maps.intentToMaps(pemesanan.latitude!!, pemesanan.longitude!!, it.context)
        }
    }
}