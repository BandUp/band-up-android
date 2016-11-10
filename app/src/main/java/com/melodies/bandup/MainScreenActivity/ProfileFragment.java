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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.LocaleSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.SoundCloudFragments.SoundCloudLoginFragment;
import com.melodies.bandup.SoundCloudFragments.SoundCloudSelectorFragment;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.locale.LocaleRules;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private TextView txtName;
    private TextView txtInstrumentsTitle;
    private TextView txtGenresTitle;
    private TextView txtAge;
    private TextView txtFavorite;
    private TextView txtAboutMe;
    private TextView txtInstrumentsList;
    private TextView txtGenresList;
    private ImageView ivUserProfileImage;
    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;
    private AdView    mAdView;
    final int CAMERA_REQUEST = 555;
    final int GALLERY_REQUEST = 666;
    final int REQUEST_TIMEOUT = 120000;
    final int REQUEST_RETRY = 0;
    final int REQUEST_TAKE_PICTURE = 200;
    final int REQUEST_READ_GALLERY = 300;
    ProgressDialog imageDownloadDialog;
    MyThread myThread;
    User currentUser;

    private Fragment soundCloudSelectorFragment;
    private LinearLayout soundCloudArea;

    private void initializeViews(View rootView) {
        txtName             = (TextView)    rootView.findViewById(R.id.txtName);
        txtAge              = (TextView)    rootView.findViewById(R.id.txtAge);
        txtFavorite         = (TextView)    rootView.findViewById(R.id.txtFavorite);
        txtAboutMe          = (TextView)    rootView.findViewById(R.id.txtAboutMe);
        txtInstrumentsTitle = (TextView)    rootView.findViewById(R.id.txtInstrumentTitle);
        txtGenresTitle      = (TextView)    rootView.findViewById(R.id.txtGenresTitle);
        txtInstrumentsList  = (TextView)    rootView.findViewById(R.id.txtInstrumentsList);
        txtGenresList       = (TextView)    rootView.findViewById(R.id.txtGenresList);
        ivUserProfileImage  = (ImageView)   rootView.findViewById(R.id.imgProfile);
        mAdView             = (AdView)      rootView.findViewById(R.id.adView);
        soundCloudArea      = (LinearLayout)rootView.findViewById(R.id.profile_soundCloudArea);
    }

    private void setFonts() {
        txtName            .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtAge             .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtFavorite        .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtAboutMe         .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtInstrumentsTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams_bold.ttf"));
        txtGenresTitle     .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams_bold.ttf"));
        txtInstrumentsList .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtGenresList      .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
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
        myThread = new MyThread();
        myThread.start();
    }

    private void createSoundCloudArea() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        soundCloudArea.setId(new Integer(1234));
        Fragment soundCloudFragment;
        if (currentUser.soundCloudId == 0){
            soundCloudFragment = SoundCloudLoginFragment.newInstance();
        }else {
            soundCloudFragment = SoundCloudSelectorFragment.newInstance(currentUser.soundCloudId,
                                                                        currentUser.soundCloudURL);
        }

        ft.add(soundCloudArea.getId(), soundCloudFragment, "soundCloudFragment");
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(rootView);
        setFonts();

        return rootView;
    }

    /**
     * Displays the user on the profile fragment
     * @param u the user that should be displayed.
     */
    private void displayUser(User u) {
        // Adding ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        LocaleRules localeRules = LocaleSingleton.getInstance(getActivity()).getLocaleRules();

        if (u.imgURL != null) {
            Picasso.with(getActivity()).load(u.imgURL).into(ivUserProfileImage);
        }

        txtName.setText(u.name);
        if (localeRules != null) {
            Integer age = u.ageCalc();
            if (age != null) {
                if (localeRules.ageIsPlural(age)) {
                    String ageString = String.format("%s %s", age, getString((R.string.age_year_plural)));
                    txtAge.setText(ageString);
                } else {
                    String ageString = String.format("%s %s", age, getString((R.string.age_year_singular)));
                    txtAge.setText(ageString);
                }
            }
        }
        //txtFavorite.setText("Drums");
        txtAboutMe.setText(u.aboutme);

        for (int i = 0; i < u.genres.size(); i++) {
            txtGenresList.append(u.genres.get(i) + "\n");
        }

        for (int i = 0; i < u.instruments.size(); i++) {
            txtInstrumentsList.append(u.instruments.get(i) + "\n");
        }
        createSoundCloudArea();
    }

    /**
     * Opens a ProgressDialog to display that upload is in progress.
     * @param title
     * @param message
     */
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

    /**
     * When the user has granted or denied us permissions to the camera,
     * this function will be called.
     * @param requestCode Arbitrary value we chose when making the request. Just to be sure we are getting the right request.
     * @param permissions The name of the permissions we were requestingþ
     * @param grantResults The results of the permission request.
     */
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

    /**
     * When the user has selected an image,
     * this function will be called.
     * @param requestCode Arbitrary value we chose when making the request. Just to be sure we are getting the right request.
     * @param resultCode The result of the request.
     * @param data The intent data. Contains the URI of the image.
     */
    public void onImageSelectResult(int requestCode, int resultCode, Intent data) {

        String url = getResources().getString(R.string.api_address).concat("/profile-picture");
        if (resultCode == RESULT_OK) {
            displayDownloadMessage("Uploading Photo", "Please wait...");
            if (requestCode == CAMERA_REQUEST) {
                sendImageToServer(cameraPhoto.getPhotoPath(), false);
            }

            if (requestCode == GALLERY_REQUEST) {
                // Get the URI from the intent result.
                sendMessage(data);

            }
        }
    }

    /**
     * Sends the image at the path 'path' to the server.
     * shouldDeleteAfterwards should be true if we downloaded the image from somewhere else
     * and it was not in the camera roll, so we do not clutter the device's camera roll.
     * @param path the path to the image on the SD card
     * @param shouldDeleteAfterwards Should we delete the image after uploading it to the server?
     */
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
                        String a = validateJSON(urlResponse);
                        if (a == null) {
                            Picasso.with(getActivity()).load(urlResponse).into(ivUserProfileImage);
                        } else {
                            Picasso.with(getActivity()).load(R.drawable.ic_profile_picture_placeholder).into(ivUserProfileImage);
                        }

                        imageDownloadDialog.dismiss();

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

    /**
     * Opens an AlertDialog that prompts the user to take a photo or select a photo.
     * @param view
     */
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

    // Get the User ID of the logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getActivity().getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String id = srdPref.getString("userID", DEFAULT);
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

        DatabaseSingleton.getInstance(getActivity()).getBandUpDatabase().getUserProfile(user, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                }
                currentUser = new User();
                try {
                    if (!responseObj.isNull("_id")) {
                        currentUser.id = responseObj.getString("_id");
                    }
                    if (!responseObj.isNull("username")) {
                        currentUser.name = responseObj.getString("username");
                    }
                    if (!responseObj.isNull("dateOfBirth")) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        currentUser.dateOfBirth = df.parse(responseObj.getString("dateOfBirth"));
                    }

                    if (!responseObj.isNull("distance")) {
                        currentUser.distance = responseObj.getInt("distance");
                    } else {
                        currentUser.distance = null;
                    }

                    if (!responseObj.isNull("percentage")) {
                        currentUser.percentage = responseObj.getInt("percentage");
                    }

                    if (!responseObj.isNull("genres")) {
                        JSONArray genreArray = responseObj.getJSONArray("genres");
                        for (int i = 0; i < genreArray.length(); i++) {
                            currentUser.genres.add(genreArray.getString(i));
                        }
                    }

                    if (!responseObj.isNull("instruments")) {
                        JSONArray instrumentArray = responseObj.getJSONArray("instruments");
                        for (int i = 0; i < instrumentArray.length(); i++) {
                            currentUser.instruments.add(instrumentArray.getString(i));
                        }
                    }

                    if (!responseObj.isNull("aboutme")) {
                        currentUser.aboutme = responseObj.getString("aboutme");
                    }

                    if (!responseObj.isNull("image")) {
                        JSONObject imageObj = responseObj.getJSONObject("image");

                        if (!imageObj.isNull("url")) {
                            currentUser.imgURL = imageObj.getString("url");
                        }
                    }

                    if (!responseObj.isNull("soundCloudId")){
                        currentUser.soundCloudId = responseObj.getInt("soundCloudId");
                    }
                    displayUser(currentUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                System.out.println("ERROR");
            }
        });
    }

    // when About Me is clicked go to edit view
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

    String validateJSON(String json) {
        String imageURL = null;
        try {
            JSONObject urlObject = new JSONObject(json);
            if (!urlObject.isNull("url")) {
                imageURL = urlObject.getString("url");
            } else {
                Toast.makeText(getActivity(), "Could not parse JSON", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        if (imageURL == null || imageURL.equals("")) {
            return null;
        }
        return imageURL;
    }
}
