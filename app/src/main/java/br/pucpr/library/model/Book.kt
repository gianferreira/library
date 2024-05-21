package br.pucpr.library.model

class Book(
    var title: String,
    var author: String,
    var available: Int,
) {
    var id: Long = -1
}