package com.example.feature.transactions.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.FinancialTransaction
import com.example.feature.transactions.R

class TransactionsAdapter(
    private val onItemClick: (FinancialTransaction) -> Unit,
    private val onItemLongClick: (FinancialTransaction) -> Unit
) : ListAdapter<FinancialTransaction, TransactionsAdapter.TransactionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onItemLongClick)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.transactionTitle)
        private val amount: TextView = itemView.findViewById(R.id.transactionAmount)

        fun bind(
            item: FinancialTransaction,
            onItemClick: (FinancialTransaction) -> Unit,
            onItemLongClick: (FinancialTransaction) -> Unit
        ) {
            title.text = item.description
            amount.text = "${item.type}: ${item.amount}"
            itemView.setOnClickListener { onItemClick(item) }
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<FinancialTransaction>() {
        override fun areItemsTheSame(oldItem: FinancialTransaction, newItem: FinancialTransaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FinancialTransaction, newItem: FinancialTransaction): Boolean {
            return oldItem == newItem
        }
    }
}
