package com.example.trato_hecho.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rut: String,
    val nombre: String,
    val correo: String
)