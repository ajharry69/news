package com.xently.tests

import android.content.SharedPreferences

private val PREFERENCE_DB = LinkedHashMap<String, LinkedHashMap<String, Any?>>()

abstract class FakeSharedPreferenceEditor(private val name: String) : SharedPreferences.Editor {
    private var commit: Boolean = false
    protected val prefDb: LinkedHashMap<String, Any?>
        get() = PREFERENCE_DB.getOrDefault(name, LinkedHashMap())

    init {
        PREFERENCE_DB[name] = LinkedHashMap()
    }

    override fun clear(): SharedPreferences.Editor {
        prefDb.clear()
        return this
    }

    override fun putLong(p0: String?, p1: Long) = saveData(p0, p1)

    override fun putInt(p0: String?, p1: Int) = saveData(p0, p1)

    override fun remove(p0: String?): SharedPreferences.Editor {
        commit = p0 != null
        if (commit) prefDb.remove(p0!!)
        return this
    }

    override fun putBoolean(p0: String?, p1: Boolean) = saveData(p0, p1)

    override fun putStringSet(p0: String?, p1: MutableSet<String>?) = saveData(p0, p1)

    override fun commit(): Boolean = commit

    override fun putFloat(p0: String?, p1: Float) = saveData(p0, p1)

    override fun apply() = Unit

    override fun putString(p0: String?, p1: String?) = saveData(p0, p1)

    private fun saveData(p0: String?, p1: Any?): SharedPreferences.Editor {
        commit = p0 != null
        if (commit) prefDb[p0!!] = p1
        return this
    }
}