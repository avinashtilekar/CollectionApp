package com.example.ajspire.collection.utility.bt_printer.async;

import android.content.Context;

public class AsyncUsbEscPosPrint extends AsyncEscPosPrint {
    public AsyncUsbEscPosPrint(Context context) {
        super(context);
    }

    public AsyncUsbEscPosPrint(Context context, OnPrintFinished onPrintFinished) {
        super(context, onPrintFinished);
    }
}
