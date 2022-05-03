package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private SQLiteDatabase database;

    public PersistentAccountDAO(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> account_numbers = new ArrayList<>();
        Cursor cursor = database.query("account",new String[]{"account_number"},null,null,null,null,null);
        if(cursor.moveToFirst()){
            while (true){
                account_numbers.add(cursor.getString(0));
                if(!cursor.moveToNext()){
                    break;
                }

            }
        }
        cursor.close();
        return account_numbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = database.query("account",new String[]{"account_number","bank_name","account_holder","balance"},null,null,null,null,null);
        if(cursor.moveToFirst()){
            while (true){
                accounts.add(new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3)));
                if(!cursor.moveToFirst()){
                    break;
                }
            }

        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account;
        Cursor cursor = database.query("account",new String[]{"account_number","bank_name","account_holder","balance"},"account_number=?",new String[]{accountNo},null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_number",account.getAccountNo());
        contentValues.put("bank_name",account.getBankName());
        contentValues.put("account_holder",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        database.insert("account",null,contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        database.delete("account","account_number=?",new String[]{accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Cursor cursor = database.query("account",new String[]{"account_number","bank_name","account_holder","balance"},"account_number=?",new String[]{accountNo},null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        double balance = cursor.getDouble(3);
        ContentValues contentValues = new ContentValues();
        if(expenseType == ExpenseType.EXPENSE){
            contentValues.put("balance",balance-amount);
        }else{
            contentValues.put("balance",balance+amount);
        }
        cursor.close();
    }
}
