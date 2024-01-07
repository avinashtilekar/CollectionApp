package com.example.ajspire.collection.utility

sealed class ToastTypeFields {
    object Error : ToastType() {
        override val name: String = "Banana"
    }
    object Warning : ToastType() {
        override val name: String = "Warning"
    }
    object Success : ToastType() {
        override val name: String = "Success"
    }
}

sealed class ToastType {
    abstract val name: String
}