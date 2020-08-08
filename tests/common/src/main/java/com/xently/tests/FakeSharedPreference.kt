package com.xently.tests

import android.content.SharedPreferences

class FakeSharedPreference(name: String = "default_name") : FakeSharedPreferenceEditor(name),
    SharedPreferences {
    override fun contains(p0: String?): Boolean = prefDb.contains(p0)

    override fun getBoolean(p0: String?, p1: Boolean): Boolean {
        return if (p0.isNullOrBlank()) p1 else prefDb.getOrDefault(p0, p1) as Boolean
    }

    override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) =
        Unit

    override fun getInt(p0: String?, p1: Int): Int =
        if (p0.isNullOrBlank()) p1 else (prefDb[p0] as? Int) ?: p1

    override fun getAll(): MutableMap<String, *> = prefDb

    override fun edit(): SharedPreferences.Editor = this

    override fun getLong(p0: String?, p1: Long): Long =
        if (p0.isNullOrBlank()) p1 else (prefDb[p0] as? Long) ?: p1

    override fun getFloat(p0: String?, p1: Float): Float =
        if (p0.isNullOrBlank()) p1 else (prefDb[p0] as? Float) ?: p1

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(p0: String?, p1: MutableSet<String>?): MutableSet<String> =
        (if (p0.isNullOrBlank()) p1 else prefDb[p0] as? MutableSet<String>) ?: mutableSetOf()

    override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) =
        Unit

    override fun getString(p0: String?, p1: String?): String? =
        if (p0.isNullOrBlank()) p1 else prefDb[p0] as? String
}

