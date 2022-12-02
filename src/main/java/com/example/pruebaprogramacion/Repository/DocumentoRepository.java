package com.example.pruebaprogramacion.Repository;

import com.example.pruebaprogramacion.Entity.InfoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<InfoDocumento,Integer> {
    @Query(nativeQuery = true,value = "SELECT documento FROM \"infoDocumentos\" WHERE nombre = ?1")
    byte[] getBytesDocument(String nombreArchivo);
}
