package io.left.core.assignment.ui.theme_color_change;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.left.core.assignment.data.helper.SharedPreferenceHelper;
import io.left.core.assignment.data.helper.UtilityForProfileImage;
import io.left.core.assignment.ui.base.BaseActivity;
import io.left.core.assignment.ui.edit_profile.EditProfileActivity;
import io.left.core.util.R;
import io.left.core.util.helper.GetColorUtil;


public class ThemeColorChangeActivity extends BaseActivity<ThemeColorChangeMvpView, ThemeColorChangePresenter> implements ThemeColorChangeMvpView, View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    String userChoosenTask;
    Bitmap thumbnail;
    Bitmap bm;

    @BindView(R.id.image_view_user)
    CircleImageView imageviewUser;
    @BindView(R.id.text_view_first_name)
    TextView textViewFirstName;
    @BindView(R.id.text_view_last_name)
    TextView textViewLastName;

    @BindView(R.id.relative_layout)
    RelativeLayout relativeLayout;

    AlertDialog dialog;
    CircleImageView imageViewUserCircular;
    Animation animationDown, animationUp;
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_color_change);
        ButterKnife.bind(this);
        imageviewUser.setOnClickListener(this);

        if (!SharedPreferenceHelper.onInstance(this).getFirstName().equals("")&&!SharedPreferenceHelper.onInstance(this).getLastName().equals("")){
            textViewFirstName.setText(SharedPreferenceHelper.onInstance(this).getFirstName());
            textViewLastName.setText(SharedPreferenceHelper.onInstance(this).getLastName());
        }
        animationDown = AnimationUtils.loadAnimation(getBaseContext(), R.anim.transcation_down);
        animationUp = AnimationUtils.loadAnimation(getBaseContext(), R.anim.transcation_up);



    }

    @Override
    protected ThemeColorChangePresenter initPresenter() {
        return new ThemeColorChangePresenter();
    }

    //take image from camera and device

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilityForProfileImage.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    /*
        * select image from camera and mobile device
        * */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ThemeColorChangeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilityForProfileImage.checkPermission(ThemeColorChangeActivity.this);


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    //onCaptureImageResult

    public void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String root = Environment.getExternalStorageDirectory().toString()+"/Assignment";
        File folder= new File(root);
        if(!folder.exists()){

            folder.mkdirs();
        }


        for (File file: folder.listFiles()) if (!file.isDirectory()) file.delete();

        File file=new File(folder,"Assignment"+Math.random()+".png");

        FileOutputStream fo;
        try {
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load(bytes.toByteArray())
                .asBitmap()
                .into(imageviewUser);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                relativeLayout.setBackgroundColor(GetColorUtil.PalletColorFromImage(thumbnail));
            }
        }, 500);
    }


    //onSelectFromGalleryResult

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        bm = null;
        if (data != null) {
            try {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                String root = Environment.getExternalStorageDirectory().toString()+"/Assignment";
                File folder= new File(root);
                if(!folder.exists()){

                    folder.mkdirs();
                }


                for (File file: folder.listFiles()) if (!file.isDirectory()) file.delete();

                File file=new File(folder,"Assignment"+Math.random()+".png");


                FileOutputStream fo;
                try {

                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Glide.with(this)
                        .load(bytes.toByteArray())
                        .asBitmap()
                        .into(imageviewUser);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.setBackgroundColor(GetColorUtil.PalletColorFromImage(bm));
                    }
                }, 500);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }



    public  void dialogForProfileEdit(){

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_box_edit_profile, null);
        alertLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_animatin) );
        final ImageButton imageButtonCamera = (ImageButton) alertLayout.findViewById(R.id.image_button_camera);
        final ImageButton imageButtonPerson = (ImageButton) alertLayout.findViewById(R.id.image_button_person);
        final ImageButton imageButtonMessge = (ImageButton) alertLayout.findViewById(R.id.image_button_messge);
        final ImageButton imageButtonEditProfile = (ImageButton) alertLayout.findViewById(R.id.image_button_edit_profile);
        imageViewUserCircular = (CircleImageView) alertLayout.findViewById(R.id.image_view_user_circular);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });


        imageButtonCamera.setOnClickListener(this);
        imageButtonEditProfile.setOnClickListener(this);


        dialog.show();

    }

    @Override
    public void onClick(View view) {
        // selectImage();
        switch (view.getId()){
            case  R.id.image_view_user:
                imageviewUser.startAnimation(animationDown);
                animationDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        dialogForProfileEdit();
                        imageViewUserCircular.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                break;
            case R.id.image_button_camera:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    imageviewUser.startAnimation(animationUp);
                }
                selectImage();
                break;

            case  R.id.image_button_edit_profile:
                dialog.dismiss();
                startActivity(new Intent(this, EditProfileActivity.class));
                imageviewUser.startAnimation(animationUp);
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (dialog!=null){
            if (!dialog.isShowing()){
                Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                imageviewUser.setVisibility(View.VISIBLE);
            }
        }

    }
}
