package com.example.routinecomposeroom.data.database

//Pedimos como parametro el valor escogido en la UI para calcular la frecuencia de manera correcta

enum class Frequency (val frequencyDisplay: String)  {
    DAILY("Diario"),
    WEEKLY("Semanal"),
    MONTHLY("Mensual")
}