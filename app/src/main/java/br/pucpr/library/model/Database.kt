package br.pucpr.library.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        const val DATABASE_NAME = "library.db"
        const val DATABASE_VERSION = 1

        const val DB_TABLE_BOOKS = "books"
        const val DB_TABLE_STUDENTS = "students"
        const val DB_TABLE_LOANS = "loans"

        const val DB_FIELD_ID = "id"
        const val DB_FIELD_AUTHOR = "author"
        const val DB_FIELD_TITLE = "title"
        const val DB_FIELD_NAME = "name"
        const val DB_FIELD_PHONE = "phone"
        const val DB_FIELD_BOOK = "book"
        const val DB_FIELD_STUDENT = "student"
        const val DB_FIELD_ACTIVE = "active"
        const val DB_FIELD_AVAILABLE = "available"

        const val DB_VALUE_ACTIVE = 1
        const val DB_VALUE_INACTIVE = 0

        const val sqlCreateBooks =
                "CREATE TABLE IF NOT EXISTS $DB_TABLE_BOOKS (" +
                "$DB_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$DB_FIELD_ACTIVE INTEGER, " +
                "$DB_FIELD_AVAILABLE INTEGER, " +
                "$DB_FIELD_TITLE TEXT, " +
                "$DB_FIELD_AUTHOR TEXT);"

        const val sqlCreateStudents =
                "CREATE TABLE IF NOT EXISTS $DB_TABLE_STUDENTS (" +
                "$DB_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$DB_FIELD_ACTIVE INTEGER, " +
                "$DB_FIELD_AVAILABLE INTEGER, " +
                "$DB_FIELD_NAME TEXT, " +
                "$DB_FIELD_PHONE TEXT);"

        const val sqlCreateLoans =
                "CREATE TABLE IF NOT EXISTS $DB_TABLE_LOANS (" +
                "$DB_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$DB_FIELD_ACTIVE INTEGER, " +
                "$DB_FIELD_BOOK INTEGER, " +
                "$DB_FIELD_STUDENT INTEGER);"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val db = db ?: return

        db.beginTransaction()
        try {
            db.execSQL(sqlCreateBooks)
            db.execSQL(sqlCreateStudents)
            db.execSQL(sqlCreateLoans)
            db.setTransactionSuccessful()
        }
        catch(e: Exception) {
            Log.d("Library", e.localizedMessage)
        }
        finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val db = db ?: return

        if (oldVersion == 1 && newVersion == 2) {
            db.beginTransaction()
            try {
                db.execSQL(sqlCreateLoans)
                db.setTransactionSuccessful()
            }
            finally {
                db.endTransaction()
            }
        }
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    // BOOKS
    fun getBooks(): MutableList<Book> {

        var books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $DB_TABLE_BOOKS " +
            " WHERE $DB_FIELD_ACTIVE = $DB_VALUE_ACTIVE " +
            " ORDER BY $DB_FIELD_TITLE ",
            null,
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val title = getString(getColumnIndexOrThrow(DB_FIELD_TITLE))
                val author = getString(getColumnIndexOrThrow(DB_FIELD_AUTHOR))
                val available = getInt(getColumnIndexOrThrow(DB_FIELD_AVAILABLE))
                val book = Book(title, author, available)
                book.id = id
                books.add(book)
            }
        }

        return books
    }

    fun getAvailableBooks(): MutableList<Book> {

        var books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $DB_TABLE_BOOKS " +
            " WHERE $DB_FIELD_ACTIVE = $DB_VALUE_ACTIVE " +
            " AND $DB_FIELD_AVAILABLE = $DB_VALUE_ACTIVE " +
            " ORDER BY $DB_FIELD_TITLE ",
            null,
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val title = getString(getColumnIndexOrThrow(DB_FIELD_TITLE))
                val author = getString(getColumnIndexOrThrow(DB_FIELD_AUTHOR))
                val available = getInt(getColumnIndexOrThrow(DB_FIELD_AVAILABLE))
                val book = Book(title, author, available)
                book.id = id
                books.add(book)
            }
        }

        return books
    }

    fun addBook(book: Book): Long {

        var id: Long = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_TITLE, book.title)
            put(DB_FIELD_AUTHOR, book.author)
            put(DB_FIELD_AVAILABLE, DB_VALUE_ACTIVE)
            put(DB_FIELD_ACTIVE, DB_VALUE_ACTIVE)
        }

        db.beginTransaction()
        try {
            id = db.insert(DB_TABLE_BOOKS, null, values)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return id
    }

    fun editBook(book: Book): Int {

        var count = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_TITLE, book.title)
            put(DB_FIELD_AUTHOR, book.author)
        }
        val selection = "$DB_FIELD_ID = ?"
        val selectionArgs = arrayOf(book.id.toString())
        db.beginTransaction()
        try {
            count = db.update(DB_TABLE_BOOKS, values, selection, selectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return count
    }

    fun removeBook(book: Book): Int {

        var count = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_ACTIVE, DB_VALUE_INACTIVE)
        }
        val selection = "$DB_FIELD_ID = ?"
        val selectionArgs = arrayOf(book.id.toString())
        db.beginTransaction()
        try {
            count = db.update(DB_TABLE_BOOKS, values, selection, selectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return count
    }

    // STUDENT
    fun getStudents(): MutableList<Student> {

        var students = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $DB_TABLE_STUDENTS " +
            " WHERE $DB_FIELD_ACTIVE = $DB_VALUE_ACTIVE " +
            " ORDER BY $DB_FIELD_NAME ",
            null,
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val name = getString(getColumnIndexOrThrow(DB_FIELD_NAME))
                val phone = getString(getColumnIndexOrThrow(DB_FIELD_PHONE))
                val available = getInt(getColumnIndexOrThrow(DB_FIELD_AVAILABLE))
                val student = Student(name, phone, available)
                student.id = id
                students.add(student)
            }
        }

        return students
    }

    fun getAvailableStudents(): MutableList<Student> {

        var students = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $DB_TABLE_STUDENTS " +
            " WHERE $DB_FIELD_ACTIVE = $DB_VALUE_ACTIVE " +
            " AND $DB_FIELD_AVAILABLE = $DB_VALUE_ACTIVE " +
            " ORDER BY $DB_FIELD_NAME ",
            null,
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val name = getString(getColumnIndexOrThrow(DB_FIELD_NAME))
                val phone = getString(getColumnIndexOrThrow(DB_FIELD_PHONE))
                val available = getInt(getColumnIndexOrThrow(DB_FIELD_AVAILABLE))
                val student = Student(name, phone, available)
                student.id = id
                students.add(student)
            }
        }

        return students
    }

    fun addStudent(student: Student): Long {

        var id: Long = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_NAME, student.name)
            put(DB_FIELD_PHONE, student.phone)
            put(DB_FIELD_AVAILABLE, DB_VALUE_ACTIVE)
            put(DB_FIELD_ACTIVE, DB_VALUE_ACTIVE)
        }

        db.beginTransaction()
        try {
            id = db.insert(DB_TABLE_STUDENTS, null, values)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return id
    }

    fun editStudent(student: Student): Int {

        var count = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_NAME, student.name)
            put(DB_FIELD_PHONE, student.phone)
        }
        val selection = "$DB_FIELD_ID = ?"
        val selectionArgs = arrayOf(student.id.toString())
        db.beginTransaction()
        try {
            count = db.update(DB_TABLE_STUDENTS, values, selection, selectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return count
    }

    fun removeStudent(student: Student): Int {

        var count = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_ACTIVE, DB_VALUE_INACTIVE)
        }
        val selection = "$DB_FIELD_ID = ?"
        val selectionArgs = arrayOf(student.id.toString())
        db.beginTransaction()
        try {
            count = db.update(DB_TABLE_STUDENTS, values, selection, selectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return count
    }

    // LOAN
    fun getLoans(): MutableList<Loan> {

        var loans = mutableListOf<Loan>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $DB_TABLE_LOANS.*, $DB_TABLE_BOOKS.$DB_FIELD_TITLE, " +
            " $DB_TABLE_STUDENTS.$DB_FIELD_NAME FROM $DB_TABLE_LOANS " +
            " INNER JOIN $DB_TABLE_BOOKS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_BOOK = $DB_TABLE_BOOKS.$DB_FIELD_ID " +
            " INNER JOIN $DB_TABLE_STUDENTS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_STUDENT = $DB_TABLE_STUDENTS.$DB_FIELD_ID ",
            null,
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val book = getLong(getColumnIndexOrThrow(DB_FIELD_BOOK))
                val student = getLong(getColumnIndexOrThrow(DB_FIELD_STUDENT))
                val title = getString(getColumnIndexOrThrow(DB_FIELD_TITLE))
                val name = getString(getColumnIndexOrThrow(DB_FIELD_NAME))
                val active = getInt(getColumnIndexOrThrow(DB_FIELD_ACTIVE))
                val loan = Loan(book, student, title, name, active)
                loan.id = id
                loans.add(loan)
            }
        }

        return loans
    }

    fun getLoansFromBook(book: Book): MutableList<Loan> {

        var loans = mutableListOf<Loan>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $DB_TABLE_LOANS.*, $DB_TABLE_BOOKS.$DB_FIELD_TITLE, " +
            " $DB_TABLE_STUDENTS.$DB_FIELD_NAME FROM $DB_TABLE_LOANS " +
            " INNER JOIN $DB_TABLE_BOOKS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_BOOK = $DB_TABLE_BOOKS.$DB_FIELD_ID " +
            " INNER JOIN $DB_TABLE_STUDENTS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_STUDENT = $DB_TABLE_STUDENTS.$DB_FIELD_ID " +
            " WHERE $DB_TABLE_BOOKS.$DB_FIELD_ID = ? ",
            arrayOf(book.id.toString()),
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val book = getLong(getColumnIndexOrThrow(DB_FIELD_BOOK))
                val student = getLong(getColumnIndexOrThrow(DB_FIELD_STUDENT))
                val title = getString(getColumnIndexOrThrow(DB_FIELD_TITLE))
                val name = getString(getColumnIndexOrThrow(DB_FIELD_NAME))
                val active = getInt(getColumnIndexOrThrow(DB_FIELD_ACTIVE))
                val loan = Loan(book, student, title, name, active)
                loan.id = id
                loans.add(loan)
            }
        }

        return loans
    }

    fun getLoansFromStudent(student: Student): MutableList<Loan> {

        var loans = mutableListOf<Loan>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $DB_TABLE_LOANS.*, $DB_TABLE_BOOKS.$DB_FIELD_TITLE, " +
            " $DB_TABLE_STUDENTS.$DB_FIELD_NAME FROM $DB_TABLE_LOANS " +
            " INNER JOIN $DB_TABLE_BOOKS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_BOOK = $DB_TABLE_BOOKS.$DB_FIELD_ID " +
            " INNER JOIN $DB_TABLE_STUDENTS " +
            " ON $DB_TABLE_LOANS.$DB_FIELD_STUDENT = $DB_TABLE_STUDENTS.$DB_FIELD_ID " +
            " WHERE $DB_TABLE_STUDENTS.$DB_FIELD_ID = ? ",
            arrayOf(student.id.toString()),
        )

        with(cursor) {
            while (moveToNext()) {

                val id = getLong(getColumnIndexOrThrow(DB_FIELD_ID))
                val book = getLong(getColumnIndexOrThrow(DB_FIELD_BOOK))
                val student = getLong(getColumnIndexOrThrow(DB_FIELD_STUDENT))
                val title = getString(getColumnIndexOrThrow(DB_FIELD_TITLE))
                val name = getString(getColumnIndexOrThrow(DB_FIELD_NAME))
                val active = getInt(getColumnIndexOrThrow(DB_FIELD_ACTIVE))
                val loan = Loan(book, student, title, name, active)
                loan.id = id
                loans.add(loan)
            }
        }

        return loans
    }

    fun addLoan(loan: Loan): Long {

        var id: Long = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_BOOK, loan.book)
            put(DB_FIELD_STUDENT, loan.student)
            put(DB_FIELD_ACTIVE, DB_VALUE_ACTIVE)
        }

        val bookValues = ContentValues().apply {
            put(DB_FIELD_AVAILABLE, DB_VALUE_INACTIVE)
        }
        val bookSelection = "$DB_FIELD_ID = ?"
        val bookSelectionArgs = arrayOf(loan.book.toString())

        val studentValues = ContentValues().apply {
            put(DB_FIELD_AVAILABLE, DB_VALUE_INACTIVE)
        }
        val studentSelection = "$DB_FIELD_ID = ?"
        val studentSelectionArgs = arrayOf(loan.student.toString())

        db.beginTransaction()
        try {
            id = db.insert(DB_TABLE_LOANS, null, values)
            db.update(DB_TABLE_BOOKS, bookValues, bookSelection, bookSelectionArgs)
            db.update(DB_TABLE_STUDENTS, studentValues, studentSelection, studentSelectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return id
    }

    fun editLoan(loan: Loan): Int {

        var count = 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_FIELD_ACTIVE, DB_VALUE_INACTIVE)
        }
        val selection = "$DB_FIELD_ID = ?"
        val selectionArgs = arrayOf(loan.id.toString())

        val bookValues = ContentValues().apply {
            put(DB_FIELD_AVAILABLE, DB_VALUE_ACTIVE)
        }
        val bookSelection = "$DB_FIELD_ID = ?"
        val bookSelectionArgs = arrayOf(loan.book.toString())

        val studentValues = ContentValues().apply {
            put(DB_FIELD_AVAILABLE, DB_VALUE_ACTIVE)
        }
        val studentSelection = "$DB_FIELD_ID = ?"
        val studentSelectionArgs = arrayOf(loan.student.toString())

        db.beginTransaction()
        try {
            count = db.update(DB_TABLE_LOANS, values, selection, selectionArgs)
            db.update(DB_TABLE_BOOKS, bookValues, bookSelection, bookSelectionArgs)
            db.update(DB_TABLE_STUDENTS, studentValues, studentSelection, studentSelectionArgs)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }

        return count
    }
}