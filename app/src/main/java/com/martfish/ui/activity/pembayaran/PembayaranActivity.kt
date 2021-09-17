package com.martfish.ui.activity.pembayaran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ActivityPembayaranBinding
import com.martfish.model.ModelPemesanan
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.*
import com.martfish.utils.Constant.PERMISSIONS_REQUEST_ACCESS_READ_PHONE_STATE
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.properties.Delegates

class PembayaranActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var dataPemesan: ModelPemesanan
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    private val dataUser = SavedData.getDataUsers()

    private val permissionMaps = MutableLiveData<Boolean>()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                showLogAssert("permissionName", permissionName)
                showLogAssert("isGranted", "$isGranted")
                if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionMaps.value = isGranted
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncher.launch(
            arrayOf(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION)
        )

        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataPemesan = intent.getParcelableExtra(Constant.listDataPembeliBundle)!!

        initMidtrans()

        val transactionRequest =
            dataPemesan.totalBayar?.let { totalBayar ->
                TransactionRequest(
                    System.currentTimeMillis().toString(),
                    totalBayar
                )
            }

        transactionRequest?.customerDetails = customerDetails()
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)

//        permissionMaps.observe(this, { permission ->
//            Maps.initMaps(this)
//            Maps.getDeviceLocation(permission).observe(this, {
//                if (it != null) {
//                    latitude = it["latitude"]!!
//                    longitude = it["longitude"]!!
//                    showLogAssert("latitude", "$latitude")
//                    showLogAssert("longitude", "$longitude")
//
//                    dataPemesan = intent.getParcelableExtra(Constant.listDataPembeliBundle)!!
//
//                    initMidtrans()
//
//                    val transactionRequest =
//                        dataPemesan.totalBayar?.let { totalBayar ->
//                            TransactionRequest(
//                                System.currentTimeMillis().toString(),
//                                totalBayar
//                            )
//                        }
//
//                    transactionRequest?.customerDetails = customerDetails()
//                    MidtransSDK.getInstance().transactionRequest = transactionRequest
//                    MidtransSDK.getInstance().startPaymentUiFlow(this)
//
//                } else {
//                    showLogAssert("null maps.location", "null maps.location")
//                    showSnackbar(binding.root, "Error lokasi tidak di temukan", "error")
//                }
//            })
//        })

    }

    private fun initMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(ConfigMidtrans.CLIENT_KEY)
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl(ConfigMidtrans.BASE_URL) //set merchant url (required)
            .setUIkitCustomSetting(uiKitCustomSetting())
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            )
            .setLanguage("id")
            .buildSDK()
    }

    private fun customerDetails(): CustomerDetails {
        val customerDetails = CustomerDetails()
        customerDetails.firstName = dataPemesan.namaPemesan

        val shippingAddress = ShippingAddress()
        shippingAddress.address = "${dataPemesan.alamat} kel. ${dataPemesan.kelurahan} kec. ${dataPemesan.kecamatan}"
        shippingAddress.city = "Gowa"
        customerDetails.shippingAddress = shippingAddress

        return customerDetails
    }

    private fun uiKitCustomSetting(): UIKitCustomSetting {
        val uIKitCustomSetting = UIKitCustomSetting()
        uIKitCustomSetting.isSkipCustomerDetailsPages = true
        uIKitCustomSetting.isShowPaymentStatus = true
        return uIKitCustomSetting
    }

    override fun onTransactionFinished(result: TransactionResult) {

        val intent = Intent(this, SuccesActivity::class.java)

        val intentTo: Intent = if(dataUser?.jenisAkun.equals("Nelayan"))
            Intent(this, NelayanActivity::class.java)
        else
            Intent(this, PembeliActivity::class.java)

        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    showLogAssert("Transaction Finished. ID: ", result.response.transactionId)
                    Toast.makeText(
                        this,
                        "Transaction Finished. ID: " + result.response.transactionId,
                        Toast.LENGTH_LONG
                    ).show()

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            dataPemesan.idTransaction = result.response.orderId
                            dataPemesan.statusPembayaran = result.response.transactionStatus
                            dataPemesan.transactionTime = result.response.transactionTime
                            dataPemesan.week = getOfWeeks()
                            dataPemesan.latitude = latitude
                            dataPemesan.longitude = longitude
                            saveDataFirestore(dataPemesan)
                        }

                        startActivity(intent)
                        finish()
                    }

                }
                TransactionResult.STATUS_PENDING -> {
                    showLogAssert("Transaction Pending. orderId: ", result.response.orderId)
                    showLogAssert("Transaction Pending. ID: ", result.response.transactionId)
                    showLogAssert("Transaction Pending. transactionTime: ", result.response.transactionTime)
                    Toast.makeText(
                        this,
                        "Transaction Pending. ID: " + result.response.transactionId,
                        Toast.LENGTH_LONG
                    ).show()

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            dataPemesan.idTransaction = result.response.orderId
                            dataPemesan.statusPembayaran = result.response.transactionStatus
                            dataPemesan.transactionTime = result.response.transactionTime
                            dataPemesan.week = getOfWeeks()
                            dataPemesan.latitude = latitude
                            dataPemesan.longitude = longitude
                            saveDataFirestore(dataPemesan)
                        }

                        startActivity(intent)
                        finish()
                    }

                }
                TransactionResult.STATUS_FAILED -> {
                    showSnackbar(binding.root.rootView, result.response.statusMessage, "error")

                    startActivity(intentTo)
                    finish()

                    Toast.makeText(
                        this,
                        "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            result.response.validationMessages

        } else if (result.isTransactionCanceled) {

            startActivity(intentTo)
            finish()

            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()

        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                showLogAssert("Transaction Invalid", "Transaction Invalid")
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                showLogAssert("Transaction Invalid", "Transaction Invalid")
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private suspend fun saveDataFirestore(dataPemesan: ModelPemesanan) {
        val db = FirestoreDatabase()
        val response = db.saveDataReference("pemesanan", dataPemesan, "")
        showLogAssert("response", "$response")
        when (response) {
            is Response.Changed -> {
                db.updateReferenceCollectionOne(
                    "pemesanan",
                    response.data.toString(),
                    "idPemesan",
                    response.data.toString()
                )
            }

            is Response.Error -> {
                showLogAssert("error", response.error)
            }

            is Response.Success -> {
            }
        }
    }

    private fun getOfWeeks(): Int {
        val calender: Calendar = Calendar.getInstance()
        return calender.get(Calendar.WEEK_OF_MONTH)
    }

}