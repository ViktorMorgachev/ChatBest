package com.pet.lovefinder

inline infix fun <reified T> Int.between(list: List<T>): Boolean {
    return this == 0 || this == list.size
}