package com.turkeytech.egranja.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.dialog.SimpleDialogFragment;
import com.turkeytech.egranja.fragment.DialogLocationFragment;
import com.turkeytech.egranja.model.Product;
import com.turkeytech.egranja.service.FetchAddressIntentService;
import com.turkeytech.egranja.session.Constants;
import com.turkeytech.egranja.util.NetworkHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.CATEGORIES_NODE;
import static com.turkeytech.egranja.session.Constants.HOME_SCREEN;
import static com.turkeytech.egranja.session.Constants.NAV_PRODUCTS;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;
import static com.turkeytech.egranja.session.Constants.PRODUCT_ID;
import static com.turkeytech.egranja.session.Constants.USERS_NODE;

public class EditProductActivity extends AppCompatActivity implements
        SimpleDialogFragment.SimpleDialogListener, DialogLocationFragment.FragmentDialogLocationListener {

    public static final String PRODUCT_NAME_KEY = "product_name_key";
    public static final String PRODUCT_DESCRIPTION_KEY = "product_description_key";
    public static final String PRODUCT_QUANTITY_KEY = "product_quantity_key";
    public static final String PRODUCT_QUANTITY_UNIT_KEY = "product_quantity_unit_key";
    public static final String PRODUCT_PRICE_KEY = "product_price_key";
    public static final String PRODUCT_CATEGORY_KEY = "product_category_key";
    public static final String PRODUCT_IMAGE1_KEY = "product_image1_key";
    public static final String PRODUCT_IMAGE2_KEY = "product_image2_key";
    public static final String PRODUCT_IMAGE3_KEY = "product_image3_key";
    public static final String PRODUCT_IMAGE4_KEY = "product_image4_key";
    public static final String PRODUCT_AUDIO_KEY = "product_audio_key";
    public static final String PRODUCT_VIDEO_KEY = "product_video_key";
    public static final String PRODUCT_LOCATION_KEY = "product_location_key";

    private static final String TAG = "xix: EditProduct";

    private static final int IMAGE_CAMERA_CODE = 101;
    private static final int VIDEO_CAMERA_CODE = 102;
    private static final int IMAGE_GALLERY_CODE = 103;
    private static final int VIDEO_GALLERY_CODE = 104;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 19;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 20;
    private static final int MY_PERMISSIONS_REQUEST_VIDEO_CAMERA = 21;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 22;

    private static final int MY_NOTIFICATION = 900;

    protected Location mLastLocation;

    @BindView(R.id.editProduct_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.editProduct_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.editProduct_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.editProduct_rootLayout)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.editProduct_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.editProduct_txtProductName)
    TextInputEditText mTextProductName;

    @BindView(R.id.editProduct_txtProductDescription)
    TextInputEditText mTextProductDescription;

    @BindView(R.id.editProduct_txtProductQuantity)
    TextInputEditText mTextProductQuantity;

    @BindView(R.id.editProduct_quantityUnit)
    Spinner mSpinQuantityUnit;

    @BindView(R.id.editProduct_txtPrice)
    TextInputEditText mTextProductPrice;

    @BindView(R.id.editProduct_categories)
    Spinner mSpinCategory;

    @BindView(R.id.editProduct_btnAddImage1)
    ImageButton mImageButton1;

    @BindView(R.id.editProduct_btnAddImage2)
    ImageButton mImageButton2;

    @BindView(R.id.editProduct_btnAddImage3)
    ImageButton mImageButton3;

    @BindView(R.id.editProduct_btnAddImage4)
    ImageButton mImageButton4;

    @BindView(R.id.editProduct_btnRecordAudio)
    Button mButtonRecordAudio;

    @BindView(R.id.editProduct_btnUploadVideo)
    Button mButtonUploadVideo;

    @BindView(R.id.editProduct_btnCurrentLocation)
    Button mButtonLocation;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseProduct;
    private DatabaseReference mDatabaseCategory;

    //Final product detail fields
    private String mProductName;
    private String mDescription;
    private String mQuantity;
    private String mQuantityUnit;
    private double mPrice;
    private String mCategory;
    private String mLocation;

    //Image Values
    private Uri mImageFileUri1;
    private Uri mImageFileUri2;
    private Uri mImageFileUri3;
    private Uri mImageFileUri4;

    //Optional Values
    private String mAudioFilePath;
    private Uri mVideoFileUri;

    //Old product detail fields
    private String mOldProductName;
    private String mOldDescription;
    private String mOldQuantity;
    private String mOldQuantityUnit;
    private double mOldPrice;
    private String mOldCategory;
    private String mOldLocation;

    //Image Values
    private String mImageFile1Url;
    private String mImageFile2Url;
    private String mImageFile3Url;
    private String mImageFile4Url;

    //Optional Values
    private String mOldAudioFile;
    private String mOldVideoFile;

    //Image fields
    private Uri mCurrentPhotoUri;
    private int mCurrentImageButtonId;

    //Audio Recording fields
    private boolean isRecordingAudio;
    private MediaRecorder mMediaRecorder;

    //Audio Preview fields
    private boolean isPaused;
    private MediaPlayer mediaPlayer;

    //Location Values
    private FusedLocationProviderClient mFusedLocationClient;

    private AddressResultReceiver mResultReceiver;
    private Uri[] mImagesList;
    private boolean isUploading;
    private NotificationCompat.Builder mBuilderSuccess;
    private NotificationManager mNotifyMgr;

    // Image Verification Variables
    private boolean isImage1Set;
    private boolean isImage2Set;
    private boolean isImage3Set;
    private boolean isImage4Set;
    private boolean isImage1Database;
    private boolean isImage2Database;
    private boolean isImage3Database;
    private boolean isImage4Database;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        ButterKnife.bind(this);

        mSavedInstanceState = savedInstanceState;
        start(mSavedInstanceState);
    }

    private void start(Bundle savedInstanceState) {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.editProduct_noData).setVisibility(View.GONE);

            if (savedInstanceState != null) {
                updateValuesFromBundle(savedInstanceState);
            }


            final String product_Id = getIntent().getStringExtra(PRODUCT_ID);

            setSupportActionBar(mToolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            initSpinners();

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            mStorage = FirebaseStorage.getInstance().getReference();
            mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child(PRODUCTS_NODE);
            mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child(CATEGORIES_NODE);
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(USERS_NODE).child(mCurrentUser.getUid());

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mResultReceiver = new AddressResultReceiver(new Handler());


            mBuilderSuccess = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Product Upload")
                    .setContentText("Uploading Product");

            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mDatabaseProduct.child(product_Id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Product product = dataSnapshot.getValue(Product.class);

                    assert product != null;

                    mOldProductName = product.getName();
                    mOldDescription = product.getDescription();
                    mOldQuantity = product.getQuantity();
                    mOldQuantityUnit = product.getQuantity();
                    mOldPrice = product.getPrice();
                    mOldCategory = product.getCategory();
                    mOldLocation = product.getLocation();
                    mLocation = mOldLocation;

                    mImageFile1Url = product.getImage1();
                    isImage1Database = true;
                    isImage1Set = true;

                    mImageFile2Url = product.getImage2();
                    isImage2Database = true;
                    isImage2Set = true;

                    mImageFile3Url = product.getImage3();
                    isImage3Database = true;
                    isImage3Set = true;

                    mImageFile4Url = product.getImage4();
                    isImage4Database = true;
                    isImage4Set = true;

                    mOldAudioFile = product.getAudio();
                    mOldVideoFile = product.getVideo();

                    updateUI();
                    updateUiFromDatabse();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            findViewById(R.id.editProduct_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry() {
        start(mSavedInstanceState);
    }

    private void initSpinners() {
        ArrayAdapter<CharSequence> quantityUnitAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.quantity_units,
                android.R.layout.simple_list_item_1
        );

        quantityUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinQuantityUnit.setAdapter(quantityUnitAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_names,
                android.R.layout.simple_list_item_1
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinCategory.setAdapter(categoryAdapter);
    }

    // Image Capturing Section
    @OnClick({
            R.id.editProduct_btnAddImage1,
            R.id.editProduct_btnAddImage2,
            R.id.editProduct_btnAddImage3,
            R.id.editProduct_btnAddImage4
    })
    public void imageButtonClicked(final ImageButton button) {

        AlertDialog.Builder imageDialogBuilder = new AlertDialog.Builder(this);
        imageDialogBuilder.setTitle("Replace Image?");
        imageDialogBuilder.setMessage("Clicking 'Yes' will delete the previous image!");
        imageDialogBuilder.setCancelable(true);
        imageDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (button.getId()) {
                    case R.id.editProduct_btnAddImage1:
                        mImageButton1.setImageResource(R.drawable.ic_add_photo);
                        mImageFile1Url = null;
                        isImage1Database = false;
                        isImage1Set = false;
                        break;
                    case R.id.editProduct_btnAddImage2:
                        mImageButton2.setImageResource(R.drawable.ic_add_photo);
                        mImageFile2Url = null;
                        isImage2Database = false;
                        isImage1Set = false;
                        break;
                    case R.id.editProduct_btnAddImage3:
                        mImageButton3.setImageResource(R.drawable.ic_add_photo);
                        mImageFile3Url = null;
                        isImage3Database = false;
                        isImage1Set = false;
                        break;
                    case R.id.editProduct_btnAddImage4:
                        mImageButton4.setImageResource(R.drawable.ic_add_photo);
                        mImageFile4Url = null;
                        isImage4Database = false;
                        isImage1Set = false;
                        break;
                }

                doImageStuff(button);
            }
        });
        imageDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        imageDialogBuilder.show();
    }

    private void doImageStuff(ImageButton button) {
        mCurrentImageButtonId = button.getId();
        Uri imageUri;

        if ((imageUri = getImageFileUri(mCurrentImageButtonId)) != null) {

            showConfirmImagePopup(imageUri);

        } else {

            showImageDialog();
        }
        Log.i(TAG, "imageButtonClicked: " + button.getId());
    }

    private Uri getImageFileUri(int imageButtonId) {
        switch (imageButtonId) {
            case R.id.editProduct_btnAddImage1:
                return mImageFileUri1;

            case R.id.editProduct_btnAddImage2:
                return mImageFileUri2;

            case R.id.editProduct_btnAddImage3:
                return mImageFileUri3;

            case R.id.editProduct_btnAddImage4:
                return mImageFileUri4;

            default:
                return null;
        }
    }

    private void showImageDialog() {
        int dialogTitle = R.string.showImageDialog_title;
        int dialogOptions = R.array.showImageDialog_dialogOptions;

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);

        imageDialog.setTitle(dialogTitle);
        imageDialog.setItems(dialogOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedOptionPosition) {
                switch (selectedOptionPosition) {
                    case 0:
                        selectImageFromGallery();
                        break;
                    case 1:
                        selectImageFromCamera();
                        break;
                }
            }
        });
        imageDialog.show();
    }

    private void selectImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (galleryIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(galleryIntent, IMAGE_GALLERY_CODE);

        } else {

            Snackbar.make(
                    mRootLayout,
                    R.string.selectImageFromGallery_errorMessage,
                    Snackbar.LENGTH_LONG
            ).show();

        }
    }

    private void selectImageFromCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();

        } else {

            takePicture();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File pictureFile = null;

            try {

                pictureFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (pictureFile != null) {

                mCurrentPhotoUri = FileProvider.getUriForFile(this,
                        "com.turkeytech.egranja.fileprovider",
                        pictureFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, IMAGE_CAMERA_CODE);
            }

        }
    }

    private void requestCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(
                    mRootLayout,
                    "This is to let the app take photos and record videos.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(EditProductActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    })
                    .show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
    }

    private void showConfirmImagePopup(final Uri imageUri) {
        AlertDialog.Builder imagePopup = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_image_preview, null);

        ImageView imageView = view.findViewById(R.id.imagePreview_image);
        imageView.setImageURI(imageUri);

        imagePopup.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                setImage(imageUri);
            }
        });

        imagePopup.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                removeImage();

                Snackbar.make(mRootLayout, "Image Discarded!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setImage(imageUri);
                            }
                        }).show();
            }
        });

        imagePopup.setTitle("Image Preview");
        imagePopup.setView(view);
        imagePopup.show();
    }

    private void setImage(Uri imageUri) {
        switch (mCurrentImageButtonId) {
            case R.id.editProduct_btnAddImage1:
                mImageButton1.setImageResource(R.drawable.ic_check);
                mCurrentPhotoUri = imageUri;
                mImageFileUri1 = mCurrentPhotoUri;
                mCurrentPhotoUri = null;
                isImage1Set = true;
                break;

            case R.id.editProduct_btnAddImage2:
                mImageButton2.setImageResource(R.drawable.ic_check);
                mCurrentPhotoUri = imageUri;
                mImageFileUri2 = mCurrentPhotoUri;
                mCurrentPhotoUri = null;
                isImage2Set = true;
                break;

            case R.id.editProduct_btnAddImage3:
                mImageButton3.setImageResource(R.drawable.ic_check);
                mCurrentPhotoUri = imageUri;
                mImageFileUri3 = mCurrentPhotoUri;
                mCurrentPhotoUri = null;
                isImage3Set = true;
                break;

            case R.id.editProduct_btnAddImage4:
                mImageButton4.setImageResource(R.drawable.ic_check);
                mCurrentPhotoUri = imageUri;
                mImageFileUri4 = mCurrentPhotoUri;
                mCurrentPhotoUri = null;
                isImage4Set = true;
                break;
        }
    }

    private void removeImage() {
        switch (mCurrentImageButtonId) {
            case R.id.editProduct_btnAddImage1:
                mImageButton1.setImageResource(R.drawable.ic_add_photo);
                mImageFileUri1 = null;
                isImage1Set = false;
                break;

            case R.id.editProduct_btnAddImage2:
                mImageButton2.setImageResource(R.drawable.ic_add_photo);
                mImageFileUri2 = null;
                isImage2Set = false;
                break;

            case R.id.editProduct_btnAddImage3:
                mImageButton3.setImageResource(R.drawable.ic_add_photo);
                mImageFileUri3 = null;
                isImage3Set = false;
                break;

            case R.id.editProduct_btnAddImage4:
                mImageButton4.setImageResource(R.drawable.ic_add_photo);
                mImageFileUri4 = null;
                isImage4Set = false;
                break;
        }
    }
    // End of Image Capturing Section


    // Audio Recording Section
    @OnClick(R.id.editProduct_btnRecordAudio)
    public void onAudioRecordClick() {



        if (isRecordingAudio) {

            showConfirmAudioPopup();
            isRecordingAudio = false;

        } else {
            if (hasAudioFile()) {

                showConfirmAudioPopup();

            } else {

                recordAudio();
                isRecordingAudio = true;
            }
        }
    }

    private boolean hasAudioFile() {

        return mAudioFilePath != null;

    }

    public void recordAudio() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestMicPermission();

        } else {

            startRecordingAudio();

        }
    }

    private void requestMicPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(mRootLayout,
                    "This is to let you record audio.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(EditProductActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                        }
                    })
                    .show();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

        }
    }

    private void startRecordingAudio() {


        mAudioFilePath = getCacheDir().getPath();
        mAudioFilePath += "/audio_description" + makeUnique() + ".mp3";

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setOutputFile(mAudioFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {

            mMediaRecorder.prepare();

        } catch (IOException e) {

            Log.e("Media Recorder", e.getMessage());
        }

        mMediaRecorder.start();
        mButtonRecordAudio.setText(R.string.product_stopRecordingAudio);
    }
    // End of Audio Recording Section

    private void showConfirmAudioPopup() {

        // Create a builder for AlertDialog
        final AlertDialog.Builder audioPopup = new AlertDialog.Builder(this);


        // Inflate the view to be used with the audioPopup dialog
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_audio_preview, null);


        // Initialize the buttons in the inflated view
        final Button playButton = view.findViewById(R.id.audioPreview_btnPlay);
        Button stopButton = view.findViewById(R.id.audioPreview_btnStop);


        // Check to see if audio is being recorded
        if (isRecordingAudio) {

            // End Audio recording and release resource
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;


            // Set Audio Button's text to 'Preview Audio'
            mButtonRecordAudio.setText(R.string.product_previewAudio);

        }


        // Initialize mediaPlayer for playback things
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        // When AudioPreview's Play Button is clicked
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Check is mediaPlayer is playing audio
                if (!mediaPlayer.isPlaying()) {

                    // Check to see whether mediaPlayer's state is paused or stopped
                    if (!isPaused) {

                        try {
                            // Set data source for mediaPlayer. The source of  the
                            // audio is in the member variable 'mAudioFilePath'.
                            // Prepare mediaPlayer for playback.
                            mediaPlayer.setDataSource(mAudioFilePath);
                            mediaPlayer.prepare();

                        } catch (IOException e) {
                            Log.e(TAG, "onClick: MediaPlayer.prepare didn't work! Dunno why!", e);
                        }
                    }


                    // Start playing the audio and set the inflated view's
                    // play button's text to 'Pause'.
                    mediaPlayer.start();
                    playButton.setText(R.string.audioPreview_pause);

                } else {

                    // Pause mediaPlayer
                    mediaPlayer.pause();


                    // Set mediaPlayer's state to paused and and set the
                    // inflated view's play button's text to 'Play'.
                    isPaused = true;
                    playButton.setText(R.string.audioPreview_play);
                }
            }
        });


        // When AudioPreview's Stop Button is clicked
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Stop and reset mediaPlayer
                mediaPlayer.stop();
                mediaPlayer.reset();


                // Set mediaPlayer's state to stopped and and set the
                // inflated view's play button's text to 'Play'.
                isPaused = false;
                playButton.setText(R.string.audioPreview_play);
            }
        });


        // When mediaPlayer successfully reaches the end of the audio
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {


                // Stop and reset mediaPlayer
                mediaPlayer.stop();
                mediaPlayer.reset();


                // Set mediaPlayer's state to stopped and and set the
                // inflated view's play button's text to 'Play'.
                isPaused = false;
                playButton.setText(R.string.audioPreview_play);
            }
        });


        // Give audioPopup a positive button and set its text to 'Keep'
        // and set it's onClickListener
        audioPopup.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                // Display audio saved message
                Snackbar.make(
                        mRootLayout,
                        "Audio Recording Saved!",
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });


        // Give audioPopup a negative button and set its text to 'Discard'
        // and set it's onClickListener
        audioPopup.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                // Set Audio Button's text to 'Start Recording Audio Description'
                mButtonRecordAudio.setText(R.string.product_startRecordingAudio);


                // Copy location of recorded audio into local variable
                // 'tempLocation' to prevent undoable deletion to the reference
                // of the recorded audio file.
                final String tempLocation = mAudioFilePath;


                // Delete location to recorded audio file. Keep in mind
                // that the actual file still exists. Only the reference to
                // the location is lost.
                mAudioFilePath = null;


                // Display audio discarded message
                Snackbar.make(mRootLayout, "Audio Recording Discarded!", Snackbar.LENGTH_LONG)

                        // Create a button named 'Undo' on the Snackbar and
                        // set it's onClickListener
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                // Reset the location of the recorded audio file
                                // to the member variable 'mAudioFilePath' from local
                                // variable 'tempLocation'.
                                mAudioFilePath = tempLocation;


                                // Set Audio Button's text to 'Preview Audio'
                                mButtonRecordAudio.setText(R.string.product_previewAudio);
                            }
                        }).show();
            }
        });


        // When audioPopup's disappears
        audioPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


                // Release mediaPlayer's resources
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });


        // Give audioPopup a title of 'Audio Preview'
        audioPopup.setTitle("Audio Preview");


        // Prevent audioPopup from being cancelled
        audioPopup.setCancelable(false);


        // Set the inflated view as the content for
        // audioPopup
        audioPopup.setView(view);


        // Show audioPopup's dialog interface
        audioPopup.show();
    }

    // Video Recording/Retrieving Section
    @OnClick(R.id.editProduct_btnUploadVideo)
    public void onVideoRecordClick() {

        if (hasVideoFile()) {

            showConfirmVideoPopup(mVideoFileUri);

        } else {

            showVideoDialog();
        }

    }

    private boolean hasVideoFile() {

        return mVideoFileUri != null;

    }

    private void showVideoDialog() {

        // Set a reference to the string resource 'Select Video From'
        int dialogTitle = R.string.showVideoDialog_title;


        // Set a reference to the string array resource. The
        // values are '{'Gallery', 'Camera'}'.
        int videoDialogItems = R.array.showVideoDialog_dialogOptions;


        // Create a builder for the alert dialog
        AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);


        // Set the title of the dialog from the string
        // 'R.string.showVideoDialog_title'.
        videoDialog.setTitle(dialogTitle);


        // Create array of options to be used with the dialog. These options are
        // stored in the array 'R.array.showVideoDialog_dialogOptions. An onClickListener
        // is added to handle shit
        videoDialog.setItems(videoDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                chooseVideoFromGallery();
                                break;
                            case 1:
                                recordVideoFromCamera();
                                break;
                        }

                    }
                });

        // Show the dialog
        videoDialog.show();
    }

    private void chooseVideoFromGallery() {

        Intent videoGalleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        if (videoGalleryIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(videoGalleryIntent, VIDEO_GALLERY_CODE);

        } else {

            Snackbar.make(
                    mRootLayout,
                    R.string.selectImageFromGallery_errorMessage,
                    Snackbar.LENGTH_LONG
            ).show();

        }
    }

    private void recordVideoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestVideoCameraPermission();

        } else {

            recordVideo();
        }

    }

    private void requestVideoCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(
                    mRootLayout,
                    "This is to let the app take photos and record videos.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(EditProductActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_VIDEO_CAMERA);
                        }
                    })
                    .show();

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_VIDEO_CAMERA);

            // MY_PERMISSIONS_REQUEST_VIDEO_CAMERA is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private void recordVideo() {

        Intent videoCameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (videoCameraIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File videoFile = null;

            try {

                videoFile = createVideoFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (videoFile != null) {

                mVideoFileUri = FileProvider.getUriForFile(this,
                        "com.turkeytech.egranja.fileprovider",
                        videoFile);

                videoCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoFileUri);
                startActivityForResult(videoCameraIntent, VIDEO_CAMERA_CODE);

            } else {

                Snackbar.make(
                        mRootLayout,
                        R.string.selectImageFromGallery_errorMessage,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String videoFileName = "3GP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                videoFileName,  /* prefix */
                ".3gp",   /* suffix */
                storageDir      /* directory */
        );
    }

    private void showConfirmVideoPopup(final Uri mCurrentVideoUri) {
        AlertDialog.Builder videoPopup = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_video_preview, null);

        final VideoView videoView = view.findViewById(R.id.videoPreview_video);
        final FrameLayout mediaControllerWrapper =
                view.findViewById(R.id.videoPreview_mediaControllerWrapper);

        videoView.setVideoURI(mCurrentVideoUri);

        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            MediaController mediacontroller;

            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayerInner,
                                                   int width, int height) {

                        // Add mediaController
                        mediacontroller = new MediaController(videoView.getContext());
                        videoView.setMediaController(mediacontroller);

                        // Anchor it to videoView
                        mediacontroller.setAnchorView(videoView);

                        // Remove mediaController from its parent layout
                        ((ViewGroup) mediacontroller.getParent()).removeView(mediacontroller);

                        // Add media controller to the FrameLayout below videoView
                        mediaControllerWrapper.addView(mediacontroller);
                        mediacontroller.setVisibility(View.VISIBLE);
                        mediacontroller.show(0);
                    }
                });
                videoView.start();
            }
        });

        videoPopup.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Display audio saved message
                Snackbar.make(
                        mRootLayout,
                        "Video Saved!",
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });

        videoPopup.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mVideoFileUri = null;
                mButtonUploadVideo.setText(R.string.product_startRecordingAudio);


                Snackbar.make(mRootLayout, "Image Discarded!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mVideoFileUri = mCurrentVideoUri;
                                mButtonUploadVideo.setText(R.string.product_previewVideo);

                            }
                        }).show();
            }
        });

        videoPopup.setTitle("Video Preview");
        videoPopup.setCancelable(false);
        videoPopup.setView(view);
        videoPopup.show();
    }
    // End of Video Recording/Retrieving section

    // Location Section
    @OnClick(R.id.editProduct_btnCurrentLocation)
    public void onCurrentLocationClick() {

        AlertDialog.Builder imageDialogBuilder = new AlertDialog.Builder(this);
        imageDialogBuilder.setMessage("Change Location?");
        imageDialogBuilder.setCancelable(true);
        imageDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doLocationStuff();
            }
        });
        imageDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        imageDialogBuilder.show();
    }

    private void doLocationStuff() {
        if (hasLocation()) {

            showConfirmLocationPopup();

        } else {

            showLocationDialog();
        }
    }

    private boolean hasLocation() {
        return mLocation != null;
    }

    private void showLocationDialog() {
        // Set a reference to the string resource 'Set Location From'
        int dialogTitle = R.string.showLocationDialog_title;


        // Set a reference to the string array resource. The
        // values are '{'Current Location', 'Location List'}'.
        int locationDialogOptions = R.array.showLocationDialog_dialogOptions;


        // Create a builder for the alert dialog
        AlertDialog.Builder locationPopup = new AlertDialog.Builder(this);


        // Set the title of the dialog from the string
        // 'R.string.showLocationDialog_title'.
        locationPopup.setTitle(dialogTitle);


        // Create array of options to be used with the dialog. These options are
        // stored in the array 'R.array.showLocationDialog_dialogOptions'. An onClickListener
        // is added to handle shit
        locationPopup.setItems(locationDialogOptions,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                getLocation();
                                break;
                            case 1:
                                onChooseLocationClick();
                                break;
                        }

                    }
                });

        // Show the dialog
        locationPopup.show();
    }

    public void onChooseLocationClick() {

        DialogLocationFragment
                .newInstance(this)
                .show(getSupportFragmentManager(), null);

    }

    @Override
    public void onLocationSelected(String location) {
        String country = ", Ghana";
        mLocation = location + country;
        showConfirmLocationPopup();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission();

        } else {

            getLastKnownLocation();

        }
    }

    private void requestLocationPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(
                    mRootLayout,
                    "Seriously?!",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(EditProductActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    })
                    .show();

        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getLastKnownLocation() throws SecurityException {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(EditProductActivity.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        reverseGeocode();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected void reverseGeocode() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private void showConfirmLocationPopup() {

        SimpleDialogFragment locationPopup = SimpleDialogFragment.newInstance(
                "Is This Your Location?",
                mLocation,      // message
                "Yes",
                "No"
        );
        locationPopup.setCancelable(false);

        locationPopup.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onPositiveResult(DialogFragment dialogFragment) {
        mButtonLocation.setText(R.string.product_viewLocation);
        showMessage("Location Successfully Saved!");
    }

    @Override
    public void onNegativeResult(DialogFragment dialogFragment) {

        mButtonLocation.setText(R.string.product_Location);
        final String tempLocation = mLocation;
        mLocation = null;

        Snackbar.make(mRootLayout, "Loaction Discarded!", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLocation = tempLocation;
                        mButtonLocation.setText(R.string.product_viewLocation);
                    }
                }).show();

    }

    @Override
    public void onNeutralResult(DialogFragment dialogFragment) {

    }

    // End of location Section


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_CODE && resultCode == RESULT_OK) {

            mCurrentPhotoUri = data.getData();
            showConfirmImagePopup(mCurrentPhotoUri);
            Log.i(TAG, "onActivityResult: ImageUri = " + mCurrentPhotoUri);

        } else if (requestCode == VIDEO_GALLERY_CODE && resultCode == RESULT_OK) {

            mVideoFileUri = data.getData();
            mButtonUploadVideo.setText(R.string.product_previewVideo);
            Log.i(TAG, "onActivityResult: VideoUri = " + mCurrentPhotoUri);

        } else if (requestCode == IMAGE_CAMERA_CODE && resultCode == RESULT_OK) {

            showConfirmImagePopup(mCurrentPhotoUri);
            Log.i(TAG, "onActivityResult: ImageUri = " + mCurrentPhotoUri);

        } else if (requestCode == VIDEO_CAMERA_CODE && resultCode == RESULT_OK) {

            mButtonUploadVideo.setText(R.string.product_previewVideo);
            Log.i(TAG, "onActivityResult: VideoUri = " + mCurrentPhotoUri);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    takePicture();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Snackbar.make(mRootLayout, "Your're an IDIOT!", Snackbar.LENGTH_LONG).show();

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    startRecordingAudio();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Snackbar.make(mRootLayout, "Your're an IDIOT!", Snackbar.LENGTH_LONG).show();

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_VIDEO_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    recordVideo();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Snackbar.make(mRootLayout, "Your're an IDIOT!", Snackbar.LENGTH_LONG).show();

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getLastKnownLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Snackbar.make(mRootLayout, "Your're an IDIOT!", Snackbar.LENGTH_LONG).show();

                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:
                upload();
                break;
        }
        return true;
    }


    @OnClick(R.id.editProduct_btnToolbarClose)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // Start uploading things
    public boolean allDataSet() {

        if (TextUtils.isEmpty(mTextProductName.getText())) {
            mTextProductName.setError("Enter Product Name!");
            isUploading = false;
            return false;
        } else if (TextUtils.isEmpty(mTextProductDescription.getText())) {
            mTextProductDescription.setError("Enter Product Description!");
            isUploading = false;
            return false;
        } else if (TextUtils.isEmpty(mTextProductQuantity.getText())) {
            mTextProductQuantity.setError("Enter Quantity Of Product!");
            isUploading = false;
            return false;
        } else if (mSpinQuantityUnit.getSelectedItemPosition() == 0) {
            showMessage("Quantity Unit Not Set!");
            isUploading = false;
            return false;
        } else if (TextUtils.isEmpty(mTextProductPrice.getText())) {
            mTextProductPrice.setError("Enter Price Of Product!");
            isUploading = false;
            return false;
        } else if (mSpinCategory.getSelectedItemPosition() == 0) {
            showMessage("Category Not Set!");
            isUploading = false;
            return false;
        } else if (isImage1Set) {
            showMessage("Image Not Set!");
            isUploading = false;
            return false;
        } else if (isImage2Set) {
            showMessage("Image Not Set!");
            isUploading = false;
            return false;
        } else if (isImage3Set) {
            showMessage("Image Not Set!");
            isUploading = false;
            return false;
        } else if (isImage4Set) {
            showMessage("Image Not Set!");
            isUploading = false;
            return false;
        } else if (mLocation == null) {
            showMessage("Location Not Set!");
            isUploading = false;
            return false;
        } else {
            mProductName = mTextProductName.getText().toString().trim();
            mDescription = mTextProductDescription.getText().toString().trim();
            mQuantity = mTextProductQuantity.getText().toString().trim();
            mQuantityUnit = mSpinQuantityUnit.getSelectedItem().toString();
            String price = mTextProductPrice.getText().toString().trim();
            if (!price.contains(".")) price = price + ".00";
            if (price.contains(".")) {
                String[] x = price.split("\\.");

                if (x[1].length() > 2) {
                    x[1] = x[1].substring(0, 2);
                    price = x[0].concat("." + x[1]);
                } else if (x[1].length() == 1) {
                    price = x[0].concat("." + x[1] + "0");
                } else if (x[1].length() == 0) {
                    price = x[0].concat(".00");
                }
            }
            mPrice = Double.parseDouble(price);
            mCategory = mSpinCategory.getSelectedItem().toString();

            if (mProductName.equals(mOldProductName)) {

            }

            mImagesList = new Uri[]{mImageFileUri1, mImageFileUri2, mImageFileUri3, mImageFileUri4};

            return true;
        }
    }

    private void upload() {

        if (isUploading) {
            showMessage("Product is updating...");
        } else {
            isUploading = true;

            if (allDataSet()) {

                Intent intent = new Intent(EditProductActivity.this, HomeActivity.class);
                intent.putExtra(HOME_SCREEN, NAV_PRODUCTS);
                startActivity(intent);


                mBuilderSuccess.setProgress(0, 0, true);
                mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());

                writeImage1();
            }
        }


    }

    private void writeImage1() {

        if (!isImage1Database) {

            StorageReference imagesFilepath = mStorage.child("images").child(UUID.randomUUID().toString());
            imagesFilepath.putFile(mImagesList[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mImageFile1Url = taskSnapshot.getDownloadUrl().toString();
                    writeImage2();
                }
            });
        } else {
            writeImage2();
        }
    }

    private void writeImage2() {

        if (!isImage2Database) {

            StorageReference imagesFilepath = mStorage.child("images").child(UUID.randomUUID().toString());
            imagesFilepath.putFile(mImagesList[1]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mImageFile2Url = taskSnapshot.getDownloadUrl().toString();
                    writeImage3();
                }
            });
        } else {
            writeImage3();
        }
    }

    private void writeImage3() {

        if (!isImage3Database) {

            StorageReference imagesFilepath = mStorage.child("images").child(UUID.randomUUID().toString());
            imagesFilepath.putFile(mImagesList[2]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mImageFile3Url = taskSnapshot.getDownloadUrl().toString();
                    writeImage4();
                }
            });
        } else {
            writeImage4();
        }
    }

    private void writeImage4() {

        if (!isImage4Database) {

            StorageReference imagesFilepath = mStorage.child("images").child(UUID.randomUUID().toString());
            imagesFilepath.putFile(mImagesList[3]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mImageFile4Url = taskSnapshot.getDownloadUrl().toString();
                    addAudioData();
                }
            });
        } else {
addAudioData();
        }
    }


    private void addAudioData() {
        if (mAudioFilePath != null) {
            Uri audio = Uri.fromFile(new File(mAudioFilePath));
            StorageReference audioFilepath = mStorage
                    .child("audio")
                    .child(UUID.randomUUID().toString());
            audioFilepath.putFile(audio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mAudioFilePath = taskSnapshot.getDownloadUrl().toString();
                    addVideoData();
                }
            });
        } else {
            addVideoData();
        }
    }

    private void addVideoData() {
        if (mVideoFileUri != null) {
            StorageReference audioFilepath = mStorage
                    .child("video")
                    .child(UUID.randomUUID().toString());
            audioFilepath.putFile(mVideoFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mVideoFileUri = taskSnapshot.getDownloadUrl();
                    addUserData();
                }
            });
        } else {
            addUserData();
        }
    }

    private void addUserData() {
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child(Constants.USERS_NAME).getValue().toString();
                String usernum = dataSnapshot.child(Constants.USERS_NUMBER).getValue().toString();

                writeProduct(mCurrentUser.getUid(), username, usernum);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeProduct(String userid, String username, String usernum) {

        if (Integer.parseInt(mQuantity) > 0) {
            mQuantityUnit += "s";
        }
        String quantity = mQuantity + " " + mQuantityUnit;
        String image1 = mImageFileUri1.toString();
        String image2 = mImageFileUri2.toString();
        String image3 = mImageFileUri3.toString();
        String image4 = mImageFileUri4.toString();

        final Product product = new Product(userid, username, usernum, mProductName, mDescription,
                quantity, mPrice, mCategory, image1, image2, image3, image4, mLocation);

        if (mAudioFilePath != null) {
            product.setAudio(mAudioFilePath);
        }
        if (mVideoFileUri != null) {
            product.setVideo(mVideoFileUri.toString());
        }

        product.setTimeStamp(stampTime());

        final String newKey = mDatabaseProduct.push().getKey();
        mDatabaseProduct.child(newKey).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    showMessage("Success: Product added to database");

                    mDatabaseCategory.child(product.getCategory()).child(newKey).setValue(true);

                    mBuilderSuccess.setContentText("Your Product Uploaded Successfully!");
                    mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());

                } else {
                    showMessage("Error: " + task.getException().getMessage());
                    mBuilderSuccess.setContentText("Your Product Upload Failed!");
                    mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());
                }

//                mProgressBar.setVisibility(View.GONE);
                isUploading = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mBuilderSuccess.setContentText("Your Product Upload Failed!");
                mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());
            }
        });

    }

    // Create a unique string. I'mma be attaching
    // this string to stuff.
    private String stampTime() {
        return new SimpleDateFormat("EEE, MMM d, \"yy", Locale.UK).format(new Date());
    }

    private String makeUnique() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
    }

    // Need I say anything?...
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(PRODUCT_NAME_KEY, mProductName);
        outState.putString(PRODUCT_DESCRIPTION_KEY, mDescription);
        outState.putString(PRODUCT_QUANTITY_KEY, mQuantity);
        outState.putString(PRODUCT_QUANTITY_UNIT_KEY, mQuantityUnit);
        outState.putDouble(PRODUCT_PRICE_KEY, mPrice);
        outState.putString(PRODUCT_CATEGORY_KEY, mCategory);
        outState.putParcelable(PRODUCT_IMAGE1_KEY, mImageFileUri1);
        outState.putParcelable(PRODUCT_IMAGE2_KEY, mImageFileUri2);
        outState.putParcelable(PRODUCT_IMAGE3_KEY, mImageFileUri3);
        outState.putParcelable(PRODUCT_IMAGE4_KEY, mImageFileUri4);
        outState.putString(PRODUCT_AUDIO_KEY, mAudioFilePath);
        outState.putParcelable(PRODUCT_VIDEO_KEY, mVideoFileUri);
        outState.putString(PRODUCT_LOCATION_KEY, mLocation);

        super.onSaveInstanceState(outState);
    }

    // I'm sure the name of the method rings a bell
    private void updateValuesFromBundle(Bundle savedInstanceState) {

        mProductName = savedInstanceState.getString(PRODUCT_NAME_KEY);
        mDescription = savedInstanceState.getString(PRODUCT_DESCRIPTION_KEY);
        mQuantity = savedInstanceState.getString(PRODUCT_QUANTITY_KEY);
        mQuantityUnit = savedInstanceState.getString(PRODUCT_QUANTITY_UNIT_KEY);
        mPrice = savedInstanceState.getDouble(PRODUCT_PRICE_KEY);
        mCategory = savedInstanceState.getString(PRODUCT_CATEGORY_KEY);
        mImageFileUri1 = savedInstanceState.getParcelable(PRODUCT_IMAGE1_KEY);
        mImageFileUri2 = savedInstanceState.getParcelable(PRODUCT_IMAGE2_KEY);
        mImageFileUri3 = savedInstanceState.getParcelable(PRODUCT_IMAGE3_KEY);
        mImageFileUri4 = savedInstanceState.getParcelable(PRODUCT_IMAGE4_KEY);
        mAudioFilePath = savedInstanceState.getString(PRODUCT_AUDIO_KEY);
        mVideoFileUri = savedInstanceState.getParcelable(PRODUCT_VIDEO_KEY);
        mLocation = savedInstanceState.getString(PRODUCT_LOCATION_KEY);

        // Update UI to match restored state
        updateUI();
    }

    // Update the UI
    private void updateUI() {

        if (mImageFileUri1 != null || mImageFile1Url != null) {
            mImageButton1.setImageResource(R.drawable.ic_check);
        }
        if (mImageFileUri2 != null || mImageFile2Url != null) {
            mImageButton2.setImageResource(R.drawable.ic_check);
        }
        if (mImageFileUri3 != null || mImageFile3Url != null) {
            mImageButton3.setImageResource(R.drawable.ic_check);
        }
        if (mImageFileUri4 != null || mImageFile4Url != null) {
            mImageButton4.setImageResource(R.drawable.ic_check);
        }
        if (mAudioFilePath != null) {
            mButtonRecordAudio.setText(R.string.product_previewAudio);
        }
        if (mVideoFileUri != null) {
            mButtonUploadVideo.setText(R.string.product_previewVideo);
        }
        if (mLocation != null) {
            mButtonLocation.setText(R.string.product_viewLocation);
        }
    }

    private void updateUiFromDatabse() {
        mTextProductName.setText(mOldProductName);
        mTextProductDescription.setText(mOldDescription);
        mTextProductQuantity.setText(mOldQuantity.split(" ")[0]);
        mTextProductPrice.setText(String.valueOf(mOldPrice));
    }

    // Display a snackbar message to the user
    private void showMessage(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();
    }


    // This class handles the Address Location Output from
    // FetchAddressIntentService class.
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.i(TAG, "onReceiveResult: " + addressOutput);

            // Check if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {

                // Location is set to retrieved address
                mLocation = addressOutput;

                // Disable the Progressbar
                mProgressBar.setVisibility(View.GONE);

                // Show a popup to confirm location
                showConfirmLocationPopup();
            } else {

                // Disable the Progress Bar
                mProgressBar.setVisibility(View.GONE);

                // Display a toast for the error
                Toast.makeText(EditProductActivity.this, addressOutput, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
