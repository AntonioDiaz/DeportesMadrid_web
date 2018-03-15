package com.adiaz.madrid.utils;

public class DeportesMadridConstants {
    public static final String URL_MATCHES = "https://datos.madrid.es/egob/catalogo/211549-3-juegos-deportivos-actual.csv";
    public static final String URL_CLASSIFICATION = "https://datos.madrid.es/egob/catalogo/211549-1-juegos-deportivos-actual.csv";
    //public static final String URL_MATCHES = "http://localhost:8080/catalogo/matches.csv";
    //public static final String URL_CLASSIFICATION = "http://localhost:8080/catalogo/classification.csv";
    public static final String DESCANSA = "DESCANSA";

    public enum MATCH_STATE {
        PENDIENTE(0, "Pendiente"), FINALIZADO(1, "Finalizado"), SUSPENDIDO(2, "Suspendido"), NO_PRESENTADO(3, "No presentado"), APLAZADO(4, "Aplazado"), DESCONOCIDO(5, "Desconocido");
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
                case 'F': return FINALIZADO;
                case 'S': return SUSPENDIDO;
                case 'N': return NO_PRESENTADO;
                case 'O': return APLAZADO;
                default: return DESCONOCIDO;
            }
            //throw new RuntimeException("Case not implemented");
        }

    }
}
