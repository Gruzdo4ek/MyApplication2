package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TViewHolder> {
    private final Context ctx;
    private TransactionListener listener;
    private final ArrayList<TransactionClass> transactionList;
    public TransactionAdapter(Context ctx, ArrayList<TransactionClass> transactionList, TransactionListener listener) {
        this.ctx = ctx;
        this.transactionList = transactionList;
        this.listener = listener;
    }
    public TransactionAdapter(Context ctx, ArrayList<TransactionClass> transactionList) {
        this.ctx = ctx;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionAdapter.TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.transaction_row_layout, parent, false);
        return new TransactionAdapter.TViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TViewHolder holder, int position) {
        // Получение текущей транзакции
        TransactionClass currentTransaction = transactionList.get(holder.getAdapterPosition());

        // Формирование текста сообщения с категорией
        String message = currentTransaction.getMessage() + " (" + currentTransaction.getCategory() + ")";
        holder.tvMessage.setText(message);

        //установка цвета для транзакции
        if (currentTransaction.isPositive()) {
            holder.tvAmount.setTextColor(Color.parseColor("#00c853"));
            holder.tvAmount.setText("+₽ " + currentTransaction.getAmount());
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
            holder.tvAmount.setText("-₽ " + currentTransaction.getAmount());
        }

        // Обработчик нажатия на иконку удаления
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Подтверждение удаления транзакции
                AlertDialog dialog = new AlertDialog.Builder(ctx)
                        .setCancelable(false)
                        .setTitle("Вы уверены? Транзакция будет удалена.")
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Удаление транзакции из списка
                                transactionList.remove(holder.getAdapterPosition());
                                dialogInterface.dismiss();
                                notifyDataSetChanged();
                                // Проверка, если список пустой, и обновление баланса
                                listener.checkIfEmpty(transactionList.size());
                                listener.setBalance(transactionList);
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvMessage;
        ImageView ivDelete;

        public TViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
