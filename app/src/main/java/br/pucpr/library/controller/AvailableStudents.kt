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
import br.pucpr.library.databinding.ActivityAvailableStudentsBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.model.Loan
import br.pucpr.library.view.StudentAdapter

class AvailableStudents : AppCompatActivity() {

    private lateinit var binding: ActivityAvailableStudentsBinding
    private lateinit var adapter: StudentAdapter
    private lateinit var gesture: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAvailableStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataStore.setContext(this)
        loadRecycleView()
        configureGesture()
        configureRecycleViewEvents()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ListBooks::class.java))
        finish()
    }

    private fun loadRecycleView() {

        LinearLayoutManager(this).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            binding.revStudents.layoutManager = this

            var availableStudents = DataStore.getAvailableStudents()

            adapter = StudentAdapter(availableStudents).apply {
                binding.revStudents.adapter = this
            }
        }
    }

    private fun configureGesture() {

        gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.revStudents.findChildViewUnder(e.x, e.y).run {
                    this?.let { child ->
                        binding.revStudents.getChildAdapterPosition(child).apply {
                            val student = DataStore.getStudent(this)
                            val bookPosition = intent.getIntExtra("bookPosition", -1)
                            val book = DataStore.getBook(bookPosition)

                            AlertDialog.Builder(this@AvailableStudents).run {
                                setMessage("Deseja emprestar o livro a este aluno?")
                                setPositiveButton("Sim") { _,_ ->
                                    adapter.notifyDataSetChanged()
                                    DataStore.addLoan(Loan(book.id, student.id))
                                    startActivity(Intent(this@AvailableStudents, ListLoans::class.java));
                                    Toast.makeText(this@AvailableStudents, "Empréstimo realizado com sucesso!", Toast.LENGTH_LONG).show()
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

        binding.revStudents.addOnItemTouchListener(object: OnItemTouchListener {

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