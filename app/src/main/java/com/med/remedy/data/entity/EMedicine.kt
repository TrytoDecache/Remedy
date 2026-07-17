package com.med.remedy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class EMedicine(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val description: String,
)