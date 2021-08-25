package com.martfish.database

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.martfish.model.ModelUsers
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.tasks.await

class FirestoreDatabase {

    private val db = Firebase.firestore

    suspend fun saveDataReference(reference: String, data: Any): Response {
        return try {
            db.collection(reference)
                .add(data)
                .await()

            Response.Success("Pendaftaran berhasil")
            
        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }

    suspend fun getReferenceByQuery(reference: String, query: String, value: Any): Response {
        return try {
            val data = db.collection(reference)
                .whereEqualTo(query, value)
                .get()
                .await()

            Response.Changed(data.toObjects<ModelUsers>())

        } catch (e: Exception) {
            showLogAssert("error", "${e.message}")
            Response.Error("${e.message}")
        }
    }
}