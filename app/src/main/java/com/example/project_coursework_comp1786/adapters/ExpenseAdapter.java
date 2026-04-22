package com.example.project_coursework_comp1786.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense, listener);
    }

    @Override
    public int getItemCount() {
        return expenseList == null ? 0 : expenseList.size();
    }

    public void setExpenses(List<Expense> newExpenses) {
        this.expenseList = newExpenses;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvAmount, tvStatus;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExpTitle);
            tvSubtitle = itemView.findViewById(R.id.tvExpSubtitle);
            tvAmount = itemView.findViewById(R.id.tvExpAmount);
            tvStatus = itemView.findViewById(R.id.tvExpStatus);
        }

        public void bind(Expense expense, OnExpenseClickListener listener) {
            // Hiển thị Mô tả hoặc Loại chi phí
            String title = (expense.getDescription() != null && !expense.getDescription().isEmpty())
                    ? expense.getDescription() : expense.getType();
            tvTitle.setText(title);

            // LOGIC MỚI: Hiển thị Ngày, Loại và NGƯỜI CHI (Claimant)
            String claimant = (expense.getClaimant() != null && !expense.getClaimant().isEmpty())
                    ? expense.getClaimant() : "Unknown";
            tvSubtitle.setText(expense.getDate() + " • " + expense.getType() + " • By: " + claimant);

            tvAmount.setText(expense.getCurrency() + " " + String.format("%,.2f", expense.getAmount()));

            tvStatus.setText(expense.getStatus().toUpperCase());

            // Xử lý màu sắc status
            if (expense.getStatus().equalsIgnoreCase("Pending")) {
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"));
            } else {
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onExpenseClick(expense);
            });
        }
    }
}