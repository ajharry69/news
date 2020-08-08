package com.xently.tests.unit.rules

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RoomDatabaseRule<T : RoomDatabase>(private val klass: Class<T>) : TestWatcher() {
    lateinit var database: T

    override fun finished(description: Description?) {
        super.finished(description)
        database.close()
    }

    override fun starting(description: Description?) {
        super.starting(description)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            klass
        ).allowMainThreadQueries().build()
    }
}