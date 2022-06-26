package com.example.ejercicio14;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ejercicio14.Procesos.AlertDialogo;
import com.example.ejercicio14.Procesos.SQLiteConexion;
import com.example.ejercicio14.Procesos.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //codigo de accion
    static final int REQUESTCODECAMARA = 100;
    static final int REQUESTAKEFOTO = 101;

    EditText txtnombre, txtdescripcion;
    Button btnfoto, btnsql,btnlista;
    ImageView ObjectImage;
    Bitmap imagenGlobal;
    String pathFoto;
    String CurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtnombre =(EditText) findViewById(R.id.txtnombre);
        txtnombre =(EditText) findViewById(R.id.txtdescripcion);
        ObjectImage = (ImageView) findViewById(R.id.fotografia);

        btnfoto = (Button) findViewById(R.id.btnfoto);
        btnsql = (Button) findViewById(R.id.btnsql);
        btnlista = (Button) findViewById(R.id.btnlista);
        
        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //otorgar permisos
                OtorgarPermisos();
            }
        });

        btnsql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!txtnombre.getText().toString().isEmpty() && !txtdescripcion.getText().toString().isEmpty())
                {
                    agregarFotoSQL();
                }
                else{
                    Mensaje();
                }

            }
        });

        btnlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLista.class);
                startActivity(intent);

            }
        });

    }

    private void Mensaje() {
        AlertDialogo alerta = new AlertDialogo();
        alerta.show(getSupportFragmentManager(),"Mensaje");
    }

    private void agregarFotoSQL() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Transacciones.nombre, txtnombre.getText().toString());
        values.put(Transacciones.descripcion, txtdescripcion.getText().toString());
        values.put(Transacciones.imagen, CurrentPhotoPath);


        ByteArrayOutputStream baos = new ByteArrayOutputStream(10480);

        imagenGlobal.compress(Bitmap.CompressFormat.JPEG, 0 , baos);

        byte[] blob = baos.toByteArray();

        values.put(Transacciones.imagen, blob);

        Long result = db.insert(Transacciones.CreateTableFotos, Transacciones.id, values);

        Toast.makeText(getApplicationContext(), "Imagen Registrada con exito " + result.toString()
                ,Toast.LENGTH_LONG).show();

        db.close();

        LimpiarPatalla();

    }

    private void LimpiarPatalla() {
        txtnombre.setText("");
        txtdescripcion.setText("");
        ObjectImage.setImageBitmap(null);
        imagenGlobal = null;

    }

    private void OtorgarPermisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            //pedimos el permiso con codigo de accion
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUESTCODECAMARA);
        }
        //si el permiso esta otorgado
        else{
            TomarFotografia();
        }
    }

    private void TomarFotografia() {
        Intent tomarfoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(tomarfoto, REQUESTAKEFOTO);
    }

    //procedimiento privado con el activity
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUESTCODECAMARA){
            //si el resultado es exitoso
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                TomarFotografia();
            }
            //si el permiso es denegado
            else{
                Toast.makeText(getApplicationContext(), "Permiso Denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == REQUESTAKEFOTO && resultCode == RESULT_OK){
            /*
            Bundle extraerFoto = data.getExtras();
            //hacer el casteo
            Bitmap imageBitmap = (Bitmap) extraerFoto.get("data");
            //establecer el objeto
            ObjectImage.setImageBitmap(imageBitmap);

             */
            File foto = new File(CurrentPhotoPath);
            ObjectImage.setImageURI(Uri.fromFile(foto));
            galleryAddPic();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        CurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ejercicio14.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUESTAKEFOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(CurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}