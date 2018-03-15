package com.adiaz.madrid.entities;


import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Entity
public class Competition {

    @Id
    private String id;
    @Index
    private Integer codTemporada;
    @Index
    private String codCompeticion;
    @Index
    private Integer codFase;
    @Index
    private Integer codGrupo;
    private String nombreTemporada;
    private String nombreCompeticion;
    private String nombreFase;
    private String nombreGrupo;

    @Index
    private String deporte;

    @Index
    private String distrito;

    public String generateId() throws Exception {
        if(codTemporada!=null && StringUtils.isNotBlank(codCompeticion) && codFase!=null && codGrupo!=null) {
            return DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
        }
        throw new Exception("Not possible to create id");
    }
}
