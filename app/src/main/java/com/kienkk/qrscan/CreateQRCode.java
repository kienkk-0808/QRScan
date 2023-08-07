package com.kienkk.qrscan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.BarcodeUtils;
import com.google.zxing.BarcodeFormat;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateQRCode extends AppCompatActivity {

    EditText content;
    Button encode,save,back;
    ImageView qrcode;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qrcode);

        init();

    }

    private void init(){
        content = findViewById(R.id.content);
        encode = findViewById(R.id.encode);
        save = findViewById(R.id.save);
        back = findViewById(R.id.quaylai);
        qrcode = findViewById(R.id.qrcode);

        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = BarcodeUtils.encodeBitmap(content.getText().toString(), BarcodeFormat.QR_CODE,500,500);
                qrcode.setImageBitmap(bitmap);
                hideKeyboard(encode);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap == null){
                    Toast.makeText(getApplicationContext(),"lỗi",Toast.LENGTH_SHORT).show();
                }else {
                    String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    Uri imageUri = saveBitmapToStorage(bitmap, currentTime + ".png");
                    finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Uri saveBitmapToStorage(Bitmap bitmap, String fileName) {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        // Tạo URI mới để lưu hình ảnh vào bộ nhớ hình ảnh của ứng dụng
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            // Mở OutputStream để lưu dữ liệu hình ảnh vào URI
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUri;
    }
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}