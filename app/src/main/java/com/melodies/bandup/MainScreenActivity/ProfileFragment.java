package com.melodies.bandup.MainScreenActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String DEFAULT = "N/A";

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
    private int getProgressMaxValue = 200;   // Max X Km radius
    ProgressDialog imageDownloadDialog;
    MyThread myThread;
    com.melodies.bandup.MainScreenActivity.ImageLoader imageLoader;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public ProfileFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        userRequest();
        cameraPhoto = new CameraPhoto(getActivity());
        galleryPhoto = new GalleryPhoto(getActivity());
        imageLoader = new ImageLoader(getActivity());
        myThread = new MyThread();
        myThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        txtName            = (TextView) rootView.findViewById(R.id.txtName);
        txtInstruments     = (TextView) rootView.findViewById(R.id.txtInstruments);
        txtGenres          = (TextView) rootView.findViewById(R.id.txtGenres);
        txtStatus          = (TextView) rootView.findViewById(R.id.txtStatus);
        txtFanStar         = (TextView) rootView.findViewById(R.id.txtFanStar);
        txtPercentage      = (TextView) rootView.findViewById(R.id.txtPercentage);
        txtAboutMe         = (TextView) rootView.findViewById(R.id.txtAboutMe);
        txtSeekValue       = (TextView) rootView.findViewById(R.id.txtSeekValue);
        txtPromotion       = (TextView) rootView.findViewById(R.id.txtPromotion);
        ivUserProfileImage = (ImageView) rootView.findViewById(R.id.imgProfile);
        seekBarRadius  = (SeekBar) rootView.findViewById(R.id.seekBarRadius);
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
        return rootView;
    }

    public void displayDownloadMessage(final String title, final String message) {
        if (imageDownloadDialog == null) {
            imageDownloadDialog = ProgressDialog.show(getActivity(), title, message, true, false);
        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        imageDownloadDialog.dismiss();
                        imageDownloadDialog.setTitle(title);
                        imageDownloadDialog.setMessage(message);
                        imageDownloadDialog.show();
                    }
                });
            }


        }
    }

    public void sendMessage(final Intent data) {
        System.out.println(myThread.handler);
        myThread.handler.post(new Runnable() {
            @Override
            public void run() {
                Uri uri = data.getData();

                // Create a new input stream for the photo to be stored.
                InputStream inputStream = null;
                String photoPath = null;
                if (uri.getAuthority() != null) {
                    try {
                        inputStream = getActivity().getContentResolver().openInputStream(uri);
                        Bitmap bmpImage = BitmapFactory.decodeStream(inputStream);
                        ContentResolver contentResolver = getActivity().getContentResolver();
                        String path = MediaStore.Images.Media.insertImage(contentResolver, bmpImage, "ImageToUpload", null);

                        galleryPhoto.setPhotoUri(Uri.parse(path));

                        photoPath = galleryPhoto.getPath();

                        if (photoPath != null) {
                            sendImageToServer(galleryPhoto.getPath(), true);
                        } else {

                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {


                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    class MyThread extends Thread {
        Handler handler;
        public MyThread() {

        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    Toast.makeText(getActivity(), R.string.user_allow_camera, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_GALLERY:
                if (allGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getActivity(), R.string.user_allow_storage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onImageSelectResult(int requestCode, int resultCode, Intent data) {

        String url = getResources().getString(R.string.api_address).concat("/profile-picture");
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                displayDownloadMessage("Uploading Photo", "Please wait...");
                sendImageToServer(cameraPhoto.getPhotoPath(), false);
            }

            if (requestCode == GALLERY_REQUEST) {
                // Get the URI from the intent result.
                displayDownloadMessage("Uploading Photo", "Please wait...");
                sendMessage(data);

            }
        }
    }

    public void sendImageToServer(String path, final Boolean shouldDeleteAfterwards) {
        final File image = new File(path);
        String url = getResources().getString(R.string.api_address).concat("/profile-picture");
        final MultipartRequest multipartRequest = new MultipartRequest(url, image, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String urlResponse) {
                        if (imageDownloadDialog != null) {
                            imageDownloadDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), R.string.user_image_success, Toast.LENGTH_SHORT).show();
                        imageLoader.getProfilePhoto(urlResponse, ivUserProfileImage, imageDownloadDialog);
                        if (shouldDeleteAfterwards) {
                            if (image.delete()) {
                                System.out.println("FILE DELETION SUCCEEDED");
                            } else {
                                System.out.println("FILE DELETION FAILED");
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (imageDownloadDialog != null) {
                            imageDownloadDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), R.string.user_image_error, Toast.LENGTH_SHORT).show();
                        VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);
                    }
                }
        );
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT,
                REQUEST_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);

        }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Boolean checkPermissions(String[] permissions, int requestCode) {
        Boolean hasAllPermissions = true;
        List<String> perms = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissions[i]) == PackageManager.PERMISSION_DENIED) {
                perms.add(permissions[i]);
                hasAllPermissions = false;
            }

        }

        if (!hasAllPermissions) {
            String[] permArray = new String[perms.size()];
            permArray = perms.toArray(permArray);

            ActivityCompat.requestPermissions(getActivity(), permArray, requestCode);
        }

        return hasAllPermissions;
    }

    public void onClickDisplayModal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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



    // Get the userid of logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getActivity().getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        String response = srdPref.getString("response", DEFAULT);
        JSONObject obj = new JSONObject(response);
        String id = obj.get("userID").toString();
        return (!id.equals(DEFAULT)) ? id : "No data Found";
    }

    // Request REAL user info from server
    public void userRequest() {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.api_address).concat("/get-user");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            // Binding View to real data
                            try {
                                if (!response.isNull("username")) {
                                    txtName.setText(response.getString("username"));
                                }
                                //txtInstruments.setText(response.getString("instruments"));    // need some work
                                //txtGenres.setText(response.getString("genres"));              // need some work
                                txtFanStar.setText("Bob Marley");
                                txtStatus.setText("Searching for band");        // need to create list of available options to choose
                                txtPercentage.setText("45%");                   // needs match % value

                                if (!response.isNull("aboutme")) {
                                    txtAboutMe.setText(response.getString("aboutme"));  // done
                                }

                                if (!response.isNull("image")) {
                                    imageLoader.getProfilePhoto(response.getJSONObject("image").toString(), ivUserProfileImage);
                                } else {
                                    // TODO: Could not get image, display the placeholder.
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Bad response: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public void onClickAboutMe (View view) {
        Intent aboutMeIntent = new Intent(getActivity(), UpdateAboutMe.class);
        startActivityForResult(aboutMeIntent, 2);
    }



    // All onActivityResults are handled by the activity.
    // The onActivityResult function in MainScreenActivity calls this function.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (data != null) {
                String message = data.getStringExtra("MESSAGE");
                txtAboutMe.setText(message);
            }
        }
    }

    public void setAbout(String a) {
        txtAboutMe.setText(a);
    }

    public void openGallery() {
        getActivity().startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
    }

    public void openCamera() {
        try {
            getActivity().startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            cameraPhoto.addToGallery();
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.user_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
