package br.pucpr.library.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import br.pucpr.library.databinding.ActivityListBooksBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.view.BookAdapter
import com.google.android.material.snackbar.Snackbar

class ListBooks : AppCompatActivity() {

    private lateinit var binding: ActivityListBooksBinding
    private lateinit var adapter: BookAdapter
    private lateinit var gesture: GestureDetector
    private val activeValue = 1

    private val addBookResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        adapter.notifyDataSetChanged()
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                Snackbar.make(
                    this,
                    binding.layout,
                    "Livro ${intent.getStringExtra("book")} adicionado com sucesso!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private var editBookResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        adapter.notifyDataSetChanged()
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                Snackbar.make(
                    this,
                    binding.layout,
                    "Livro ${intent.getStringExtra("book")} alterado com sucesso!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataStore.setContext(this)
        loadRecycleView()
        configureFab()
        configureGesture()
        configureRecycleViewEvents()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java));
        finish()
    }

    private fun loadRecycleView() {

        LinearLayoutManager(this).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            binding.revBooks.layoutManager = this
            adapter = BookAdapter(DataStore.books).apply {
                binding.revBooks.adapter = this
            }
        }
    }

    private fun configureGesture() {

        gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.revBooks.findChildViewUnder(e.x, e.y).run {
                    this?.let { child ->
                        binding.revBooks.getChildAdapterPosition(child).apply {
                            Intent(this@ListBooks, ManageBook::class.java).run {
                                putExtra("position", this@apply)
                                editBookResult.launch(this)
                            }
                        }
                    }
                }
                return super.onSingleTapConfirmed(e)
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)

                binding.revBooks.findChildViewUnder(e.x, e.y).run {
                    this?.let { child ->
                        binding.revBooks.getChildAdapterPosition(child).apply {
                            val book = DataStore.getBook(this)

                            if(book.available == activeValue) {
                                AlertDialog.Builder(this@ListBooks).run {
                                    setMessage("Tem certeza que deseja remover este livro?")
                                    setPositiveButton("Excluir") { _,_ ->
                                        DataStore.removeBook(this@apply)
                                        Toast.makeText(this@ListBooks, "Livro ${book.title} removido com sucesso!!!", Toast.LENGTH_LONG).show()
                                        adapter.notifyDataSetChanged()
                                    }
                                    setNegativeButton("Cancelar", null)
                                    show()
                                }
                            } else {
                                AlertDialog.Builder(this@ListBooks).run {
                                    setMessage("Esse livro está emprestado, devolva o livro antes de removê-lo da biblioteca!")
                                    setPositiveButton("OK", null)
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun configureRecycleViewEvents() {

        binding.revBooks.addOnItemTouchListener(object: OnItemTouchListener {

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y).apply {
                    return (this != null && gesture.onTouchEvent(e))
                }
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun configureFab() {
        binding.fab.setOnClickListener {
            Intent(this, ManageBook::class.java).run {
                addBookResult.launch(this)
            }
        }
    }
}