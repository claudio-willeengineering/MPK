package de.dfki.mpk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import java.io.InputStream;

import de.dfki.mpk.R;

/**
 * Created by Olakunmi on 07/08/2017.
 */

public class UtilsHelpers {
    public static void showErrorDialog(Context activity, String title , String text)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        build.setTitle(title);
        build.setMessage(text);
        build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        build.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static Bitmap TextToImageEncode(String Value, Activity activity) throws WriterException {
        int QRcodeWidth = 1024;
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            int black = ActivityCompat.getColor(activity,R.color.black);
            int white = ActivityCompat.getColor(activity,R.color.white);

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        black:white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 1024, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public static JSONObject fromRawToJson(Context context, int rawResource)
    {
        JSONObject jsonObject = null;
        try {
            InputStream in = context.getResources().openRawResource(rawResource);

            byte[] b = new byte[in.available()];
            in.read(b);
            jsonObject = new JSONObject(new String(b));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
