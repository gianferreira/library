package br.pucpr.library.model

class Loan(
    var book: Long,
    var student: Long,
    var bookTitle: String,
    var studentName: String,
    var active: Int,
) {
    var id: Long = 0
    constructor(book: Long, student: Long) : this(book, student, "", "", 1)
}