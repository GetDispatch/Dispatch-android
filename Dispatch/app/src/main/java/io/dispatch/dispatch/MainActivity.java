package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity implements IContactListener {
    private List<Contact> contacts = new ArrayList<Contact>();;
    private CrashService crashService;
    private CrashHandler crashHandler;
    private Intent crashServiceIntent;
    private ProgressDialog progressDialog;
    private ProgressDialog notifyContactsProgressDialog;
    private SharedPreferences preferences;
    private ProgressDialog dispatchProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("contactPrefs.xml", MODE_PRIVATE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handleContacts();
        setupUI();

        contacts.add(new Contact("7033623714"));

        crashHandler = new CrashHandler("", contacts);

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

        final Set<String> numbers = preferences.getStringSet("contacts", new HashSet<String>());

        //if(numbers.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.import_contacts);

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Importing Your Contacts...");

                    new Thread(new ContactImporter()).start();
                }

            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "Text Messages will not be sent without imported contacts", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        //}
    }

    private void setupUI() {
        Button save = (Button) findViewById(R.id.save_button);
        final EditText messageField = (EditText) findViewById(R.id.messageEditText);

        String saved = preferences.getString("messagetosend", "");

        if(!saved.equals("")) {
            messageField.setText(saved);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = preferences.edit();

                e.putString("messagetosend", messageField.getText().toString());

                e.apply();

                Toast.makeText(MainActivity.this, "Messaged saved.", Toast.LENGTH_SHORT).show();
            }
        });

        final Button toggleRun = (Button) findViewById(R.id.startButton);

        toggleRun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = toggleRun.getText().toString();
                Context context = getApplicationContext();
                if (text.equals("Start Dispatch")) {
                    toggleRun.setText("Stop Dispatch");

                    ((Button) findViewById(R.id.startButton)).setEnabled(true);

                    crashHandler.setMessage(preferences.getString("messagetosend", ""));

                    crashServiceIntent = new Intent(MainActivity.this, CrashService.class);
                    crashServiceIntent.putExtra("listener", crashHandler);

                    context.startService(crashServiceIntent);
                } else if (text.equals("Stop Dispatch")) {
                    toggleRun.setText("Start Dispatch");

                    context.stopService(crashServiceIntent);
                }
            }

        });
    }

    @Override
    public void onContactsImported(List<Contact> contactList) {
        progressDialog.dismiss();

        final Set<String> numbers = preferences.getStringSet("contacts", new HashSet<String>());

        for(Contact contact : contacts) {
            numbers.add(contact.getNumber());
            Log.d(contact.getNumber(), contact.getNumber());
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("contacts", numbers);
        editor.apply();
    }

    public void onCrashOccured() {
        dispatchProgressDialog.dismiss();
        findViewById(R.id.startButton).setEnabled(false);
    }

    public void onCrashIgnored() {
        dispatchProgressDialog.dismiss();
        findViewById(R.id.startButton).setEnabled(true);
    }

    public void showNotifyContactsDialog() {
        notifyContactsProgressDialog = ProgressDialog.show(MainActivity.this, "Emergency", "Notifying all of your contacts immediately");
    }

    public void hideNotifyContactsDialog() {
        notifyContactsProgressDialog.dismiss();
    }
}
