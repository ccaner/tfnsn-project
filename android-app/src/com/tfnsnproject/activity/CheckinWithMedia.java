package com.tfnsnproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tfnsnproject.R;
import com.tfnsnproject.service.CheckinService;
import com.tfnsnproject.to.MediaCheckin;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class CheckinWithMedia extends Activity {

    Uri imageUri;
    EditText messageText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    this.imageUri = imageUri;
                }
            }
        }

        this.messageText = (EditText) findViewById(R.id.edit_message);
        setContentView(R.layout.checkin_with_media);
    }

    public void checkin(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Button button = (Button) findViewById(R.id.button_send);
            button.setEnabled(false);

            MediaCheckin checkin = new MediaCheckin();
            checkin.setMedia(imageUri);
            checkin.setMessage(((EditText) findViewById(R.id.edit_message)).getText().toString());

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.edit_message).getWindowToken(), 0);

            Toast.makeText(getApplicationContext(), getString(R.string.sending), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, CheckinService.class);
            intent.putExtra(CheckinService.CHECKIN, checkin);
            startService(intent);
            finish();
        } else {
            // display error
        }

    }


}