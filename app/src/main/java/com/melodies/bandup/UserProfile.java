package com.melodies.bandup;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.melodies.bandup.MainScreenActivity.MultipartRequest;
import com.melodies.bandup.MainScreenActivity.UserListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    UserListController ulc = new UserListController();
    private TextView txtName;
    private TextView txtInstruments;
    private TextView txtGenres;
    private TextView txtStatus;
    private TextView txtFanStar;
    private TextView txtPercentage;
    private TextView txtAboutMe;
    private TextView txtPromotion;
    private ImageView ivUserProfileImage;
    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;
    final int CAMERA_REQUEST = 555;
    final int GALLERY_REQUEST = 666;
    final int REQUEST_TIMEOUT = 120000;
    final int REQUEST_RETRY = 0;
    final int REQUEST_TAKE_PICTURE = 200;
    final int REQUEST_READ_GALLERY = 300;
    private SeekBar seekBarRadius;
    private TextView txtSeekValue;  // displaying searching value
    private int progressMinValue = 1;       // Min 1 Km radius
    private int getProgressMaxValue = 25;   // Max 25 Km radius
    ProgressDialog imageDownloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtName        = (TextView) findViewById(R.id.txtName);
        txtInstruments = (TextView) findViewById(R.id.txtInstruments);
        txtGenres      = (TextView) findViewById(R.id.txtGenres);
        txtStatus      = (TextView) findViewById(R.id.txtStatus);
        txtFanStar     = (TextView) findViewById(R.id.txtFanStar);
        txtPercentage  = (TextView) findViewById(R.id.txtPercentage);
        txtAboutMe     = (TextView) findViewById(R.id.txtAboutMe);
        txtSeekValue   = (TextView) findViewById(R.id.txtSeekValue);
        txtPromotion   = (TextView) findViewById(R.id.txtPromotion);
        ivUserProfileImage = (ImageView) findViewById(R.id.imgProfile);

        cameraPhoto = new CameraPhoto(this);
        galleryPhoto = new GalleryPhoto(this);

        // TODO: Access Real Data from Server/DB

        // dumm data
        txtName.setText("Jón Forseti");
        txtInstruments.setText("Bass, Guitar, Drums");
        txtGenres.setText("Rock, Jazz, Hip Hop");
        txtFanStar.setText("Bob Marley");
        txtStatus.setText("Searching for band");
        txtPercentage.setText("45%");
        txtAboutMe.setText("About Me...Lorem ipsum dolor sit amet, eius aliquid qui no. Ei viris pertinax convenire vel");


        seekBarRadius  = (SeekBar)findViewById(R.id.seekBarRadius);
        seekBarRadius.setMax(getProgressMaxValue);
        seekBarRadius.setProgress(progressMinValue);
        txtSeekValue.setText(progressMinValue + " km");

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressMinValue = i;
                txtSeekValue.setText(progressMinValue + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // TODO: Get the user.

        UserListController.User u = new UserListController.User();
        // Get and display the user profile image
        final ImageView iv = (ImageView) findViewById(R.id.imgProfile);
        ImageLoader il = VolleySingleton.getInstance(UserProfile.this).getImageLoader();
        iv.setImageResource(R.color.transparent);
        if (u.imgURL != null && !u.imgURL.equals("")) {
            il.get(u.imgURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    final Bitmap b = response.getBitmap();
                    if (b != null) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(b);
                            }
                        };
                        runOnUiThread(r);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleySingleton.getInstance(UserProfile.this).checkCauseOfError(UserProfile.this, error);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String url = getResources().getString(R.string.api_address).concat("/profile-picture");
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                sendImageToServer(url, cameraPhoto.getPhotoPath());
            }

            if (requestCode == GALLERY_REQUEST) {
                // Get the URI from the intent result.
                Uri uri = data.getData();

                // Create a new input stream for the photo to be stored.
                InputStream inputStream = null;

                if (uri.getAuthority() != null) {
                    try {
                        inputStream = this.getContentResolver().openInputStream(uri);
                        Bitmap bmpImage = BitmapFactory.decodeStream(inputStream);
                        ContentResolver contentResolver = UserProfile.this.getContentResolver();
                        String path = MediaStore.Images.Media.insertImage(contentResolver, bmpImage, "ImageToUpload", null);

                        galleryPhoto.setPhotoUri(Uri.parse(path));

                        String photoPath = galleryPhoto.getPath();

                        if (photoPath != null) {
                            sendImageToServer(url, galleryPhoto.getPath());
                        } else {

                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void sendImageToServer(String url, String path) {
        File image = new File(path);
        MultipartRequest multipartRequest = new MultipartRequest(url, image, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String urlResponse) {
                        Toast.makeText(UserProfile.this, R.string.user_image_success, Toast.LENGTH_SHORT).show();
                        ImageLoader il = VolleySingleton.getInstance(UserProfile.this).getImageLoader();
                        ivUserProfileImage.setImageResource(R.color.transparent);
                        String imageURL = null;
                        try {
                            JSONObject urlObject = new JSONObject(urlResponse);
                            if (urlObject != null) {
                                Toast.makeText(UserProfile.this, "Could not parse JSON", Toast.LENGTH_SHORT).show();
                            }
                            if (!urlObject.isNull("url")) imageURL = urlObject.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (imageURL != null && !imageURL.equals("")) {
                            il.get(imageURL, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                    final Bitmap b = response.getBitmap();
                                    if (b != null) {
                                        Runnable r = new Runnable() {
                                            @Override
                                            public void run() {
                                                imageDownloadDialog.dismiss();
                                                ivUserProfileImage.setImageBitmap(b);
                                            }
                                        };
                                        runOnUiThread(r);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    imageDownloadDialog.dismiss();
                                    VolleySingleton.getInstance(UserProfile.this).checkCauseOfError(UserProfile.this, error);
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfile.this, R.string.user_image_error, Toast.LENGTH_SHORT).show();
                        VolleySingleton.getInstance(UserProfile.this).checkCauseOfError(UserProfile.this, error);
                    }
                }
        );
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT,
                REQUEST_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
        imageDownloadDialog = ProgressDialog.show(this, getString(R.string.profile_uploading), getString(R.string.login_progress_description), true, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        Boolean allGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
            }
        }

        switch(requestCode){
            case REQUEST_TAKE_PICTURE:
                if(allGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, R.string.user_allow_camera, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_GALLERY:
                if (allGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, R.string.user_allow_storage, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void openGallery() {
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
    }

    public void openCamera() {
        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            cameraPhoto.addToGallery();
        } catch (IOException e) {
            Toast.makeText(this, R.string.user_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public Boolean checkPermissions(String[] permissions, int requestCode) {
        Boolean hasAllPermissions = true;
        List<String> perms = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                perms.add(permissions[i]);
                hasAllPermissions = false;
            }

        }

        if (!hasAllPermissions) {
            String[] permArray = new String[perms.size()];
            permArray = perms.toArray(permArray);

            ActivityCompat.requestPermissions(this, permArray, requestCode);
        }

        return hasAllPermissions;
    }


    public void onClickDisplayModal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);

        builder.setTitle("New Profile Photo").setItems(R.array.image_res_ids, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (checkPermissions(new String[]{
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }, REQUEST_TAKE_PICTURE)) {
                            openCamera();
                        }
                        break;
                    case 1:
                        if (checkPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }, REQUEST_READ_GALLERY)) {
                            openGallery();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
