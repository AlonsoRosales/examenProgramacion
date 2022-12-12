package com.example.pruebaprogramacion.Controller;


import com.example.pruebaprogramacion.Entity.InfoDocumento;
import com.example.pruebaprogramacion.Repository.DocumentoRepository;
import com.example.pruebaprogramacion.Service.FileStorageService;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/documento")
public class DocumentoController {
    ZoneId zoneId = ZoneId.of( "America/Lima" );

    @Autowired
    DocumentoRepository documentoRepository;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public List<InfoDocumento> listarDocumentos(){
        return documentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<String,Object>> obtenerDocumento(@PathVariable("id") String id){
        HashMap<String,Object> hashMap = new HashMap<>();
        try {
            int idDocumento = Integer.parseInt(id);
            Optional<InfoDocumento> optDoc = documentoRepository.findById(idDocumento);
            if (optDoc.isPresent()) {
                hashMap.put("existe",true);
                hashMap.put("producto",optDoc.get());
            } else {
                hashMap.put("existe",false);
                hashMap.put("msg","No existe un documento con ese ID"); //Considero que es una buena consulta solo que el id no existe
            }

            return ResponseEntity.ok(hashMap);

        } catch (NumberFormatException e) {
            hashMap.put("existe",false);
            hashMap.put("msg","El id es un número!!!");
            return ResponseEntity.badRequest().body(hashMap);
        }
    }


    @PostMapping("/guardar")
    public ResponseEntity<HashMap<String,Object>> crearDocumento(@RequestParam(value = "autor",required = false) String autorDocumento,
                                                                 @RequestParam(value = "empresa" ,required = false) String empresaDocumento,
                                                                 @RequestParam(value = "area",required = false) String areaDocumento,
                                                                 @RequestParam(value = "file",required = false)MultipartFile file){
        HashMap<String,Object> hashMap = new HashMap<>();

        boolean dataCorrecta = true;


        if(autorDocumento == null || autorDocumento.equals(" ") || autorDocumento.isEmpty()){
            System.out.println("autor incorrecto");
            dataCorrecta = false;
        }

        if(empresaDocumento == null || empresaDocumento.equals(" ") || empresaDocumento.isEmpty()){
            System.out.println("empresa incorrecto");
            dataCorrecta = false;
        }

        if(areaDocumento == null || areaDocumento.equals(" ") || areaDocumento.isEmpty()){
            System.out.println("area incorrecto");
            dataCorrecta = false;
        }


        if(file == null){
            System.out.println("file incorrecto");
            dataCorrecta = false;
        }else{
            if(!StringUtils.endsWithIgnoreCase(file.getOriginalFilename(),"pdf") && !StringUtils.endsWithIgnoreCase(file.getOriginalFilename(),"docx")){
                dataCorrecta = false;
                System.out.println("file incorrecto");
            }
        }


        if(dataCorrecta){

            String url = ServletUriComponentsBuilder.fromCurrentContextPath().path(StringUtils.cleanPath(file.getOriginalFilename())).toUriString();
            String nombreDocumento = StringUtils.cleanPath(file.getOriginalFilename());

            InfoDocumento infoDocumento = new InfoDocumento(nombreDocumento,autorDocumento,areaDocumento,empresaDocumento,1,OffsetDateTime.now(zoneId),url);

            int idFile = fileStorageService.store(file,infoDocumento);

            if(idFile != 0){
                hashMap.put("success",true);
                hashMap.put("msg","Documento creado");
                hashMap.put("ID:",idFile );
                return ResponseEntity.status(HttpStatus.CREATED).body(hashMap);

            }else{
                hashMap.put("success",false);
                hashMap.put("msg","Falla al subir archivo!!!");
                return ResponseEntity.badRequest().body(hashMap);
            }

        }else{
            hashMap.put("success",false);
            hashMap.put("msg","Campos incorrectos!!!");
            return ResponseEntity.badRequest().body(hashMap);
        }


    }


    @PutMapping(value = "/actualizar/{id}")
    public ResponseEntity<HashMap<String,Object>> actualizarDocumentoByID(@RequestBody InfoDocumento documento,
                                                                                      @PathVariable(value = "id",required = false) String idStr,
                                                                          @RequestParam(value = "file",required = false)MultipartFile file){
        HashMap<String,Object> hashMap = new HashMap<>();

        if(idStr == null || idStr.equals(" ") || idStr.isEmpty()){
            hashMap.put("existe",false);
            hashMap.put("msg","Debes ingresar un id!!!");
            return ResponseEntity.badRequest().body(hashMap);
        }else{
            try{
                int id = Integer.parseInt(idStr);

                Optional<InfoDocumento> documento1 = documentoRepository.findById(id);
                if(documento1.isPresent()){
                    boolean camposCorrectos = true;

                    InfoDocumento documentoOriginal = documento1.get();

                    if((documento.getFechaSubida() != null) || (documento.getFechaActualizacion() != null) || (documento.getFechaEliminacion() != null)){
                        camposCorrectos = false;
                    }

                    // Body/raw no admite archivos
                    if(file != null){
                        camposCorrectos = false;
                    }

                    if(documento.getDocumento() != null){
                        camposCorrectos = false;
                    }

                    if(camposCorrectos){

                        if(documento.getAutor() != null){
                            documentoOriginal.setAutor(documento.getAutor());
                        }

                        if(documento.getArea() != null){
                            documentoOriginal.setArea(documento.getArea());
                        }

                        if(documento.getEmpresa() != null){
                            documentoOriginal.setEmpresa(documento.getEmpresa());
                        }

                        if(documento.getNombre() != null){
                            documentoOriginal.setNombre(documento.getNombre());
                        }

                        if(documento.getUrl() != null){
                            documentoOriginal.setUrl(documento.getUrl());
                        }


                        documentoOriginal.setFechaActualizacion(OffsetDateTime.now(zoneId));

                        documentoRepository.save(documentoOriginal);
                        hashMap.put("success", true);
                        hashMap.put("msg","Documento Actualizado");
                        hashMap.put("ID:",id);

                        return ResponseEntity.status(HttpStatus.CREATED).body(hashMap);

                    }else{
                        hashMap.put("success",false);
                        hashMap.put("msg","Datos incorrectos!!!");
                        return ResponseEntity.badRequest().body(hashMap);
                    }

                }else{
                    hashMap.put("status","error");
                    hashMap.put("msg","el documento a actualizar no existe");
                    return ResponseEntity.ok(hashMap);
                }


            }catch (NumberFormatException e){
                hashMap.put("existe",false);
                hashMap.put("msg","El id es un número!!!");
                return ResponseEntity.badRequest().body(hashMap);
            }
        }

    }


    @DeleteMapping("")
    public ResponseEntity<HashMap<String,String>> borrarDocumento(@RequestParam("id") int id){
        HashMap<String,String> hashMap = new HashMap<>();
        Optional<InfoDocumento> optionalInfoDocumento = documentoRepository.findById(id);
        if(optionalInfoDocumento.isPresent()){
            try {
                InfoDocumento documentoOriginal = optionalInfoDocumento.get();
                documentoOriginal.setFechaEliminacion(OffsetDateTime.now(zoneId));
                documentoOriginal.setEstado(0);
                documentoRepository.save(documentoOriginal);

                hashMap.put("status","ok");

            }catch (Exception e){
                hashMap.put("status", "error-4000"); // ocurrió un error al borrar
            }
        }else{
            hashMap.put("status", "error-3000"); //no se borró

        }
        return ResponseEntity.ok(hashMap);
    }

    //linea de prueba
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,String>> gestionarErrorDocumento(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("error","true");
        hashMap.put("msg","Debes enviar un documento y en formato json");
        return ResponseEntity.badRequest().body(hashMap);
    }















}
