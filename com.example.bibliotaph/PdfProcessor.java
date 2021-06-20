package com.example.bibliotaph;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PdfProcessor {
    public static String extractPdfName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String pdfName = cursor.getString(nameIndex);
        cursor.close();
        return pdfName.substring(0, pdfName.lastIndexOf("."));
    }

    public static String extractTextPdfFile(Context context, Uri uri) {
        InputStream inputStream = null;
        StringBuilder fileContent = new StringBuilder();
        PdfReader reader;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            assert inputStream != null;
            reader = new PdfReader(inputStream);

            int pages = reader.getNumberOfPages();

            for(int i=1; i<=pages; i++) {
                fileContent.append(PdfTextExtractor.getTextFromPage(reader, i).trim()).append("\n");
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }
}
