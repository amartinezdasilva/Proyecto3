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
            AlertDialog.Builder alert= new AlertDialog.Builder(this);
            final EditText usern=new EditText(this);
            usern.setSingleLine();
            usern.setPadding(50,0,50,0);
            alert.setTitle("USERNAME");alert.setMessage("Introduzca su nombre de usuario");alert.setView(usern);
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                 usuario=usern.getText().toString();
                }
            });
            alert.create();
            alert.show();


        } else if (id == R.id.conexion) {
                if(usuario==null){
                AlertDialog.Builder alertC=new AlertDialog.Builder(this);
                alertC.setTitle("USERNAME necesario");alertC.setMessage("Necesitamos saber tu nombre de usuario para conectarte ");
                alertC.setPositiveButton("Aceptar",null);
                alertC.create();
                alertC.show();

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
                        TextView textView = (TextView)findViewById(R.id.textView);

                        String nick;
                        String mens;
                        String destino;
                        int priv;
                       try {
                            clienteJS = new JSONObject(message);
                            nick= clienteJS.getString("id");
                            mens = clienteJS.getString("mensaje");
                            destino= clienteJS.getString("destino");
                            priv= clienteJS.getInt("Privado");

                            if(priv==1){
                                if(destino.equals(usuario)){
                                    textView.setText(textView.getText() + "\n" + nick+ "\n" + mens);
                                }

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

        mWebSocketClient.connect();

    }
    public void sendMessage() {
        EditText mens = (EditText)findViewById(R.id.message);
        EditText destino = (EditText)findViewById(R.id.destino);
        CheckBox checkBox = (CheckBox)findViewById(R.id.priv);

        int priv;

        String destin = destino.getText().toString();
        String men = mens.getText().toString();
        if(checkBox.isChecked()) {
           priv=1 ;
        }else{
            priv = 0;
        }
        jsonm = new JSONObject();
        try {
            clienteJS.put("id",usuario);
            clienteJS.put("msg",mens);
            clienteJS.put("esPrivado",priv);
            clienteJS.put("dst",destino);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebSocketClient.send(jsonm.toString());


    }
}
