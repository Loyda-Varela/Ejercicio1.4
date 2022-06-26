package com.example.ejercicio14.Procesos;

public class Transacciones {
    //nombre de la base de datos
    public static final String NameDataBase = "BDFOTO";

    //creacion de tablas de la base de datos

    public static final String tablaFotos = "Fotos";
    //escoger los campos
    public static final String id ="id";
    public static final String nombre ="nombre";
    public static final String descripcion ="descripcion";
    public static final String imagen ="imagen";


    //sentencias sql para crear tablas

public static final String CreateTableFotos = "CREATE TABLE fotos " +
            "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT, descripcion TEXT " +
            "imagen TEXT)";

    //hacer comando para eliminar la tabla
    public static final String DropTableFotos ="DROP TABLE IF EXISTS fotos";
}
