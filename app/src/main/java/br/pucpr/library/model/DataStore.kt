package br.pucpr.library.model

import android.content.Context
import android.util.Log

object DataStore {

    var books: MutableList<Book> = arrayListOf()
        private set
    var students: MutableList<Student> = arrayListOf()
        private set
    var loans: MutableList<Loan> = arrayListOf()
        private set

    private var database: Database? = null

    fun setContext(context: Context) {

        database = Database(context)
        database?.let {
            books = it.getBooks()
            students = it.getStudents()
            loans = it.getLoans()
        }
    }

    // BOOKS
    fun getAvailableBooks(): MutableList<Book> {
        database?.let {
            books = it.getAvailableBooks()
        }

        return books
    }

    fun getBook(position: Int): Book {
        return books[position]
    }

    fun addBook(book: Book) {
        val id = database?.addBook(book) ?: return

        if (id > 0) {
            book.id = id
            books.add(book)
        }
        else {
            Log.d("Library", "Operação falhou: inserindo livro")
        }
    }

    fun editBook(position: Int, book: Book) {
        book.id = getBook(position).id
        val count = database?.editBook(book) ?: return

        if (count > 0) {
            books.set(position, book)
        }
        else {
            Log.d("Library", "Operação falhou: alterando livro")
        }
    }

    fun removeBook(position: Int) {
        val count = database?.removeBook(getBook(position)) ?: return

        if (count > 0) {
            books.removeAt(position)
        }
        else {
            Log.d("Library", "Operação falhou: removendo livro")
        }
    }

    // STUDENTS
    fun getAvailableStudents(): MutableList<Student> {
        database?.let {
            students = it.getAvailableStudents()
        }

        return students
    }

    fun getStudent(position: Int): Student {
        return students[position]
    }

    fun addStudent(student: Student) {
        val id = database?.addStudent(student) ?: return

        if (id > 0) {
            student.id = id
            students.add(student)
        }
        else {
            Log.d("Library", "Operação falhou: inserindo aluno")
        }
    }

    fun editStudent(position: Int, student: Student) {
        student.id = getStudent(position).id
        val count = database?.editStudent(student) ?: return

        if (count > 0) {
            students.set(position, student)
        }
        else {
            Log.d("Library", "Operação falhou: alterando aluno")
        }
    }

    fun removeStudent(position: Int) {
        val count = database?.removeStudent(getStudent(position)) ?: return

        if (count > 0) {
            students.removeAt(position)
        }
        else {
            Log.d("Library", "Operação falhou: removendo aluno")
        }
    }

    // LOANS
    fun getLoan(position: Int): Loan {
        return loans[position]
    }

    fun getLoansFromBook(book: Book): MutableList<Loan> {
        database?.let {
            loans = it.getLoansFromBook(book)
        }

        return loans
    }

    fun getLoansFromStudent(student: Student): MutableList<Loan> {
        database?.let {
            loans = it.getLoansFromStudent(student)
        }

        return loans
    }

    fun addLoan(loan: Loan) {
        val id = database?.addLoan(loan) ?: return

        if (id > 0) {
            loan.id = id
            loans.add(loan)
        }
        else {
            Log.d("Library", "Operação falhou: inserindo empréstimo")
        }
    }

    fun editLoan(position: Int, loan: Loan) {
        loan.id = getLoan(position).id
        val count = database?.editLoan(loan) ?: return

        if (count > 0) {
            loans.set(position, loan)
        }
        else {
            Log.d("Library", "Operação falhou: alterando empréstimo")
        }
    }

    fun lastLoan(): Loan {
        val loansToSort = loans
        loansToSort.sortBy { it.id }

        return  loansToSort.last()
    }
}