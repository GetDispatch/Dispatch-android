package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity {
    private List<Contact> contacts = new ArrayList<Contact>();;
    private CrashService crashService;
    private CrashHandler crashHandler;
    private Intent crashServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handleContacts();
        setupUI();

        contacts.add(new Contact("7033623714"));
        crashHandler = new CrashHandler("If you got this text, my HackTJ app glitched. Sorry!", contacts);

        ActivityManager.setActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleContacts() {
        SharedPreferences cprefs = getSharedPreferences("contactPrefs.xml", MODE_PRIVATE);
        final Set<String> numbers = cprefs.getStringSet("contacts", new HashSet<String>());

        if(numbers.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.import_contacts);

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences preferences = getSharedPreferences("contactPrefs.xml", MODE_PRIVATE);

                    List<Contact> safe = ContactImporter.importContacts(MainActivity.this);

                    for(Contact contact : contacts) {
                        numbers.add(contact.getNumber());
                    }

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet("contacts", numbers);
                    editor.apply();
                }

            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "Text Messages will not be sent without imported contacts", Toast.LENGTH_LONG);
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

    private void setupUI() {
        Button save = (Button) findViewById(R.id.save_button);
        final EditText messageField = (EditText) findViewById(R.id.messageEditText);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("messagePrefs.xml", MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();

                e.putString("messagetosend", messageField.getText().toString());

                Toast.makeText(MainActivity.this, "Messaged saved.", Toast.LENGTH_SHORT).show();
            }
        });

        final Button toggleRun = (Button) findViewById(R.id.startButton);

        toggleRun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = toggleRun.getText().toString();
                Context context = getApplicationContext();

                if (text.equals("Start")) {
                    toggleRun.setText("Stop");

                    crashServiceIntent = new Intent(MainActivity.this, CrashService.class);
                    crashServiceIntent.putExtra("listener", crashHandler);

                    context.startService(crashServiceIntent);
                } else if (text.equals("Stop")) {
                    toggleRun.setText("Start");

                    context.stopService(crashServiceIntent);
                }
            }

        });
    }
}
