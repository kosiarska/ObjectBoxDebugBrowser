package com.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sample.utils.Utils;

import java.util.Date;
import java.util.Random;

import io.objectbox.Box;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Note note = new Note();
        note.setDate(new Date());
        note.setText("new notes");
        final Box<Note> noteBox = App.get().boxStore().boxFor(Note.class);
        noteBox.put(note);

        Person person = new Person(0, "Some Person " + (new Random().nextInt()), 50.4f, 180.4d );
        App.get().boxStore().boxFor(Person.class).put(person);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 5; i++) {
                    Note note = new Note();
                    note.setDate(new Date());
                    note.setText("fresh note number: " + i);
                    noteBox.put(note);
                }

                Toast.makeText(MainActivity.this, "Added some more notes, refresh browser to see results", Toast.LENGTH_LONG).show();
            }
        }, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    public void showDebugDbAddress(View view) {
        Utils.showDebugDBAddressLogToast(getApplicationContext());
    }
}
