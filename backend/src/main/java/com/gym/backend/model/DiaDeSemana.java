package com.gym.backend.model;

import java.time.DayOfWeek;

import lombok.Getter;

@Getter
public enum DiaDeSemana {
    LUNES(DayOfWeek.MONDAY),
    MARTES(DayOfWeek.TUESDAY),
    MIERCOLES(DayOfWeek.WEDNESDAY),
    JUEVES(DayOfWeek.THURSDAY),
    VIERNES(DayOfWeek.FRIDAY),
    SABADO(DayOfWeek.SATURDAY),
    DOMINGO(DayOfWeek.SUNDAY);

    private final DayOfWeek dia;

    DiaDeSemana(DayOfWeek dia){
        this.dia = dia;
    }
}
