package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{

    private Context context;

    public PersistentExpenseManager(Context context){
        this.context = context;
        setup();
    }

    @Override
    public void setup(){
        SQLiteDatabase database = context.openOrCreateDatabase("190146K",Context.MODE_PRIVATE,null);
        String sqlQ1 = "CREATE TABLE IF NOT EXISTS account(account_number VARCHAR PRIMARY KEY, bank_name VARCHAR, account_holder VARCHAR, balance REAL);";
        String sqlQ2 = "CREATE TABLE IF NOT EXISTS transaction_log(transaction_id INTEGER PRIMARY KEY, account_number VARCHAR, expense_type INT, amount REAL, transaction_date DATE, FOREIGN KEY (account_number) REFERENCES account(account_number));";

        database.execSQL(sqlQ1);
        database.execSQL(sqlQ2);

        setAccountsDAO(new PersistentAccountDAO(database));
        setTransactionsDAO(new PersistentTransactionDAO(database));
    }
}
