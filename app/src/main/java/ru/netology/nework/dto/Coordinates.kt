package ru.netology.nework.dto

data class Coordinates(
    val lat: String,
    val long: String,
){
    override fun toString(): String {
        return "$lat | $long"
    }
}
