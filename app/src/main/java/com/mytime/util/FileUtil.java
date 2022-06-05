package com.mytime.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.CompoundButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    public static void writeInFile(String str, String fileName) throws IOException {

        FileOutputStream outputStream = FileUtil.context.openFileOutput(fileName,Context.MODE_APPEND);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(str);
        bufferedWriter.close();
        outputStream.close();
    }
}
