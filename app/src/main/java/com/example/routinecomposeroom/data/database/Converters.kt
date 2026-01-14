package com.example.routinecomposeroom.data.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime


//Aplicamos la clase converters para pasar fecha y hora a string cuando van an ser guardados
//tambien para pasar los string que guardamos en la base de datos otra vez a LocalDate y LocalHour

//Con los enum aplicamos la misma logica pasamos uno de los valores a string para guardar en BBDD
//Si lo leemos en el programa nuevamente pasa a uno de los valores instanciados

// Principalmente para trabajar con workManager con  LocalDate y LocalHour y la frecuencia establecidos

class Converters {

    // --- FECHAS (LocalDate) ---

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun stringToLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null // Si falla el formato, devolvemos null en vez de romper la app
            }
        }
    }

    // --- HORAS (LocalTime) ---

    @TypeConverter
    fun localTimeToString(hour: LocalTime?): String? {
        return hour?.toString()
    }

    @TypeConverter
    fun stringToLocalTime(hourString: String?): LocalTime? {
        return hourString?.let {
            try {
                LocalTime.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    // --- FRECUENCIA (Enum) ---

    @TypeConverter
    fun frequencyToString(freq: Frequency?): String? {
        return freq?.name
    }

    @TypeConverter
    fun stringToFrequency(value: String?): Frequency {
        // Truco de seguridad: Si el valor es nulo o inválido, devolvemos una frecuencia por defecto
        // para que la UI siempre tenga algo que mostrar.
        return if (value.isNullOrEmpty()) {
            Frequency.DAILY
        } else {
            try {
                Frequency.valueOf(value)
            } catch (e: IllegalArgumentException) {
                Frequency.DAILY
            }
        }
    }
}
