package com.adiaz.madrid.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Data
public class ClassificationLineEntity {

    private static final Logger logger = Logger.getLogger(ClassificationLineEntity.class);


    public ClassificationLineEntity(String line) throws Exception {
        String[] split = line.split(";");
        field00_codTemporada = Integer.parseInt(split[0].trim());
        field01_codCompeticion = split[1].trim();
        field02_codFase = Integer.parseInt(split[2].trim());
        field03_codGrupo = Integer.parseInt(split[3].trim());
        field04_codEquipo = Long.parseLong(split[4].trim());
        field05_posicion = Integer.parseInt(split[5].trim());
        field06_puntos = Integer.parseInt(split[6].trim());
        field07_partidosJugados = Integer.parseInt(split[7].trim());
        field08_partidosGanados = Integer.parseInt(split[8].trim());
        field09_partidosEmpatados = Integer.parseInt(split[9].trim());
        field10_partidosPerdidos = Integer.parseInt(split[10].trim());
        field11_golesFavor = Integer.parseInt(split[11].trim());
        field12_golesContra = Integer.parseInt(split[12].trim());
        field13_nombreTemporada = split[13].trim();
        field14_nombreCompeticion = split[14].trim();
        field15_nombreFase = split[15].trim();
        field16_nombreGrupo = split[16].trim();
        field17_nombreDeporte = split[17].trim();
        field18_nombreCategoria = split[18].trim();
        field19_nombreEquipo = split[19].trim();
        field20_nombreSexo = split[20].trim();
        field21_nombreDistrito = split[21].trim();
    }

    private Integer field00_codTemporada;
    private String field01_codCompeticion;
    private Integer field02_codFase;
    private Integer field03_codGrupo;
    private Long field04_codEquipo;
    private Integer field05_posicion;
    private Integer field06_puntos;
    private Integer field07_partidosJugados;
    private Integer field08_partidosGanados;
    private Integer field09_partidosEmpatados;
    private Integer field10_partidosPerdidos;
    private Integer field11_golesFavor;
    private Integer field12_golesContra;
    private String field13_nombreTemporada;
    private String field14_nombreCompeticion;
    private String field15_nombreFase;
    private String field16_nombreGrupo;
    private String field17_nombreDeporte;
    private String field18_nombreCategoria;
    private String field19_nombreEquipo;
    private String field20_nombreSexo;
    private String field21_nombreDistrito;


}
