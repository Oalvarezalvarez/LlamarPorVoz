package com.example.oalvarezalvarez.llamarporvoz;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Vector;


public class MainActivity extends ActionBarActivity
{
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    public Button bt_start;
    public Vector <String> nombres; //En este vector guarda los nombres de todos los contactos
    public Vector <String> telefonos;// En este vector guarda los telefonos de todos los contactos

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_start = (Button)findViewById(R.id.button1);
        bt_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startVoiceRecognitionActivity();
            }
        });

        getNombreYNumero();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startVoiceRecognitionActivity() //Metodo para analizar la voz
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Llamar a ...");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

    }

    @Override
    //Los resultados del reconocimiento de voz
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK)
        {
            ArrayList <String> arra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); //El intent envia el arraylist
            String [] palabras = arra.get(0).toString().split(" "); //Esto separa lo que escucha en palabras poniendo espacios

            if(palabras[0].equals("llamar"))
            {
                showToast("LLAMAR");
                for(int a=0; a<nombres.size(); a++)
                {
                    if(nombres.get(a).equals(palabras[2]))
                    {
                        //Si el nombre es igual a la tercera palabra, recoge el telefono de esa persona con el otro arraylist
                        showToast(String.valueOf(nombres.get(a)));
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + telefonos.get(a)));
                        startActivity(callIntent);

                        break;
                    }
                }
            }
        }
    }

    public void getNombreYNumero() //este metodo recoge los nombres de los contactos en un vector y los numeros de telefonos en otro
    {
        nombres = new Vector<String>();
        telefonos = new Vector<String>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ContentResolver buscador = getContentResolver();

        Cursor cursor = buscador.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor contactos = getContentResolver().query(uri, projection, null, null, null);

        int indexName = contactos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = contactos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        contactos.moveToFirst();

            do
            {
                String name = contactos.getString(indexName);
                nombres.add(name);
                String number = contactos.getString(indexNumber);
                telefonos.add(number);
                showToast(name + "\n" + number);
            }

            while (contactos.moveToNext());
    }

    public void showToast(String mensaje)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, mensaje, duration);
        toast.show();
    }


}
