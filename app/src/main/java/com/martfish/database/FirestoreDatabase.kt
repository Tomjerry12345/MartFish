package com.martfish.database

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.tasks.await

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

    suspend fun saveDataReferenceDocumentOne(
        collection: String,
        collection1: String,
        document: String,
        data: Any
    ): Response {
        return try {
            val getRef = dbFireStore
                .collection(collection)
                .document(document)
                .collection(collection1)
                .add(data)
                .await()

            Response.Success("")
            Response.Changed(getRef.id)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun saveDataReferenceDocumentThree(
        collection: String,
        collection1: String,
        document: String,
        document1: String,
        document2: String,
        data: Any
    ): Response {
        return try {
            dbFireStore
                .collection(collection)
                .document(document)
                .collection(document1)
                .document(document2)
                .collection(collection1)
                .add(data)
                .await()

            Response.Success("")

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceDocumentOneCollection(
        reference: String,
        document: String
    ): Response {
        return try {
            val data = dbFireStore
                .collection(reference)
                .document(document)
                .collection("komentar")
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceDocumentOne(
        reference: String,
        document: String
    ): Response {
        return try {
            val data = dbFireStore
                .collection(reference)
                .document(document)
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceDocumentThree(
        reference: String,
        document: String,
        document1: String,
        document2: String
    ): Response {
        return try {
            val data = dbFireStore.collection(reference)
                .document(document)
                .collection(document1)
                .document(document2)
                .collection("messages")
                .orderBy("timeStamp")
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceByQuery(reference: String, query: String, value: Any): Response {
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

    suspend fun getReferenceByQueryOrderBy(reference: String, query: String, value: Any, field: String): Response {
        return try {
            val data = dbFireStore.collection(reference)
                .whereEqualTo(query, value)
                .orderBy(field)
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceByTwoQuery(
        reference: String,
        query: String,
        value: Any,
        query1: String,
        value1: Any?
    ): Response {
        return try {
            val data = dbFireStore.collection(reference)
                .whereEqualTo(query, value)
                .whereEqualTo(query1, value1)
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceByTwoQueryThreeValue(
        reference: String,
        query: String,
        value: Any,
        query1: String,
        value1: Any,
        value2: Any?
    ): Response {
        return try {
            val data = dbFireStore.collection(reference)
                .whereEqualTo(query, value)
                .whereIn(query1, listOf(value1, value2))
                .get()
                .await()

            Response.Changed(data)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun deleteReferenceCollectionOne(reference: String, colection: String): Response {
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

    suspend fun updateReferenceCollectionOne(
        reference: String,
        colection: String,
        update: String?,
        data: Any
    ): Response {
        return try {
            if (update != null) {
                dbFireStore.collection(reference)
                    .document(colection)
                    .update(update, data)
                    .await()
            } else {
                dbFireStore.collection(reference)
                    .document(colection)
                    .set(data)
                    .await()
            }

            Response.Success("Update Data Berhasil")

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun updateReferenceCollectionTwo(
        colection: String,
        colection1: String,
        document: String,
        update: String?,
        data: Any
    ): Response {
        return try {
            if (update != null) {
                dbFireStore.collection(colection)
                    .document(document)
                    .collection(colection1)
                    .document(data as String)
                    .set(update, data as SetOptions)
                    .await()
            }

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

    suspend fun uploadPhoto(file: Uri, path: String): Response {
        return try {
            val storageRef = storage.reference
            val fileRef = storageRef.child("$path${file.lastPathSegment}")
            val urlTask = fileRef.putFile(file)

            val response = urlTask.await().storage.downloadUrl.await().toString()

            Response.Changed(response)

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }

    }
}