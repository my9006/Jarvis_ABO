package com.jarvis_abo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jarvis_abo.entities.User;
import com.jarvis_abo.entities.UserDao;
import com.joanzapata.iconify.widget.IconButton;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends AppCompatActivity {

    private IconButton bt_ok;
    private IconButton bt_register;
    @Required(order = 1)
    private EditText et_name;
    @Required(order = 2)
    private EditText et_password;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 /*
        UserDao table in one line (as well opens session for DAO bases)
         */
        userDao = JA_application.getInstance().getDaoSession().getUserDao();
        SharedPreferences sharedPreferences = getSharedPreferences("com.jarvis_abo_preferences", MODE_PRIVATE);
        if (sharedPreferences.contains("last_successful_login") && userDao.queryBuilder().where(UserDao.Properties.Username.eq(sharedPreferences.getString("last_successful_login", "BAD"))).list().size() > 0) {
            Toast.makeText(this, "Hello Master " + sharedPreferences.getString("last_successful_login", ""), Toast.LENGTH_LONG).show();
            
            Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        final Validator validator = new Validator(this);


        bt_ok = (IconButton) findViewById(R.id.bt_loginOk);
        bt_register = (IconButton) findViewById(R.id.bt_loginRegister);

        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /*
                 *Validates edit texte ok described below (everitying is writen there)
                 */

                validator.setValidationListener(new Validator.ValidationListener() {
                    @Override
                    public void onValidationSucceeded() {
                        /*
                        Assures the uniqueness of username
                         */
                        if (userDao.queryBuilder().where(UserDao.Properties.Username.eq(et_name.getText().toString())).list().size() > 0) {

                            Toast.makeText(MainActivity.this, "The username already EXIST", Toast.LENGTH_LONG).show();

                            return;

                        }
                       /*
                       takes user created by greenDAO and fills in
                        */
                        User user = new User();

                        user.setPassword(et_password.getText().toString());
                        user.setUsername(et_name.getText().toString());


                        /*
                        opens/gets session and save the configured user
                         */
                        JA_application.getInstance().getDaoSession().getUserDao().insert(user);

                        Toast.makeText(MainActivity.this, "Process succeed, Please LOGIN", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onValidationFailed(View failedView, Rule<?> failedRule) {
                        if (failedView.getId() == et_name.getId()) {

                            Toast.makeText(MainActivity.this, "Invalid USERNAME", Toast.LENGTH_LONG).show();

                        } else if (failedView.getId() == et_password.getId()) {

                            Toast.makeText(MainActivity.this, "Invalid PASSWORD", Toast.LENGTH_LONG).show();
                        }

                    }

                });
                validator.validate();

            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 *Validates edit texte ok described below (everitying is writen there)
                 */

                validator.setValidationListener(new Validator.ValidationListener() {
                    @Override
                    public void onValidationSucceeded() {

                        QueryBuilder queryBuilder = userDao.queryBuilder();
                        if (
                                queryBuilder.where(
                                        queryBuilder.and(
                                                UserDao.Properties.Username.eq(et_name.getText().toString()),
                                                UserDao.Properties.Password.eq(et_password.getText().toString())
                                        )
                                ).list().size() > 0) {

                            Toast.makeText(MainActivity.this, "Successful LOGIN", Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("com.jarvis_abo_preferences", MODE_PRIVATE);
                            sharedPreferences.edit().putString("last_successful_login", et_name.getText().toString()).apply();

                            Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(MainActivity.this, "Something went WRONG", Toast.LENGTH_LONG).show();

                        }


                    }

                    @Override
                    public void onValidationFailed(View failedView, Rule<?> failedRule) {
                        if (failedView.getId() == et_name.getId()) {

                            Toast.makeText(MainActivity.this, "Invalid USERNAME", Toast.LENGTH_LONG).show();

                        } else if (failedView.getId() == et_password.getId()) {

                            Toast.makeText(MainActivity.this, "Invalid PASSWORD", Toast.LENGTH_LONG).show();
                        }

                    }

                });
                validator.validate();

            }
        });

    }


}
