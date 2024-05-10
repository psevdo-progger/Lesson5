package com.mirea.usatyukds.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mirea.usatyukds.camera.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private	static	final	int	REQUEST_CODE_PERMISSION	=	100;
    private	static	final	int	CAMERA_REQUEST	=	0;
    private	boolean	isWork	=	false;
    private Uri imageUri;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        //setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        int	cameraPermissionStatus	=	ContextCompat.checkSelfPermission(this,	android.Manifest.permission.CAMERA);

        if	(cameraPermissionStatus	==	PackageManager.PERMISSION_GRANTED)	{
            isWork = true;
        }	else	{
            //	Выполняется запрос к пользователь на получение необходимых разрешений
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
        ///////////////////
        ActivityResultCallback<ActivityResult> callback	=	new	ActivityResultCallback<ActivityResult>()	{
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    binding.imageView.setImageURI(imageUri);
                }
            }

        };

        ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback);
        //	Обработчик	нажатия	на	компонент	«imageView»
        binding.imageView1.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (isWork) {
                    try {
                        File photoFile = createImageFile();
                        String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                        imageUri = FileProvider.getUriForFile(MainActivity.this, authorities, photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        cameraActivityResultLauncher.launch(cameraIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //////////////////
    }

    private File createImageFile()	throws IOException {
        String	timeStamp	=	new SimpleDateFormat("yyyyMMdd_HHmmss",	Locale.ENGLISH).format(new Date());
        String	imageFileName	=	"IMAGE_"	+	timeStamp	+	"_";
        File	storageDirectory	=	getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return	File.createTempFile(imageFileName,	".jpg",	storageDirectory);
    }

}