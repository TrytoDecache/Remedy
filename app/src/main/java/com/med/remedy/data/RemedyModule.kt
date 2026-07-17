package com.med.remedy.data

import android.content.Context
import androidx.room.Room
import com.med.remedy.data.dao.MedicineDao
import com.med.remedy.data.dao.ReminderDao
import com.med.remedy.data.dao.ReminderOccurrenceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RemedyDatabase {
        return Room.databaseBuilder(
            context,
            RemedyDatabase::class.java,
            "remedy.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideMedicineDao(db: RemedyDatabase): MedicineDao = db.medicineDao()

    @Provides
    fun provideReminderDao(db: RemedyDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideReminderOccurrenceDao(db: RemedyDatabase): ReminderOccurrenceDao = db.reminderOccurrenceDao()
}