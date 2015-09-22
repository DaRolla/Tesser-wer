
package android.adesso_mobile.de.tesser_wer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity {

    public static final String TAG = "Tesser-wer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opticalCharacterRecognition();
            }
        });
    }

    protected void opticalCharacterRecognition() {
        final String path = Environment.getExternalStorageDirectory() + "/tesseract-ocr";
        final Bitmap bitmap = BitmapFactory.decodeFile(path + "/arbeitsunfaehigkeit.hausarzt.exp0.jpg");

        final TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);

        // https://github.com/rmtheis/tess-two/issues/109
        tessBaseAPI.init(path, "deu");

        // PSM_SINGLE_BLOCK is default in tess-two
        // PSM_AUTO is default in Tesseract (http://tesseract-ocr.googlecode.com/git/doc/tesseract.1.html)
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);

        tessBaseAPI.setImage(bitmap);
        final String ocr = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();

        Log.v(TAG, ocr);
        dump(ocr);

        final EditText editText = (EditText) findViewById(R.id.editText1);
        editText.setText(ocr);
    }

    public void dump(final String content) {
        try {
            final String path = Environment.getExternalStorageDirectory() + "/tesseract-ocr";
            final File file = new File(path + "ocr.txt");
            final FileWriter writer = new FileWriter(file);
            writer.append(content);
            writer.flush();
            writer.close();
            Log.v(TAG, "text dumped into file ocr.txt");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                MediaScannerConnection.scanFile(MainActivity.this, new String[] {
                        file.toString()
                }, null, null);
            }

        } catch (IOException e) {
            Log.w(TAG,
                    "text NOT dumped into file ocr.txt. Please make sure that the android.permission.WRITE_EXTERNAL_STORAGE is enabled in the AndroidManifest.xml");
        }
    }

}
