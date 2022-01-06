package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Material.Type;
import it.unive.cybertech.utils.Utils;

/**
 * This activity is used for add new product in the showcase
 *
 * @author Mattia Musone
 */
public class AddProductForRent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int PERMISSIONS_FINE_LOCATION = 5;
    ///The image of the product
    private ImageView image;
    ///The end expire date of the material
    private EditText date;
    ///Holding the uri of the photo taken by the camera, if any
    private Uri output;
    ///The type of material
    private Spinner type;
    public static final int SUCCESS = 1;
    private boolean hasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_for_rent);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.add_to_showcase);
        FloatingActionButton done = findViewById(R.id.showcase_details_done);
        EditText title = findViewById(R.id.showcase_details_title),
                description = findViewById(R.id.showcase_details_description);
        date = findViewById(R.id.showcase_details_date);
        type = findViewById(R.id.showcase_details_type);
        image = findViewById(R.id.showcase_details_image);
        Button loadImage = findViewById(R.id.showcase_details_load_image);
        loadImage.setOnClickListener(v -> pickImage());
        date.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
            datePickerDialog.show();
        });
        /**
         * This method is called when a user has compiled all the required field
         *
         * It checks if all the fields are compiled, otherwise set the error on the empty fields
         * If all is ok, then it adds the material to the database and close the activity
         *
         * Note that the locations is required
         * */
        done.setOnClickListener(v -> {
            boolean formOk = true;
            if (title.length() == 0) {
                formOk = false;
                title.setError(getString(R.string.field_required));
            }
            if (description.length() == 0) {
                formOk = false;
                description.setError(getString(R.string.field_required));
            }
            if (date.length() == 0) {
                formOk = false;
                date.setError(getString(R.string.field_required));
            }
            if (type.getSelectedItem() != null && type.getSelectedItem().toString().length() == 0)
                formOk = false;

            if (formOk) {
                String baseString = null;
                ///If an image has beet picked, then get it an upload it as Base64 on db
                if (hasImage && image.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
                    byte[] byteArray = byteStream.toByteArray();
                    baseString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                String finalBaseString = baseString;
                try {
                    Utils.getLocation(this, new Utils.TaskResult<Utils.Location>() {
                        @Override
                        public void onComplete(Utils.Location location) {
                            Utils.executeAsync(() -> Material.createMaterial(user, title.getText().toString(), description.getText().toString(), finalBaseString, (Type) type.getSelectedItem(), location.latitude, location.longitude, new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString())), new Utils.TaskResult<Material>() {
                                @Override
                                public void onComplete(Material result) {
                                    if (result != null) {
                                        Thread t = new Thread(() -> {
                                            user.addMaterial(result);
                                        });
                                        t.start();
                                        try {
                                            t.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        new Utils.Dialog(getApplicationContext())
                                                .hideCancelButton()
                                                .setCallback(new Utils.DialogResult() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Intent i = new Intent();
                                                        i.putExtra("ID", result.getId());
                                                        setResult(SUCCESS, i);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancel() {

                                                    }
                                                })
                                                .show(getString(R.string.done_exclamation), getString(R.string.material_published_to_showcase));
                                    } else
                                        new Utils.Dialog(getApplicationContext()).show(getString(R.string.error), getString(R.string.material_error));
                                }

                                @Override
                                public void onError(Exception e) {
                                    e.printStackTrace();
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } catch (Utils.PermissionDeniedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            ///Building the adapter of material types
            Utils.executeAsync(Type::getMaterialTypes, new Utils.TaskResult<ArrayList<Type>>() {
                @Override
                public void onComplete(ArrayList<Type> result) {
                    ArrayAdapter<Type> userAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, result);
                    type.setAdapter(userAdapter);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Functions that find all the activities of installed app that support the image capture in order to pick an image from the device
     * */
    private void pickImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        final List<Intent> intents = new ArrayList<Intent>();
        intents.add(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        output = Uri.fromFile(file);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
            intents.add(intent);
        }

        Intent result = Intent.createChooser(intents.remove(0), null);
        if (!intents.isEmpty()) {
            result.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[]{}));
        }
        startActivityForResult(result, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AddProductForRent", resultCode + "");
        if (requestCode == 0) {
            if (resultCode == -1) {
                ///If image has been picked, then update the ui
                if (data.getData() != null)
                    image.setImageURI(data.getData());
                else
                    image.setImageURI(output);
                hasImage = true;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar pickedDate = Calendar.getInstance();
        pickedDate.set(year, month, dayOfMonth);
        date.setText(Utils.formatDateToString(pickedDate.getTime()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            new Utils.Dialog(this)
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .hideCancelButton()
                    .show(getString(R.string.position_required), getString(R.string.position_required_description));
    }
}