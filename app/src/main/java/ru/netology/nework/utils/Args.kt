package ru.netology.nework.utils

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object StringArg : ReadWriteProperty<Bundle, String?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>) =
        thisRef.getString(property.name)


    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: String?) {
        thisRef.putString(property.name, value)
    }
}

object BooleanArg : ReadWriteProperty<Bundle, Boolean?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>) =
        thisRef.getBoolean(property.name)

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Boolean?) {
        value?.let { thisRef.putBoolean(property.name, it) }
    }

}