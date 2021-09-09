package com.martfish.model

import com.google.gson.annotations.SerializedName

data class ModelTransaksi(
    @SerializedName("transaction_status")
    val transactionStatus: String
)
