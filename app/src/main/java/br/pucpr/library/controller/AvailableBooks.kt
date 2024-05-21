package br.pucpr.library.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import br.pucpr.library.databinding.ActivityAvailableBooksBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.model.Loan
import br.pucpr.library.view.BookAdapter

class AvailableBooks : AppCompatActivity() {

    private lateinit var binding: ActivityAvailableBooksBinding
    private lateinit var adapter: BookAdapter
    private lateinit var gesture: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAvailableBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataStore.setContext(this)
        loadRecycleView()
        configureGesture()
        configureRecycleViewEvents()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ListStudents::class.java))
        finish()
    }

    private fun loadRecycleView() {

        LinearLayoutManager(this).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            binding.revBooks.layoutManager = this

            var availableBooks = DataStore.getAvailableBooks()

            adapter = BookAdapter(availableBooks).apply {
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
                            val book = DataStore.getBook(this)
                            val studentPosition = intent.getIntExtra("studentPosition", -1)
                            val student = DataStore.getStudent(studentPosition)

                            AlertDialog.Builder(this@AvailableBooks).run {
                                setMessage("Deseja emprestar este livro ao aluno?")
                                setPositiveButton("Sim") { _,_ ->
                                    adapter.notifyDataSetChanged()
                                    DataStore.addLoan(Loan(book.id, student.id))
                                    startActivity(Intent(this@AvailableBooks, ListLoans::class.java));
                                    Toast.makeText(this@AvailableBooks, "Empréstimo realizado com sucesso!", Toast.LENGTH_LONG).show()
                                }
                                setNegativeButton("Cancelar", null)
                                show()
                            }
                        }
                    }
                }

                return super.onSingleTapConfirmed(e)
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
}