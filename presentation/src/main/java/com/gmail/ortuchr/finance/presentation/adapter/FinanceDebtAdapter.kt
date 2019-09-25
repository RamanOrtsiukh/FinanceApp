package com.gmail.ortuchr.finance.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat

class FinanceDebtAdapter (var onItemClick: (Debt) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<FinanceDebtAdapter.Holder>() {

    var financeDebtDataSet: List<Debt> = emptyList()

    fun setData(data: List<Debt>) {
        financeDebtDataSet = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): FinanceDebtAdapter.Holder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_single_debt, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = financeDebtDataSet.size

    override fun onBindViewHolder(holder: FinanceDebtAdapter.Holder, position: Int) {
        val debt = financeDebtDataSet[position]

        holder.itemDateStart.text = debt.dateStart
        holder.itemDebtor.text = debt.debtor
        if (debt.type == "toMe") {
            holder.itemAmount.setTextColor(ContextCompat.getColor(App.instance.applicationContext, R.color.green))
        } else {
            holder.itemAmount.setTextColor(ContextCompat.getColor(App.instance.applicationContext, R.color.red))
        }
        holder.itemComment.text = debt.comment
        holder.itemAmount.text = setMoneyDisplayFormat(debt.amount)
        holder.itemView.setOnClickListener {
            onItemClick(debt)
        }
    }

    inner class Holder : androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val itemDateStart: TextView
        val itemDebtor: TextView
        val itemComment: TextView
        val itemAmount: TextView

        constructor(view: View) : super(view) {
            itemDateStart = view.findViewById(R.id.itemDateStart)
            itemDebtor = view.findViewById(R.id.itemDebtor)
            itemComment = view.findViewById(R.id.itemComment)
            itemAmount = view.findViewById(R.id.itemAmount)
        }
    }
}