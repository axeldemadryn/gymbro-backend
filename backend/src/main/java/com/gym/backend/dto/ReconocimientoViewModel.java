package com.gym.backend.dto;

import java.util.List;

public class ReconocimientoViewModel {
    private int cantidadObjetos;
    private List<ObjetoReconocido> objetos;

    public static class ObjetoReconocido {
        private String nombre;
        private double confianza;
        private int x, y;

        public ObjetoReconocido(String nombre, double confianza, int x, int y) {
            this.nombre = nombre;
            this.confianza = confianza;
            this.x = x;
            this.y = y;
        }

        public String getNombre() { return nombre; }
        public double getConfianza() { return confianza; }
        public int getX() { return x; }
        public int getY() { return y; }
    }

    public int getCantidadObjetos() { return cantidadObjetos; }
    public void setCantidadObjetos(int cantidadObjetos) { this.cantidadObjetos = cantidadObjetos; }
    public List<ObjetoReconocido> getObjetos() { return objetos; }
    public void setObjetos(List<ObjetoReconocido> objetos) { this.objetos = objetos; }
}
