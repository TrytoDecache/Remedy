package com.med.remedy.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T: NavKey> NavBackStack<T>.addUnique(screen: T) {
    val isAlready = this.iterator().asSequence().any { it == screen }
    if (!isAlready) {
        this.add(screen)
    }
}