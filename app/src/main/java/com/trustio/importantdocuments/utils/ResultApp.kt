package com.trustio.importantdocuments.utils

sealed class ResultApp {
    data class Success(val isAuthenticated: Boolean) : ResultApp()
    data class Error(val exception: Exception) : ResultApp()
    data  class CodeSent(val code: String) : ResultApp()
}
