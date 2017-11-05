package cg.yunbee.cn.wangyoujar.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class QRCodeUtil {
    
    public static boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {
        try {
            if (content == null || "".equals(content)) {
                return false;
            }

            //閰嶇疆鍙傛暟
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //瀹归敊绾у埆
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //璁剧疆绌虹櫧杈硅窛鐨勫搴�
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

            // 鍥惧儚鏁版嵁杞崲锛屼娇鐢ㄤ簡鐭╅樀杞崲
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 涓嬮潰杩欓噷鎸夌収浜岀淮鐮佺殑绠楁硶锛岄�愪釜鐢熸垚浜岀淮鐮佺殑鍥剧墖锛�
            // 涓や釜for寰幆鏄浘鐗囨í鍒楁壂鎻忕殑缁撴灉
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 鐢熸垚浜岀淮鐮佸浘鐗囩殑鏍煎紡锛屼娇鐢ˋRGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            Log.i("INFO", "createQRImage end!");
            //蹇呴』浣跨敤compress鏂规硶灏哹itmap淇濆瓨鍒版枃浠朵腑鍐嶈繘琛岃鍙栥�傜洿鎺ヨ繑鍥炵殑bitmap鏄病鏈変换浣曞帇缂╃殑锛屽唴瀛樻秷鑰楀法澶э紒
            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
        } catch (Exception e) {
          Log.i("INFO", "createQRImage: ex->" + e.toString());
        }

        return false;
    }

   
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //鑾峰彇鍥剧墖鐨勫楂�
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo澶у皬涓轰簩缁寸爜鏁翠綋澶у皬鐨�1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

}
