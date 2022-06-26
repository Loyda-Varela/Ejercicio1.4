package com.example.ejercicio14;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ejercicio14.Procesos.Fotos;
import com.example.ejercicio14.Procesos.SQLiteConexion;
import com.example.ejercicio14.Procesos.Transacciones;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {
    SQLiteConexion conexion;
    ListView listemple;

    // dos arreglos para traer la list de empleados

    ArrayList<Fotos> lista;
    ArrayList<String> Arreglo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        //traer la lista
        listemple = (ListView) findViewById(R.id.lista);
        conexion = new SQLiteConexion(this, Transacciones.tablaFotos, null, 1);
        //obtener la lista

        ObtenerLista();
    }
    private void mostrarPicture(int i){

        Fotos fotos = lista.get(i);

        Bundle bundle = new Bundle();
        bundle.putSerializable("fotos", (Serializable) fotos);

        Intent intent = new Intent(getApplicationContext(), InformacionFotos.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void ObtenerLista() {
        //modo lectura
        SQLiteDatabase db = conexion.getReadableDatabase();
        Fotos foto = null; ///variable vacia

        lista = new ArrayList<Fotos>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.CreateTableFotos, null);

        //navegar el cursor
        while (cursor.moveToNext()) {
            foto = new Fotos();
            foto.setId(cursor.getInt(0));
            foto.setNombre(cursor.getString(1));
            foto.setDescripcion(cursor.getString(2));
            lista.add(foto);
        }
        cursor.close();
        fillList();

    }

    private void fillList() {
        Arreglo = new ArrayList<String>();
        //por cada elemnto
        for (int i = 0; i < lista.size(); i++) {

            Arreglo.add(lista.get(i).getId() + " + "
                    + lista.get(i).getNombre() + " + "
                    + lista.get(i).getDescripcion());
        }
    }
}
