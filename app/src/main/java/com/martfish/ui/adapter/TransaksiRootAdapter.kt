package com.martfish.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.martfish.R

class TransaksiRootAdapter(
    private val transaksiChildAdapter: List<TransaksiChildAdapter>,
    val activity: FragmentActivity,
    private val listTotalHarga: List<Float>
) : RecyclerView.Adapter<TransaksiRootdHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TransaksiRootdHolder {
        return TransaksiRootdHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_root_transaksi, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = transaksiChildAdapter.size

    override fun onBindViewHolder(holder: TransaksiRootdHolder, position: Int) {
        holder.bindPemesanan(transaksiChildAdapter[position], position.plus(1), activity, listTotalHarga[position])
    }
}

class TransaksiRootdHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val rvRecyclerAdapter = view.findViewById<RecyclerView>(R.id.rvRecyclerAdapter)
    private val mtvWeeks = view.findViewById<MaterialTextView>(R.id.mtvWeeks)
    private val mtvTotalHarga = view.findViewById<MaterialTextView>(R.id.mtvTotalHarga)

    @SuppressLint("SetTextI18n")
    fun bindPemesanan(
        transaksiChildAdapter: TransaksiChildAdapter,
        week: Int,
        activity: FragmentActivity,
        totalHarga: Float
    ) {
        mtvWeeks.text = "Minggu ke-$week"
        mtvTotalHarga.text = "Total pendapatan: $totalHarga"
        rvRecyclerAdapter.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = transaksiChildAdapter
        }
    }
}