package com.example.myapplication2;

import java.util.ArrayList;

public interface TransactionListener {
    void checkIfEmpty(int size);
    void setBalance(ArrayList<TransactionClass> transactionList);
}
