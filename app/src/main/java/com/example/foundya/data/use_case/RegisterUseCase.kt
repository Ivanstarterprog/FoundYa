package com.example.foundya.data.use_case

import com.example.foundya.data.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String) =
        repository.signUpWithEmail(email, password, name)
}