package com.example.pruebaprogramacion.Controller;


import com.example.pruebaprogramacion.Entity.InfoDocumento;
import com.example.pruebaprogramacion.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class ContentOfDocumentController {

    @Autowired
    FileStorageService fileStorageService;


    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerDocumento(@PathVariable(value = "nombreArchivo",required = false) String nombre){

        if(nombre == null || nombre.equals(" ") || nombre.isEmpty()){
            return ResponseEntity.badRequest().
                    body(new ByteArrayResource(null));

        }else{
            byte[] arrayBytes =  fileStorageService.getFile(nombre);
            return ResponseEntity.ok()
                    .body(new ByteArrayResource(arrayBytes));
        }

    }

}
