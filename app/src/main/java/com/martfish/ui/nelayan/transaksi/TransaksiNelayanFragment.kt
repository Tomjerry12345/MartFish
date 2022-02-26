package com.martfish.ui.nelayan.transaksi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.TransaksiNelayanFragmentBinding
import com.martfish.model.ModelPemesanan
import com.martfish.ui.adapter.TransaksiChildAdapter
import com.martfish.ui.adapter.TransaksiRootAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.google.firebase.firestore.ktx.toObjects as toObjects
import com.github.mikephil.charting.utils.ColorTemplate

import com.github.mikephil.charting.data.BarData

import com.github.mikephil.charting.data.BarDataSet

import com.github.mikephil.charting.data.BarEntry

class TransaksiNelayanFragment : Fragment(R.layout.transaksi_nelayan_fragment) {

    private val viewModel: TransaksiNelayanViewModel by viewModels {
        TransaksiNelayanViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: TransaksiNelayanFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = TransaksiNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.data.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Changed -> {
                    val snapshoot = it.data as QuerySnapshot
                    val dataPemesanan = snapshoot.toObjects<ModelPemesanan>()
                    showLogAssert("dataPemesanan", "$dataPemesanan")

                    val listWeek1 = ArrayList<ModelPemesanan>()
                    val listWeek2 = ArrayList<ModelPemesanan>()
                    val listWeek3 = ArrayList<ModelPemesanan>()
                    val listWeek4 = ArrayList<ModelPemesanan>()

                    var totalHarga1 = 0f
                    var totalHarga2 = 0f
                    var totalHarga3 = 0f
                    var totalHarga4 = 0f

                    dataPemesanan.forEach { pemesanan ->
                        when (pemesanan.week) {
                            1 -> {
                                listWeek1.add(pemesanan)
                                totalHarga1 += pemesanan.totalBayar!!.toFloat()
                            }
                            2 -> {
                                listWeek2.add(pemesanan)
                                totalHarga2 += pemesanan.totalBayar!!.toFloat()
                            }
                            3 -> {
                                listWeek3.add(pemesanan)
                                totalHarga3 += pemesanan.totalBayar!!.toFloat()
                            }
                            4 -> {
                                listWeek4.add(pemesanan)
                                totalHarga4 += pemesanan.totalBayar!!.toFloat()
                            }
                            else -> {
                                listWeek4.add(pemesanan)
                                totalHarga4 += pemesanan.totalBayar!!.toFloat()
                            }
                        }
                    }

                    showLogAssert("listWeek1", "$listWeek1")
                    showLogAssert("listWeek2", "$listWeek2")
                    showLogAssert("listWeek3", "$listWeek3")
                    showLogAssert("listWeek4", "$listWeek4")

                    val listAdapter = listOf(
                        TransaksiChildAdapter(listWeek1),
                        TransaksiChildAdapter(listWeek2),
                        TransaksiChildAdapter(listWeek3),
                        TransaksiChildAdapter(listWeek4)
                    )

                    val listTotalHarga = listOf(
                        totalHarga1,
                        totalHarga2,
                        totalHarga3,
                        totalHarga4
                    )

                    setRecyclerView(listAdapter, listTotalHarga)
                    setBarchartGrafik(listTotalHarga)

                }
                is Response.Error -> {
                }
                is Response.Success -> {
                }
            }
        }

    }

    private fun setRecyclerView(
        listAdapter: List<TransaksiChildAdapter>,
        listTotalHarga: List<Float>
    ) {

        val adapter1 = TransaksiRootAdapter(listAdapter, requireActivity(), listTotalHarga)

        binding.rvTransaksi.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = adapter1
        }
    }

    private fun setBarchartGrafik(listTotalHarga: List<Float>) {
        showLogAssert("barchart grafik", "$listTotalHarga")
        val entries: ArrayList<BarEntry> = ArrayList()
        var index = 0
        listTotalHarga.forEach {
            entries.add(BarEntry(it, index))
            index++
        }

//        entries.add(BarEntry(2f, 1))
//        entries.add(BarEntry(5f, 2))
//        entries.add(BarEntry(20f, 3))

        val bardataset = BarDataSet(entries, "Penjualan")

        val labels = ArrayList<String>()
        labels.add("Minggu ke-1")
        labels.add("Minggu ke-2")
        labels.add("Minggu ke-3")
        labels.add("Minggu ke-4")

        val data = BarData(labels, bardataset)
        binding.barchart.data = data // set the data and list of labels into chart

        binding.barchart.setDescription("Jumlah penjualan perminggu") // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
        binding.barchart.animateY(5000)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_transaksi, menu)
        val barcharToolbar = menu.findItem(R.id.barchar_toolbar)
        var onClickBarchart = false
        barcharToolbar.setOnMenuItemClickListener {
            val id = it.itemId
            if (id == R.id.barchar_toolbar) {
                val myDrawable = if (onClickBarchart) {
                    binding.rvTransaksi.visibility = View.VISIBLE
                    binding.barchart.visibility = View.GONE
                    onClickBarchart = false
                    resources.getDrawable(R.drawable.ic_baseline_bar_chart_24)
                } else {
                    binding.rvTransaksi.visibility = View.GONE
                    binding.barchart.visibility = View.VISIBLE
                    onClickBarchart = true
                    resources.getDrawable(R.drawable.ic_baseline_backup_table_24)
                }

                it.icon = myDrawable
            }

            false
        }

    }

}