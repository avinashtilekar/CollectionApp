package com.example.ajspire.collection.utility

sealed class PrinterType {
    object VriddhiDefault : Printers() {
        override val printerName: String = "VriddhiDefault"
    }

    object VriddhiExternal : Printers() {
        override val printerName: String = "VriddhiExternal"
    }

    object ThermalExternal : Printers() {
        override val printerName: String = "ThermalExternal"
    }
}

sealed class Printers {
    abstract val printerName: String
}