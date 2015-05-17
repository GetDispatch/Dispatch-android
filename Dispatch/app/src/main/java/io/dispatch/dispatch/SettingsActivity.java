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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class SettingsActivity extends Activity {
    private List<Contact> contacts;
    private CrashService crashService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Properties properties = new Properties();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.import_contacts);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                contacts = ContactImporter.importContacts(SettingsActivity.this);
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

                if(text.equals("Start")) {
                    toggleRun.setText("Stop");

                    Context context = getApplicationContext();

                    List<Contact> cl = new ArrayList<Contact>();
                    cl.add(new Contact("Daniel Christoper", "7033623714"));

                    CrashListener listener = new CrashHandler("A crash has happened! Save me!", cl);

                    crashService = new CrashService();

                    Intent intent = new Intent(SettingsActivity.this, CrashService.class);
                    intent.putExtra("listener", listener);

                    context.startService(intent);
                }
                else if(text.equals("Stop")) {
                    toggleRun.setText("Start");

                    if(crashService != null) {
                        crashService.stopSelf();
                    }
                }
            }

        });
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
