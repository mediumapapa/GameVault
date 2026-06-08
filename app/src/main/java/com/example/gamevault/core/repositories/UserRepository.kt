package com.example.gamevault.core.repositories

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.models.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository : UserService {
    private val userCollection = FirebaseFirestore.getInstance().collection("users")

    override suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val userData = hashMapOf(
                    "id" to userProfile.id,
                    "firstName" to userProfile.firstName,
                    "lastName" to userProfile.lastName,
                    "phone" to userProfile.phone,
                    "birthDate" to userProfile.birthDate,
                    "profileCompleted" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                userCollection.document(userProfile.id)
                    .set(userData, SetOptions.merge())
                    .await()

                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("No se pudo guardar el perfil: ${e.localizedMessage}")
            }
        }
}
