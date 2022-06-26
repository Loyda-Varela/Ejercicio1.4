package com.example.ejercicio14;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ejercicio14.Procesos.Fotos;

import java.io.ByteArrayInputStream;

public class InformacionFotos extends AppCompatActivity {
    ImageView imageViewMostrarIMG;

    EditText Nombre, Descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_fotos);

        imageViewMostrarIMG = (ImageView) findViewById(R.id.IMGVMostrarFotoP);
        Nombre = (EditText) findViewById(R.id.txtMostNombre);
        Descripcion = (EditText) findViewById(R.id.txtMostDescripcion);
        Bundle objetEnvia = getIntent().getExtras();
        Fotos imagen = null;

        if (objetEnvia != null) {
            imagen = (Fotos) objetEnvia.getSerializable("fotos");

            Nombre.setText(imagen.getNombre());
            Descripcion.setText(imagen.getDescripcion());
            mostrarImagen(imagen.getImagen());
            Bitmap image = BitmapFactory.decodeFile(imagen.getImagen());
            imageViewMostrarIMG.setImageBitmap(image);
        }
    }

        private void mostrarImagen(byte[] image) {
            Bitmap bitmap = null;
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            bitmap = BitmapFactory.decodeStream(bais);
            imageViewMostrarIMG.setImageBitmap(bitmap);
        }

}