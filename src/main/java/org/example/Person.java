package org.example;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Person {
    private String name;
    private String surname;
    private Integer passportData;
    private LocalDate birthDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getPassportData() {
        return passportData;
    }

    public void setPassportData(Integer passportData) {
        this.passportData = passportData;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
/*
    public String getFormattedBirthDate() {
        if (birthDate == null) return "нет данных о др";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return birthDate.format(formatter);
    }

    public Integer getAge(){
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }*/
}
