package com.example.myapplication2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TransactionListener{
    private TextView tvSign, tvEmpty, tvBalance;
    private EditText etAmount, etMessage;
    private ImageView ivSend;
    private boolean positive = true;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private ArrayList<TransactionClass> transactionList;
    private Spinner spCategory;
    private ArrayAdapter<CharSequence> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        spCategory = findViewById(R.id.spCategory);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.income_categories, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter2);

        loadData();

        rvTransactions.setHasFixedSize(true);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        try {
            adapter = new TransactionAdapter(this, transactionList, this);
            rvTransactions.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("AdapterError", e.getMessage());
            e.printStackTrace();
        }

        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSign();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etAmount.getText().toString().trim().isEmpty())
                {
                    etAmount.setError("Введите сумму!");
                    return;
                }
                try {
                    int amt = Integer.parseInt(etAmount.getText().toString().trim());
                    String category = spCategory.getSelectedItem().toString(); // Получение выбранной категории

                    // Добавление транзакции в RecyclerView
                    sendTransaction(amt, etMessage.getText().toString().trim(), positive, category);
                    checkIfEmpty(transactionList.size());

                    // Обновление баланса
                    setBalance(transactionList);
                    etAmount.setText("");
                    etMessage.setText("");
                }
                catch (Exception e){
                    etAmount.setError("Сумма должна быть целым числом!");
                }
            }
        });
        final TextView tvCategoryLabel = findViewById(R.id.tvCategoryLabel);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Обновление текста в поле "Категория" при выборе элемента из Spinner
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                tvCategoryLabel.setText(selectedCategory);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        tvCategoryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызов события клика для spCategory
                spCategory.performClick();
            }
        });
        checkIfEmpty(transactionList.size());
        setBalance(transactionList);
    }
    public void setBalance(ArrayList<TransactionClass> transactionList){ //изменение знака перед балансом
        int bal = calculateBalance(transactionList);
        if(bal<0)
        {
            tvBalance.setText("-₽ "+calculateBalance(transactionList)*-1);
        }
        else {
            tvBalance.setText("+₽ "+calculateBalance(transactionList));
        }
    }
    private void loadData() {
        SharedPreferences pref = getSharedPreferences("com.cs.ec", MODE_PRIVATE);//загрузка последних сохраненных данных
        // Получение SharedPreferences
        Gson gson = new Gson();

        String json = pref.getString("transactions", null);// Получение списка транзакций в формате JSON
        Type type = new TypeToken<ArrayList<TransactionClass>>(){}.getType();// Получение типа списка транзакций

        // Если список транзакций не пустой, загружаем его
        if(json != null) {
            transactionList = gson.fromJson(json, type);
        } else { // Иначе создаем новый пустой список
            transactionList = new ArrayList<>();
        }
    }
    private void sendTransaction(int amt, String msg, boolean positive, String category) {
        transactionList.add(new TransactionClass(amt, msg, positive, category));// Создание новой транзакции и добавление ее в список
        adapter.notifyDataSetChanged();// Уведомление адаптера
        rvTransactions.smoothScrollToPosition(transactionList.size() - 1);// Прокрутка к последней добавленной транзакции
    }
    private void changeSign() { //смена расходов и доходов
        if (positive) {
            tvSign.setText("-₽");
            tvSign.setTextColor(Color.parseColor("#F44336"));
            // Изменяем адаптер спиннера
            adapter2 = ArrayAdapter.createFromResource(this, R.array.expense_categories, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(adapter2);
            positive = false;
        } else {
            tvSign.setText("+₽");
            tvSign.setTextColor(Color.parseColor("#00c853"));
            adapter2 = ArrayAdapter.createFromResource(this, R.array.income_categories, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(adapter2);
            positive = true;
        }
    }
    public void checkIfEmpty(int size) {// Проверка списка транзакций на пустоту
        if (size == 0)
        {
            tvEmpty.setVisibility(View.VISIBLE);//текст о том что нет транзакций
        }
        else {
            tvEmpty.setVisibility(View.GONE);//скрыть текст
        }
    }
    private int calculateBalance(ArrayList<TransactionClass> transactionList)//вычисление баланса
    {
        int bal = 0;
        for(TransactionClass transaction : transactionList)
        {
            if(transaction.isPositive())
            {
                bal+=transaction.getAmount();
            }
            else {
                bal-=transaction.getAmount();
            }
        }
        return bal;
    }
    private void initViews() { //инициализация объектов
        tvSign = findViewById(R.id.tvSign);
        rvTransactions = findViewById(R.id.rvTransactions);
        etAmount = findViewById(R.id.etAmount);
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvBalance = findViewById(R.id.tvBalance);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Получение редактора SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("com.cs.ec",MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(transactionList);
        //сохранение транзакций
        editor.putString("transactions",json);
        editor.apply();
    }
}
