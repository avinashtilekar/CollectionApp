package com.example.ajspire.escp_printer_lib.textparser;

import com.example.ajspire.escp_printer_lib.EscPosPrinterCommands;
import com.example.ajspire.escp_printer_lib.exceptions.EscPosConnectionException;
import com.example.ajspire.escp_printer_lib.exceptions.EscPosEncodingException;

public interface IPrinterTextParserElement {
    int length() throws EscPosEncodingException;
    IPrinterTextParserElement print(EscPosPrinterCommands printerSocket) throws EscPosEncodingException, EscPosConnectionException;
}
