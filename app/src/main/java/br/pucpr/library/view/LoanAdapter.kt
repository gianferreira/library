package br.pucpr.library.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.pucpr.library.databinding.AdapterLoanBinding
import br.pucpr.library.model.Loan

class LoanAdapter(
    var loans: MutableList<Loan>,
    var isBook: Boolean = false,
    var isStudent: Boolean = false,
) : RecyclerView.Adapter<LoanAdapter.LoanHolder>() {

    private val activeValue = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanHolder {

        AdapterLoanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false).apply {
            return LoanHolder(this)
        }
    }

    override fun onBindViewHolder(holder: LoanHolder, position: Int) {

        loans[position].apply {
            holder.binding.txtBook.text = this.bookTitle
            holder.binding.txtStudent.text = this.studentName

            if(isBook) {
                holder.binding.txtTitle.isVisible = false
                holder.binding.txtBook.isVisible = false
            } else if (isStudent) {
                holder.binding.txtName.isVisible = false
                holder.binding.txtStudent.isVisible = false
            }

            if(this.active == activeValue) {
                holder.binding.txtLoanStatus.text = "Não devolvido"
            } else {
                holder.binding.txtLoanStatus.text = "Devolvido"
            }
        }
    }

    override fun getItemCount() = loans.size

    inner class LoanHolder(var binding: AdapterLoanBinding): RecyclerView.ViewHolder(binding.root)
}