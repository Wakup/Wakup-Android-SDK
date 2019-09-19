package com.yellowpineapple.wakup.sdk.controllers

data class Person(var name: String, var age: Int)

fun getPeople(): List<Person> {
    var alice = Person("Alice", 29)
    return listOf(Person("Alice", 29), Person("Bob", 31))
}