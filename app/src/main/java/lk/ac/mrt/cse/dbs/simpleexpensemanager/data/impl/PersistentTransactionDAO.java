package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private SQLiteDatabase database;

    public PersistentTransactionDAO(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_number",accountNo);
        contentValues.put("expense_type",(expenseType == ExpenseType.INCOME)?0:1);
        contentValues.put("amount",amount);
        contentValues.put("transaction_date",simpleDateFormat.format(date));
        database.insert("transaction_log",null,contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * from transaction_log";
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst()){
            while (true){
                Date date = new Date(cursor.getLong(0));
                String accountNumber = cursor.getString(1);
                ExpenseType expenseType = ExpenseType.INCOME;
                if(cursor.getInt(2) == 1){
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = cursor.getDouble(3);
                Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);
                transactions.add(transaction);
                if(cursor.moveToNext()){
                    break;
                }
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM transaction_log LIMIT " + limit, null);
        if(cursor.moveToFirst()){
            while (true){
                Date date = new Date(cursor.getLong(0));
                String acc_no = cursor.getString(1);
                ExpenseType expenseType = ExpenseType.INCOME;
                if (cursor.getInt(2) == 1) {
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = cursor.getDouble(3);
                Transaction transaction = new Transaction(date, acc_no, expenseType, amount);
                transactions.add(transaction);
                if(!cursor.moveToNext()){
                    break;
                }
            }
        }
        cursor.close();
        return transactions;
    }
}
