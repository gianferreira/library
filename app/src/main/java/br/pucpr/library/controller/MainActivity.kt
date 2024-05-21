package br.pucpr.library.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import br.pucpr.library.databinding.ActivityMainBinding
import br.pucpr.library.model.DataStore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataStore.setContext(this)
        configureLastLoanInfo()
        configureBooksButton()
        configureStudentsButton()
        configureLoansButton()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).run {
            setMessage("Tem certeza que deseja sair do aplicativo?")
            setPositiveButton("OK") { _,_ ->
                finish()
            }
            setNegativeButton("Cancelar", null)
            show()
        }
    }

    private fun configureBooksButton() {
        binding.btnBooks.setOnClickListener {
            startActivity(Intent(this, ListBooks::class.java));
            finish()
        }
    }

    private fun configureStudentsButton() {
        binding.btnStudents.setOnClickListener {
            startActivity(Intent(this, ListStudents::class.java));
            finish()
        }
    }

    private fun configureLoansButton() {
        binding.btnLoans.setOnClickListener {
            startActivity(Intent(this, ListLoans::class.java));
            finish()
        }
    }

    private fun configureLastLoanInfo() {
        if(DataStore.loans.isNotEmpty()) {
            val lastLoan = DataStore.lastLoan()

            binding.loanMessage.text = "Último empréstimo realizado:"
            binding.txtBook.text = lastLoan.bookTitle
            binding.txtStudent.text = lastLoan.studentName
        } else {
            binding.loanMessage.text = "Nenhum empréstimo realizado ainda!"
            binding.borrowInfo.isVisible = false
        }
    }
}
