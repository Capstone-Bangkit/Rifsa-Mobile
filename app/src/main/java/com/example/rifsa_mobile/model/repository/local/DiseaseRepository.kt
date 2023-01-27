package com.example.rifsa_mobile.model.repository.local

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.rifsa_mobile.helpers.fetching.StatusRespons
import com.example.rifsa_mobile.model.entity.remotefirebase.DiseaseEntity
import com.example.rifsa_mobile.model.local.room.dbconfig.DbConfig

class DiseaseRepository(
    database : DbConfig,
) {
    private val dao = database.diseaseDao()

    fun readLocalDisease(): LiveData<List<DiseaseEntity>> =
        dao.getDiseaseLocal()

    fun getDiseaseNotUploaded(): LiveData<List<DiseaseEntity>> =
        dao.getDiseaseNotUploaded()

    fun insertLocalDisease(data : DiseaseEntity) =
        dao.insertDiseaseLocal(data)

    fun deleteLocalDisease(id: String) =
        dao.deleteDiseaseLocal(id)

    fun updateDiseaseUpload(imageUri : Uri, idDisease : String) =
        dao.updateDiseaseUpload(imageUri.toString(), idDisease)
}