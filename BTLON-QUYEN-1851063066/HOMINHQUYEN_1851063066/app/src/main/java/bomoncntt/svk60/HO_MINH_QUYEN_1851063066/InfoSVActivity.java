package bomoncntt.svk60.HO_MINH_QUYEN_1851063066;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bomoncntt.svk60.HO_MINH_QUYEN_1851063066.helper.AllConTrol;
import bomoncntt.svk60.HO_MINH_QUYEN_1851063066.helper.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;

public class InfoSVActivity extends AppCompatActivity {



    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    Button buttonImage;
    CircleImageView imageView;
    Spinner spinnerlop;
    ArrayList<String> arraylistLop;
    String lop = "";
    EditText txtmasv, txttensv;
    Button btnluu, btnlamlai;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    DatabaseHelper mydb = null;
    Bitmap bp;
    Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_svactivity);

        setTitle("Thêm mới");
        anhxa();

        arraylistLop = new ArrayList<String>();
        arraylistLop.add("Khóa 59");
        arraylistLop.add("Khóa 60");
        arraylistLop.add("Khóa 61");
        arraylistLop.add("Khóa 62");

        ArrayAdapter<String> adapterlop = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraylistLop);
        adapterlop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerlop.setAdapter(adapterlop);
        spinnerlop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                lop = arraylistLop.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lop = "";
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        btnluu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((txtmasv.getText().toString().contains(" ")) || (txtmasv.length() == 0)) {
                    txtmasv.requestFocus();
                    txtmasv.setError("MASV khong được có khoảng trắng hay bỏ trống");
                } else if (txttensv.length() == 0) {
                    txttensv.requestFocus();
                    txttensv.setError("Khong được bỏ trống tên SV");
                } else {
                        int selectedId = radioSexGroup.getCheckedRadioButtonId();
                        radioSexButton = (RadioButton) findViewById(selectedId);
                        String hinhanh = "";
                        if (bp != null) {
                            Bitmap bMapScaled = AllConTrol.getResizedBitmap(bp,250);
                            hinhanh = AllConTrol.BitMapToString(bMapScaled);
                        }else {
                                hinhanh = getNoavartar();
                        }
                        Boolean Inserted = mydb.insertData(txtmasv.getText().toString(), txttensv.getText().toString(), radioSexButton.getText().toString(), lop, hinhanh);
                        if (Inserted) {
                            Log.d("tag_checkIMG", "Insert Infor" + " " + hinhanh);
                            Toast.makeText(InfoSVActivity.this, "Data is Inserted", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(InfoSVActivity.this, "Data is failed", Toast.LENGTH_SHORT).show();
                        }
                    finish();
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //xóa đi cửa sổ parent
                    startActivity(in);//mở lên cửa sổ MainActivity
                }
            }
        });
        btnlamlai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetView();
            }
        });
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void anhxa() {
        imageView = findViewById(R.id.img_avartar);
        mydb = new DatabaseHelper(this);
        btnluu = (Button) findViewById(R.id.btnluu);
        btnlamlai = (Button) findViewById(R.id.btnlamlai);
        txtmasv = (EditText) findViewById(R.id.txtmasv);
        txttensv = findViewById(R.id.txttensv);
        radioSexGroup = (RadioGroup) findViewById(R.id.radiogroupsex);
        spinnerlop = findViewById(R.id.spinnerlop);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    //@SuppressLint("ResourceType")
    private void resetView() {
        txtmasv.requestFocus();
        txtmasv.setText("");
        txttensv.setText("");
        radioSexButton = (RadioButton) findViewById(R.id.radioButtonNam);
        radioSexGroup.check(radioSexButton.getId());
        spinnerlop.setSelection(0);
        imageView.setImageBitmap(AllConTrol.StringToBitMap(getNoavartar()));
    }

    public Intent getPickImageChooserIntentFile() {
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
        }
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    public Intent getPickImageChooserIntentCamera() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
        }
        //allIntents.add(captureIntent);
        Intent chooserIntent = Intent.createChooser(captureIntent, "Select source");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntent);

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);


        }
        //allIntents.add(0,captureIntent);
        allIntents.add(0, captureIntent);
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select source");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntent);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        // Add all other intents
        //chooserIntent
        return chooserIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {
                    bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);//hình ảnh trong thư mục mà chụp
                    //   myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    bp = AllConTrol.getResizedBitmap(bp, 500); //nén ảnh lại

                    //Đoạn lệnh hiển thị ảnh lên circleimageview
                    imageView.setImageBitmap(bp);
                    //  imageView.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                bp = bitmap;
                if (imageView != null) {
                    imageView.setImageBitmap(bp);
                }
            }
        }
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    public boolean hasPermission(String permission) {
        if (AllConTrol.canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.camera, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.camera) {
            openCamera();
        }
        if (itemId == R.id.folder) {
            openFolder();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCamera() {
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        Intent in = getPickImageChooserIntentCamera();
        startActivityForResult(in, 200);
    }

    private void openFolder() {
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        startActivityForResult(getPickImageChooserIntentFile(), 200);
    }

    private String getNoavartar(){
        String hinhanh;
        Bitmap bMapScaled = ((BitmapDrawable)getResources().getDrawable(R.drawable.noavatar)).getBitmap();
        hinhanh = AllConTrol.BitMapToString(bMapScaled);
        return hinhanh;
    }

}