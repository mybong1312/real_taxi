package mobileapp.taxiproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isTaxi = false;
    ImageView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button emergency = (Button) findViewById(R.id.emergency);
        TextView textResult = (TextView) findViewById(R.id.resultText);
        resultView = (ImageView) findViewById(R.id.resultView);

        Intent intent = getIntent();
        String cameraPath = intent.getStringExtra("cameraPath");
        String galleryPath = intent.getStringExtra("galleryPath");

        String path="";
        if (galleryPath != null && cameraPath == null) {
            path = galleryPath;
        } else if (galleryPath == null && cameraPath != null) {
            path = cameraPath;
        }


        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=2;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap, exifDegree);
            resultView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        emergency.setOnClickListener(this);

        if(isTaxi==true){
            emergency.setVisibility(View.INVISIBLE);
            textResult.setText("택시가 맞습니다!");
        }
        else{
            emergency.setVisibility(View.VISIBLE);
            textResult.setText("헉!택시가 아닙니다! 조심하세요!");
        }
    }

    public void onClick(View v){
        switch (v.getId()){

            case R.id.emergency:
                startActivity(new Intent("android.intent.action.DIAL",
                        Uri.parse("tel:112")));
                break;
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }
}