package br.pucpr.library.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.pucpr.library.databinding.AdapterStudentBinding
import br.pucpr.library.model.Student

class StudentAdapter(var students: MutableList<Student>) : RecyclerView.Adapter<StudentAdapter.StudentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {

        AdapterStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false).apply {
            return StudentHolder(this)
        }
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {

        students[position].apply {
            holder.binding.txtName.text = this.name
            holder.binding.txtPhone.text = this.phone
        }
    }

    override fun getItemCount() = students.size

    inner class StudentHolder(var binding: AdapterStudentBinding): RecyclerView.ViewHolder(binding.root)
}