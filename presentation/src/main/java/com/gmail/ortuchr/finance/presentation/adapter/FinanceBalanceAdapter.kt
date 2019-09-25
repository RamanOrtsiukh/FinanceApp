package com.gmail.ortuchr.finance.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat

class FinanceBalanceAdapter(var onItemClick: (Balance) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<FinanceBalanceAdapter.Holder>() {

    var financeBalanceDataSet: List<Balance> = emptyList()

    fun setData(data: List<Balance>) {
        financeBalanceDataSet = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): FinanceBalanceAdapter.Holder {
                val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_single_balance, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = financeBalanceDataSet.size

    override fun onBindViewHolder(holder: FinanceBalanceAdapter.Holder, position: Int) {
        val balance = financeBalanceDataSet[position]
        holder.itemDate.text = balance.date
        if (balance.type == "income") {
            holder.itemType.text = App.instance.getString(R.string.incomeTitle)
            holder.itemAmount.setTextColor(ContextCompat.getColor(App.instance.applicationContext, R.color.green))
        } else {
            holder.itemType.text = balance.type
            holder.itemAmount.setTextColor(ContextCompat.getColor(App.instance.applicationContext, R.color.red))
        }
        holder.itemComment.text = balance.comment
        holder.itemAmount.text = setMoneyDisplayFormat(balance.amount)
        holder.itemView.setOnClickListener {
            onItemClick(balance)
        }
    }

    inner class Holder : androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val itemDate: TextView
        val itemType: TextView
        val itemComment: TextView
        val itemAmount: TextView

        constructor(view: View) : super(view) {
            itemDate = view.findViewById(R.id.itemDate)
            itemType = view.findViewById(R.id.itemType)
            itemComment = view.findViewById(R.id.itemComment)
            itemAmount = view.findViewById(R.id.itemAmount)
        }
    }
}