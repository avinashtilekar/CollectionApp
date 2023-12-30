package com.example.ajspire.escp_printer_lib.barcode;

import com.example.ajspire.escp_printer_lib.EscPosPrinterCommands;
import com.example.ajspire.escp_printer_lib.EscPosPrinterSize;
import com.example.ajspire.escp_printer_lib.exceptions.EscPosBarcodeException;

public class BarcodeUPCA extends BarcodeNumber {

    public BarcodeUPCA(EscPosPrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws EscPosBarcodeException {
        super(printerSize, EscPosPrinterCommands.BARCODE_TYPE_UPCA, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return 12;
    }
}
