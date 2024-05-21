package br.pucpr.library.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import br.pucpr.library.databinding.ActivityManageStudentBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.model.Student
import br.pucpr.library.view.LoanAdapter

class ManageStudent : AppCompatActivity() {

    private lateinit var binding: ActivityManageStudentBinding
    private lateinit var adapter: LoanAdapter
    private var position = -1
    private val activeValue = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getIntExtra("position", -1).apply {
            position = this
            if (position != -1) {
                setData(position)
                val student = DataStore.getStudent(position)
                if(student.available != activeValue) {
                    binding.btnAdd.isVisible = false
                }
                loadRecycleView()
            }
            else {
                binding.loansManager.isVisible = false
            }
        }

        binding.btnSave.setOnClickListener {
            getData()?.let { student ->
                saveStudent(student)
            } ?: run {
                showMessage("Campos inválidos! \n\nPreencha corretamente o NOME e TELEFONE DO ALUNO.")
            }
        }
        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AvailableBooks::class.java).putExtra("studentPosition", position));
            finish()
        }
    }

    private fun loadRecycleView() {

        LinearLayoutManager(this).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            binding.revLoans.layoutManager = this
            DataStore.getLoansFromStudent(DataStore.getStudent(position))
            adapter = LoanAdapter(DataStore.loans, isStudent = true).apply {
                binding.revLoans.adapter = this
            }
        }
    }

    private fun getData(): Student? {

        val name = binding.txtName.text.toString()
        val phone = binding.txtPhone.text.toString()

        if (name.isEmpty() || phone.isEmpty())
            return null

        return Student(name, phone, activeValue)
    }

    private fun setData(position: Int) {

        DataStore.getStudent(position).run {
            binding.txtName.setText(this.name)
            binding.txtPhone.setText(this.phone)
        }
    }

    private fun saveStudent(student: Student) {

        if (position == -1)
            DataStore.addStudent(student)
        else
            DataStore.editStudent(position, student)
        Intent().run {
            putExtra("student", student.name)
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