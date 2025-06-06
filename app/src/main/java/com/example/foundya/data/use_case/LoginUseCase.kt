package com.example.foundya.data.use_case

import com.example.foundya.data.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        repository.signInWithEmail(email, password)
}