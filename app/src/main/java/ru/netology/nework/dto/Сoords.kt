package ru.netology.nework.dto

data class Сoords(
    val lat: String,
    val long: String,
){
    override fun toString(): String {
        return "$lat | $long"
    }
}
