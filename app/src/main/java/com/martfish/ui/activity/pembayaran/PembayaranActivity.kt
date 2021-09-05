package com.martfish.ui.activity.pembayaran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ActivityPembayaranBinding
import com.martfish.model.ModelPemesanan
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.utils.ConfigMidtrans
import com.martfish.utils.Constant
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.*
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PembayaranActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var dataPemesan: ModelPemesanan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                101
            )
        }

        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataPemesan = intent.getParcelableExtra(Constant.listDataPembeliBundle)!!


        initMidtrans()
//
        val transactionRequest =
            dataPemesan.totalBayar?.let { totalBayar ->
                TransactionRequest(
                    System.currentTimeMillis().toString(),
                    totalBayar
                )
            }
//
        transactionRequest?.customerDetails = customerDetails()
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)

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
        intent.putExtra(Constant.typeAccount, "nelayan")

        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    showLogAssert("Transaction Finished. ID: ", result.response.transactionId)
                    Toast.makeText(
                        this,
                        "Transaction Finished. ID: " + result.response.transactionId,
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(intent)
                    finish()
                }
                TransactionResult.STATUS_PENDING -> {
                    showLogAssert("Transaction Pending. ID: ", result.response.transactionId)
                    Toast.makeText(
                        this,
                        "Transaction Pending. ID: " + result.response.transactionId,
                        Toast.LENGTH_LONG
                    ).show()

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            saveDataFirestore(dataPemesan)
                        }

                        startActivity(intent)
                        finish()
                    }

                }
                TransactionResult.STATUS_FAILED -> {

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            saveDataFirestore(dataPemesan)
                        }

                        startActivity(intent)
                        finish()
                    }

                    Toast.makeText(
                        this,
                        "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            result.response.validationMessages

        } else if (result.isTransactionCanceled) {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    saveDataFirestore(dataPemesan)
                }

                startActivity(intent)
                finish()
            }

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

}