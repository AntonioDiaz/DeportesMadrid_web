package com.adiaz.madrid.utils;

public class DeportesMadridConstants {
    public static final String URL_MATCHES = "https://datos.madrid.es/egob/catalogo/211549-3-juegos-deportivos-actual.csv";
    public static final String URL_CLASSIFICATION = "https://datos.madrid.es/egob/catalogo/211549-1-juegos-deportivos-actual.csv";
    public static final String URL_MATCHES_FAKE = "http://localhost:8080/catalogo/matches.csv";
    public static final String URL_CLASSIFICATION_FAKE = "http://localhost:8080/catalogo/classification.csv";
    public static final String DESCANSA = "DESCANSA";
    public static final String BUCKET_CLASSIFICATION = "deportes_madrid_classification";
    public static final String BUCKET_MATCHES= "deportes_madrid_matches";
    public static final String PATH_ENQUE_TASK = "/releases/createReleaseTask";
    public static final Integer PAGINATION_RECORDS = 500;
    public static final String CACHE_TEAMS_LIST = "TEAMS_LIST";
    public static final String CACHE_GROUPS_LIST = "GROUPS_LIST";
    public static final String PARAMETER_FCM_SERVER_KEY = "PARAMETER_FCM_SERVER_KEY";
    public static final String PARAMETER_DEBUG = "PARAMETER_DEBUG";
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String TEAMS_UPDATED_SEPARATOR = "_";
    public static final String GROUPS_UPDATED_SEPARATOR = "-";
    public static final String ERROR = "error";
    public static final String DONE = "done";

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
