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
import br.pucpr.library.databinding.ActivityListLoansBinding
import br.pucpr.library.model.DataStore
import br.pucpr.library.view.LoanAdapter

class ListLoans : AppCompatActivity() {

    private lateinit var binding: ActivityListLoansBinding
    private lateinit var adapter: LoanAdapter
    private lateinit var gesture: GestureDetector
    private val activeValue = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListLoansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataStore.setContext(this)
        loadRecycleView()
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
            binding.revLoans.layoutManager = this
            adapter = LoanAdapter(DataStore.loans).apply {
                binding.revLoans.adapter = this
            }
        }
    }

    private fun configureGesture() {

        gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.revLoans.findChildViewUnder(e.x, e.y).run {
                    this?.let { child ->
                        binding.revLoans.getChildAdapterPosition(child).apply {
                            val loan = DataStore.getLoan(this)

                            if(loan.active == activeValue) {
                                AlertDialog.Builder(this@ListLoans).run {
                                    setMessage("Deseja confirmar o devolvimento deste livro?")
                                    setPositiveButton("Sim") { _,_ ->
                                        DataStore.editLoan(this@apply, loan)
                                        Toast.makeText(this@ListLoans, "Livro devidamente devolvido!", Toast.LENGTH_LONG).show()
                                        adapter.notifyDataSetChanged()
                                        startActivity(Intent(this@ListLoans, MainActivity::class.java));
                                        finish()
                                    }
                                    setNegativeButton("Cancelar", null)
                                    show()
                                }
                            }
                        }
                    }
                }

                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun configureRecycleViewEvents() {

        binding.revLoans.addOnItemTouchListener(object: OnItemTouchListener {

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