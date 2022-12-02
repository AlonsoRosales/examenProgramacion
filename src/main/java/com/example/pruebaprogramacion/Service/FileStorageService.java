package com.example.pruebaprogramacion.Service;

import com.example.pruebaprogramacion.Entity.InfoDocumento;
import com.example.pruebaprogramacion.Repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Service
public class FileStorageService {

    @Autowired
    DocumentoRepository documentoRepository;

    public int store(MultipartFile file, InfoDocumento doc){
        try {
            doc.setDocumento(file.getBytes());
            documentoRepository.save(doc);
            return doc.getId();

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public byte[] getFile(String nombreArchivo){
        return documentoRepository.getBytesDocument(nombreArchivo);
    }
}
