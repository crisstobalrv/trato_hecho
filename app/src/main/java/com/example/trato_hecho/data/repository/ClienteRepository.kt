package com.example.trato_hecho.data.repository

import com.example.trato_hecho.data.local.ClienteDao
import com.example.trato_hecho.data.model.ClienteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ClienteRepository(private val clienteDao: ClienteDao) {

    // Mantenemos suspend aquí, pero usamos Dispatchers.IO
    suspend fun insertarCliente(cliente: ClienteEntity) {
        withContext(Dispatchers.IO) {
            clienteDao.insertarCliente(cliente)
        }
    }

    fun obtenerClientes(): Flow<List<ClienteEntity>> {
        return clienteDao.obtenerTodosLosClientes()
    }
}