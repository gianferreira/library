package br.pucpr.library.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import br.pucpr.library.databinding.ActivityManageBookBinding
import br.pucpr.library.model.Book
import br.pucpr.library.model.DataStore
import br.pucpr.library.view.LoanAdapter

class ManageBook : AppCompatActivity() {

    private lateinit var binding: ActivityManageBookBinding
    private lateinit var adapter: LoanAdapter
    private var position = -1
    private val activeValue = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getIntExtra("position", -1).apply {
            position = this
            if (position != -1) {
                setData(position)
                val book = DataStore.getBook(position)
                if(book.available != activeValue) {
                    binding.btnAdd.isVisible = false
                }
                loadRecycleView()
            }
            else {
                binding.loansManager.isVisible = false
            }
        }

        binding.btnSave.setOnClickListener {
            getData()?.let { book ->
                saveBook(book)
            } ?: run {
                showMessage("Campos inválidos! \n\nPreencha corretamente o TITULO e AUTOR DO LIVRO.")
            }
        }
        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AvailableStudents::class.java).putExtra("bookPosition", position));
            finish()
        }
    }

    private fun loadRecycleView() {

        LinearLayoutManager(this).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            binding.revLoans.layoutManager = this
            DataStore.getLoansFromBook(DataStore.getBook(position))
            adapter = LoanAdapter(DataStore.loans, isBook = true).apply {
                binding.revLoans.adapter = this
            }
        }
    }

    private fun getData(): Book? {

        val title = binding.txtTitle.text.toString()
        val author = binding.txtAuthor.text.toString()

        if (title.isEmpty() || author.isEmpty())
            return null

        return Book(title, author, activeValue)
    }

    private fun setData(position: Int) {

        DataStore.getBook(position).run {
            binding.txtTitle.setText(this.title)
            binding.txtAuthor.setText(this.author)
        }
    }

    private fun saveBook(book: Book) {

        if (position == -1)
            DataStore.addBook(book)
        else
            DataStore.editBook(position, book)
        Intent().run {
            putExtra("book", book.title)
            setResult(RESULT_OK, this)
        }

        finish()
    }

    private fun showMessage(message: String) {

        AlertDialog.Builder(this).run {
            title = "Library"
            setMessage(message)
            setPositiveButton("Ok", null)
            show()
        }
    }
}