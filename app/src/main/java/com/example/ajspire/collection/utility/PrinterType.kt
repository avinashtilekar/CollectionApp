package com.example.ajspire.collection.utility

sealed class PrinterType {
    object VriddhiDefault : UserPrinters() {
        override val printerName: String = "VriddhiDefault"
    }

    object VriddhiExternal : UserPrinters() {
        override val printerName: String = "VriddhiExternal"
    }

    object ThermalExternal : UserPrinters() {
        override val printerName: String = "ThermalExternal"
    }
}

sealed class UserPrinters {
    abstract val printerName: String
}