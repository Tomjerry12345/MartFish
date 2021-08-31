package com.martfish.database

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.martfish.model.ModelProduk
import com.martfish.model.ModelUsers
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreDatabase {

    private val dbFireStore = Firebase.firestore
    private val storage = Firebase.storage

    suspend fun saveDataReference(reference: String, data: Any, responseSucces: String): Response {
        return try {
            val getRef = dbFireStore.collection(reference)
                .add(data)
                .await()

            Response.Success(responseSucces)
            Response.Changed(getRef.id)
            
        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceByQuery(reference: String, query: String, value: Any): Response {
        showLogAssert("value", "$value")
        return try {
            val data = dbFireStore.collection(reference)
                .whereEqualTo(query, value)
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun deleteReferenceCollection1(reference: String, colection: String): Response {
        showLogAssert("value", colection)
        return try {
             dbFireStore.collection(reference)
                .document(colection)
                .delete()
                .await()

            Response.Success("Hapus Data Berhasil")

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun updateReferenceCollection1(reference: String, colection: String, update: String, data: Any): Response {
        showLogAssert("value", colection)
        return try {
            dbFireStore.collection(reference)
                .document(colection)
                .update(update, data)
                .await()

            Response.Success("Update Data Berhasil")

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }



    fun getReferenceByQueryListener(reference: String, query: String, value: Any): LiveData<Response> {
        val data = MutableLiveData<Response>()
        val dbData = dbFireStore.collection(reference)
            .whereEqualTo(query, value)
        dbData.addSnapshotListener { value1, e ->
            if (e != null) {
                showLogAssert("Listen failed.", "${e.message}")
                data.value = Response.Error("${e.message}")
                return@addSnapshotListener
            }

            data.value = value1?.let { Response.Changed(it) }

        }

        return data

    }

    suspend fun getAllDataRefeference(reference: String): Response {
        return try {
            val data = dbFireStore.collection(reference)
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun uploadPhoto(file: Uri, name: String): Response {
        showLogAssert("file", "$file")
        showLogAssert("name", name)

        return try {
            val storageRef = storage.reference
            val fileRef = storageRef.child("images/produk/${file.lastPathSegment}")
            val urlTask = fileRef.putFile(file)

            val response = urlTask.await().storage.downloadUrl.await().toString()

            Response.Changed(response)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }

    }
}