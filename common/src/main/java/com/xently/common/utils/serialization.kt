package com.xently.common.utils

import com.google.gson.*
import com.xently.common.utils.Exclude.During.*
import java.text.DateFormat

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
/**
 * Excludes field(s) from json serialization and/or deserialization depending on [during]
 * @param during When to exclude field from serialization and/or deserialization
 */
annotation class Exclude(val during: During = BOTH) {
    /**
     * @see SERIALIZATION Exclude field ONLY from json serialization
     * @see DESERIALIZATION Exclude field ONLY from json deserialization
     * @see BOTH Exclude field from json serialization and deserialization
     */
    enum class During {
        /**
         * Exclude field ONLY from json serialization
         */
        SERIALIZATION,

        /**
         * Exclude field ONLY from json deserialization
         */
        DESERIALIZATION,

        /**
         * Exclude field from json serialization and deserialization
         */
        BOTH
    }
}

private fun getExclusionStrategy(during: Exclude.During = BOTH): ExclusionStrategy {
    return object : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            return if (f == null) true else {
                val annotation = f.getAnnotation(Exclude::class.java)
                if (annotation == null) {
                    false
                } else {
                    annotation.during == during
                }
            }
        }
    }
}

val JSON_CONVERTER: Gson = GsonBuilder()
    .enableComplexMapKeySerialization()
    .addSerializationExclusionStrategy(
        getExclusionStrategy(
            SERIALIZATION
        )
    )
    .addDeserializationExclusionStrategy(
        getExclusionStrategy(
            DESERIALIZATION
        )
    )
    .setExclusionStrategies(getExclusionStrategy())
    .serializeNulls()
    .setDateFormat(DateFormat.LONG)
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    /*.registerTypeAdapter(Id::class.java, IdTypeAdapter())
    .setPrettyPrinting()
    .setVersion(1.0)*/
    .create()