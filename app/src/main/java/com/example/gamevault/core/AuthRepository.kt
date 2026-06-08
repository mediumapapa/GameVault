package com.example.gamevault.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository : Authentication {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun requestLogin(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it) }
                ?: ResponseService.Error("Usuario no encontrado")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            ResponseService.Error("Correo o contrasena incorrectos")
        } catch (e: FirebaseAuthException) {
            ResponseService.Error(e.localizedMessage ?: "Error de autenticacion")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado. Intenta de nuevo")
        }
    }

    override suspend fun requestSignUp(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                val userData = hashMapOf(
                    "email" to email,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("users").document(user.uid).set(userData).await()
                ResponseService.Success(user)
            } else {
                ResponseService.Error("No se pudo crear el usuario")
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            ResponseService.Error("Este correo ya esta registrado, intenta con otro")
        } catch (e: FirebaseAuthWeakPasswordException) {
            ResponseService.Error("La contrasena es muy debil")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            ResponseService.Error("Correo invalido")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}
