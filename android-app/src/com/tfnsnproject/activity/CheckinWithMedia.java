package com.tfnsnproject.activity;

import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tfnsnproject.R;
import com.tfnsnproject.service.CheckinService;
import com.tfnsnproject.to.MediaCheckin;

import java.io.IOException;
import java.net.Authenticator;

import static com.tfnsnproject.authenticator.Authenticator.ACCOUNT_TYPE;
import static com.tfnsnproject.authenticator.Authenticator.AUTHTOKEN_TYPE;

public class CheckinWithMedia extends Activity {

    Uri imageUri;
    EditText messageText;

    String authToken;

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

        AccountManager accountManager = AccountManager.get(this);
        accountManager.getAuthTokenByFeatures(ACCOUNT_TYPE, AUTHTOKEN_TYPE, null, this, null, null, new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
                Bundle result = null;
                try {
                    result = bundleAccountManagerFuture.getResult();
                    authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                } catch (Exception e) {
                    finish();
                }
                System.out.println("result = " + result);
            }
        }, null);

    }

    public void checkin(View view) {
        if (authToken == null) {
            return;
        }
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Button button = (Button) findViewById(R.id.button_send);
            button.setEnabled(false);

            MediaCheckin checkin = new MediaCheckin();
            checkin.setAuthToken(authToken);
            checkin.setMedia(imageUri);
            checkin.setMessage(((EditText) findViewById(R.id.edit_message)).getText().toString());

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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