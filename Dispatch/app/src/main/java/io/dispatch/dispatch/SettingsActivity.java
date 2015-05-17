package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class SettingsActivity extends Activity {
    private List<Contact> contacts = new ArrayList<Contact>();;
    private CrashService crashService;
    private CrashHandler crashHandler;
    private Intent crashServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Properties properties = new Properties();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.import_contacts);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ContactImporter.importContacts(SettingsActivity.this);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

        Button save = (Button) findViewById(R.id.save_button);
        final EditText messageField = (EditText) findViewById(R.id.messageEditText);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("messagePrefs.xml", MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();

                e.putString("messagetosend", messageField.getText().toString());

                Toast.makeText(SettingsActivity.this, "Messaged saved.", Toast.LENGTH_SHORT).show();
            }
        });

        final Button toggleRun = (Button) findViewById(R.id.startButton);

        toggleRun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = toggleRun.getText().toString();
                Context context = getApplicationContext();

                if(text.equals("Start")) {
                    toggleRun.setText("Stop");

                    crashServiceIntent = new Intent(SettingsActivity.this, CrashService.class);
                    crashServiceIntent.putExtra("listener", crashHandler);

                    context.startService(crashServiceIntent);
                }
                else if(text.equals("Stop")) {
                    toggleRun.setText("Start");

                    context.stopService(crashServiceIntent);
                }
            }

        });

        contacts.add(new Contact("Daniel Christopher", "7033623714"));
        crashHandler = new CrashHandler("If you got this text, my HackTJ app glitched. Sorry!", contacts);
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
}
