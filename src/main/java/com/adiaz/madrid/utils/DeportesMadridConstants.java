package com.adiaz.madrid.utils;

public class DeportesMadridConstants {
    public static final String URL_MATCHES = "https://datos.madrid.es/egob/catalogo/211549-3-juegos-deportivos-actual.csv";
    public static final String URL_CLASSIFICATION = "https://datos.madrid.es/egob/catalogo/211549-1-juegos-deportivos-actual.csv";
    //public static final String URL_MATCHES = "http://localhost:8080/catalogo/matches.csv";
    //public static final String URL_CLASSIFICATION = "http://localhost:8080/catalogo/classification.csv";
    public static final String DESCANSA = "DESCANSA";
    public static final String BUCKET_CLASSIFICATION = "deportes_madrid_classification";
    public static final String BUCKET_MATCHES= "deportes_madrid_matches";

    public enum MATCH_STATE {
        PENDIENTE(0, "Pendiente"),
        REPROGRAMADO (1, "Reprogramado"),
        COMITE(2, "Comite"),
        FINALIZADO(3, "Finalizado"),
        SUSPENDIDO(4, "Suspendido"),
        NO_PRESENTADO(5, "No presentado"),
        APLAZADO(6, "Aplazado"),
        DESCONOCIDO(7, "Desconocido"),
        DESCANSA(8, "Descansa");

        private int value;
        private String stateDesc;

        MATCH_STATE(int i, String stateDesc) {
            this.value = i;
            this.stateDesc = stateDesc;
        }

        public int getValue() {
            return value;
        }

        public String getStateDesc() {
            return stateDesc;
        }

        public static MATCH_STATE createState(char input) {
            switch(input) {
                case ' ': return PENDIENTE;
                case 'A': return REPROGRAMADO;
                case 'C': return COMITE;
                case 'F': return FINALIZADO;
                case 'S': return SUSPENDIDO;
                case 'N': return NO_PRESENTADO;
                case 'O': return APLAZADO;
                case 'R': return DESCONOCIDO;
                default: return DESCONOCIDO;
            }
            //throw new RuntimeException("Case not implemented");
        }

    }
}
