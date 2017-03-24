package com.example.aaron.proyecto3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.proyecto3.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject clienteJS, jsonm;
    public static String usuario;
    private WebSocketClient mWebSocketClient;

    private static final int MY_PERMISSIONS_REQUEST_INTERNET=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectWebSocket();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                sendMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.user) {
            AlertDialog.Builder alertt= new AlertDialog.Builder(this);
            final EditText usern=new EditText(this);
            usern.setSingleLine();
            usern.setPadding(50,0,50,0);
            alertt.setTitle("USERNAME");alertt.setMessage("Introduzca su nombre de usuario");alertt.setView(usern);
            alertt.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                 usuario=usern.getText().toString();
                }
            });
            alertt.setNegativeButton("Cancelar",null);
            alertt.create();
            alertt.show();


        } else if (id == R.id.conexion) {
                if(usuario==null){
                AlertDialog.Builder alerta=new AlertDialog.Builder(this);
                alerta.setTitle("USERNAME necesario");alerta.setMessage("Necesitamos saber tu nombre de usuario para conectarte ");
                alerta.setPositiveButton("Aceptar",null);
                alerta.create();
                alerta.show();

            }else{
                connectWebSocket();

            }


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectWebSocket() {

        URI uri;
        try {
            uri = new URI("ws://chatpmdm-amartinezdasilva.c9users.io:8081");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> headers = new HashMap<>();

        mWebSocketClient = new WebSocketClient(uri, new Draft_17(), headers, 0) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");

                mWebSocketClient.send("{\"id\":\"" + usuario + "\"}");   clienteJS = new JSONObject();
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.textmensaje);

                        String nick;
                        String mens;
                        String destino;
                        int priv;
                       try {
                            jsonm = new JSONObject(message);
                            nick= jsonm.getString("id");
                            mens = jsonm.getString("mensaje");
                            destino= jsonm.getString("destino");
                            priv= jsonm.getInt("Privado");

                            if(priv==1){
                                if(destino.equals(usuario)){
                                    textView.setText(textView.getText() + "\n" + nick+ "\n" + mens);
                                }else textView.setText(textView.getText() + "\n" + nick+ "\n" + mens);

                            }
                        } catch (JSONException e) {
                           textView.setText(textView.getText() + "\n" + message);
                        }


                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
//
        mWebSocketClient.connect();

    }
    public void sendMessage() {
        EditText editText = (EditText)findViewById(R.id.message);
        EditText destino = (EditText)findViewById(R.id.destino);
        CheckBox checkBox = (CheckBox)findViewById(R.id.priv);




        String men = editText.getText().toString();
        int priv;
        if(checkBox.isChecked()) {
           priv=1 ;
        }else{
            priv = 0;
        }String destin = destino.getText().toString();
        clienteJS = new JSONObject();
        try {
            clienteJS.put("id",usuario);
            clienteJS.put("mensaje",men);
            clienteJS.put("Privado",priv);
            clienteJS.put("destino",destin);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebSocketClient.send(clienteJS.toString());

        editText.setText("");
        destino.setText("");

    }
}
