package com.adiaz.madrid.utils.entities;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Data
public class MatchLineEntity {

    private static final Logger logger = Logger.getLogger(MatchLineEntity.class);

    public MatchLineEntity(String line) throws Exception {
        String[] split = line.split(";", -1);
        field00_codTemporada = Integer.parseInt(split[0].trim());
        field01_codCompeticion = split[1].trim();
        field02_codFase = Integer.parseInt(split[2].trim());
        field03_codGrupo = Integer.parseInt(split[3].trim());
        field04_weekNum = Integer.parseInt(split[4].trim());
        field05_matchNum = Integer.parseInt(split[5].trim());
        field06_codEquipoLocal = Long.parseLong(split[6].trim());
        field07_codEquipoVisitante = Long.parseLong(split[7].trim());
        field08_scoreLocal = Integer.parseInt(split[8].trim());
        field09_scoreVisitor = Integer.parseInt(split[9].trim());
        field10_codCampo = Long.parseLong(split[10].trim());
        field11_fecha = split[11].trim();
        field12_hora = split[12].trim();
        field13_programado = Integer.parseInt(split[13].trim());
        field14_estado = split[14];
        field15_nombreTemporada = split[15].trim();
        field16_nombreCompeticion = split[16].trim();
        field17_nombreFase = split[17].trim();
        field18_nombreGrupo = split[18].trim();
        field19_nombreDeporte = split[19].trim();
        field20_nombreCategoria = split[20].trim();
        field22_equipoLocal = split[22].trim();
        field23_equipoVisitante = split[23].trim();
        field24_campo = split[24].trim();
        field26_distrito = split[26].trim();
        if (StringUtils.isNotBlank(split[29])) {
            field29_coordx = Long.parseLong(split[29].trim());
        } else {
            field29_coordx = 0L;
        }
        if (StringUtils.isNotBlank(split[30])) {
            field30_coordy = Long.parseLong(split[30].trim());
        } else {
            field30_coordy = 0L;
        }
    }

    private Integer field00_codTemporada;
    private String field01_codCompeticion;
    private Integer field02_codFase;
    private Integer field03_codGrupo;
    private Integer field04_weekNum;
    private Integer field05_matchNum;
    private Long field06_codEquipoLocal;
    private Long field07_codEquipoVisitante;
    private Integer field08_scoreLocal;
    private Integer field09_scoreVisitor;
    private Long field10_codCampo;
    private String field11_fecha;
    private String field12_hora;
    private Integer field13_programado;
    private String field14_estado;
    private String field15_nombreTemporada;
    private String field16_nombreCompeticion;
    private String field17_nombreFase;
    private String field18_nombreGrupo;
    private String field19_nombreDeporte;
    private String field20_nombreCategoria;
    private String field22_equipoLocal;
    private String field23_equipoVisitante;
    private String field24_campo;
    private String field26_distrito;
    private Long field29_coordx;
    private Long field30_coordy;
}
