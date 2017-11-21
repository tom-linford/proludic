package icn.proludic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import icn.proludic.misc.CircleTransform;

import static icn.proludic.misc.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static icn.proludic.misc.Constants.NO_PICTURE;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;

/**
 * Author:  Bradley Wilson
 * Date: 27/07/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class GalleryIntentActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SELECT_FILE = 420;
    private ImageView profilePicture;
    private String picturePath;
    private String url;
    boolean url_set;
    private Context context = this;

    public void showGalleryIntent() {
        if (checkPermission(this)) {
            galleryIntent();
        }
    }

    public void setProfileImage(ImageView profileImage) {
        Log.e("debug", "setProfileImage");
        this.profilePicture = profileImage;
        profilePicture.setTag(R.drawable.no_profile);
        profilePicture.setOnClickListener(this);
    }

    public ImageView getProfileImage() {
        return profilePicture;
    }

    public void galleryIntent() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, SELECT_FILE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permission_necessary);
                    alertBuilder.setMessage(R.string.storage_camera_permissions);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("debug", "onActivityResult");
        if (requestCode == SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.e("inside gallery", "it's here");
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                if (cursor != null) {
                    Log.e("cursor!null", "");
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }
                // Load the selected image into a preview
                Bitmap bitmap = getScaledBitmap(picturePath, profilePicture.getWidth(), profilePicture.getHeight());
                //profilePicture.setImageBitmap(bitmap);
                Picasso.with(context).load(selectedImage).transform(new CircleTransform()).into(profilePicture);
                profilePicture.setTag(420);
                saveImage(bitmap);
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        url_set = false;
        Log.e("debug","saveImage called");
        // Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
        Log.e("debug","byte count: " + Integer.toString(bitmap.getByteCount()));
        if (bitmap == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            Log.e("debug", "byte array: " + image);
            // Create the ParseFile
            final ParseFile file = new ParseFile("image.png", image);
            Log.e("debug", "file toString: " + file.toString());
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e("debug", "saveInBackground");
                        // Log.e("debug", e.getLocalizedMessage());
                        String url = file.getUrl();
                        Log.e("debug", "url: " + url);
                        setUrl(url);
                        url_set = true;
                        if (ParseUser.getCurrentUser() != null) {
                            if (url != null) {
                                ParseUser.getCurrentUser().put(USER_PROFILE_PICTURE, url);
                            } else {
                                ParseUser.getCurrentUser().put(USER_PROFILE_PICTURE, NO_PICTURE);
                            }
                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Toast.makeText(GalleryIntentActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("failed", "failed" + e.getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("failed", "failed" + e.getLocalizedMessage());
                    }
                }
            });
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);
        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);
        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View view) {
        showGalleryIntent();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
