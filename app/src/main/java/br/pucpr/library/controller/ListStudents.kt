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
import br.pucpr.library.databinding.ActivityListStudentsBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.view.StudentAdapter
import com.google.android.material.snackbar.Snackbar

class ListStudents : AppCompatActivity() {

    private lateinit var binding: ActivityListStudentsBinding
    private lateinit var adapter: StudentAdapter
    private lateinit var gesture: GestureDetector
    private val activeValue = 1

    private val addStudentResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        adapter.notifyDataSetChanged()
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                Snackbar.make(
                    this,
                    binding.layout,
                    "Aluno ${intent.getStringExtra("student")} adicionado com sucesso!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private var editStudentResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        adapter.notifyDataSetChanged()
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                Snackbar.make(
                    this,
                    binding.layout,
                    "Aluno ${intent.getStringExtra("student")} alterado com sucesso!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListStudentsBinding.inflate(layoutInflater)
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
            binding.revStudents.layoutManager = this
            adapter = StudentAdapter(DataStore.students).apply {
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
                            Intent(this@ListStudents, ManageStudent::class.java).run {
                                putExtra("position", this@apply)
                                editStudentResult.launch(this)
                            }
                        }
                    }
                }
                return super.onSingleTapConfirmed(e)
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)

                binding.revStudents.findChildViewUnder(e.x, e.y).run {
                    this?.let { child ->
                        binding.revStudents.getChildAdapterPosition(child).apply {
                            val student = DataStore.getStudent(this)

                            if(student.available == activeValue) {
                                AlertDialog.Builder(this@ListStudents).run {
                                    setMessage("Tem certeza que deseja remover este aluno?")
                                    setPositiveButton("Excluir") { _,_ ->
                                        DataStore.removeStudent(this@apply)
                                        Toast.makeText(this@ListStudents, "Aluno ${student.name} removido com sucesso!!!", Toast.LENGTH_LONG).show()
                                        adapter.notifyDataSetChanged()
                                    }
                                    setNegativeButton("Cancelar", null)
                                    show()
                                }
                            } else {
                                AlertDialog.Builder(this@ListStudents).run {
                                    setMessage("Esse aluno está com um livro emprestado, é neeessário devolver o livro antes de removê-lo!")
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

    private fun configureFab() {
        binding.fab.setOnClickListener {
            Intent(this, ManageStudent::class.java).run {
                addStudentResult.launch(this)
            }
        }
    }
}