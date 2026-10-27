package com.springai.init.service;

import com.springai.init.model.Pelicula;

import java.util.List;

public interface PeliculasService {

    List<Pelicula> consultarPeliculas(String query);
}
