package com.example.foundya.ui.auth
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundya.data.use_case.LoginUseCase
import com.example.foundya.data.use_case.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email)
            }
            is AuthEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password)
            }
            is AuthEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(name = event.name)
            }
            is AuthEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPassword = event.confirmPassword)
            }
            is AuthEvent.Login -> login()
            is AuthEvent.Register -> register()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loginUseCase(_uiState.value.email, _uiState.value.password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ну слушай, ваще не понятно чет"
                    )
                }
        }
    }

    private fun register() {
        if (validateRegisterForm()) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                registerUseCase(
                    email = _uiState.value.email,
                    password = _uiState.value.password,
                    name = _uiState.value.name
                ).onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Не получилось зарегистрироваться"
                    )
                }
            }
        }
    }

    private fun validateRegisterForm(): Boolean {
        val nameValid = _uiState.value.name.isNotBlank()
        val passwordsMatch = _uiState.value.password == _uiState.value.confirmPassword

        _uiState.value = _uiState.value.copy(
            nameError = if (!nameValid) "Необходимо ввести имя" else null,
            confirmPasswordError = if (!passwordsMatch) "Пароли не совпадают" else null
        )

        return nameValid && passwordsMatch
    }

}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    val name: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val confirmPasswordError: String? = null
)

sealed class AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class NameChanged(val name: String) : AuthEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : AuthEvent()
    object Login : AuthEvent()
    object Register : AuthEvent()
}