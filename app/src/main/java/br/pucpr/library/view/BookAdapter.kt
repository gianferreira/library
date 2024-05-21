package br.pucpr.library.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.pucpr.library.databinding.AdapterBookBinding
import br.pucpr.library.model.Book

class BookAdapter(var books: MutableList<Book>) : RecyclerView.Adapter<BookAdapter.BookHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {

        AdapterBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false).apply {
            return BookHolder(this)
        }
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {

        books[position].apply {
            holder.binding.txtTitle.text = this.title
            holder.binding.txtAuthor.text = this.author
        }
    }

    override fun getItemCount() = books.size

    inner class BookHolder(var binding: AdapterBookBinding): RecyclerView.ViewHolder(binding.root)
}