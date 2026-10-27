package com.springai.init.controller;

import com.springai.init.model.Pelicula;
import com.springai.init.service.PeliculasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class peliculasController {

    PeliculasService peliculasService;


    public peliculasController(PeliculasService peliculasService) {
        this.peliculasService = peliculasService;
    }

    @GetMapping("peliculas")
    public ResponseEntity<List<Pelicula>> peliculas(@RequestParam String preferencia) {
        return ResponseEntity.ok(peliculasService.consultarPeliculas(preferencia));
    }
}
