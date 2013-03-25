package com.tfnsnproject.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
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
import android.widget.TextView;
import android.widget.Toast;
import com.tfnsnproject.R;
import com.tfnsnproject.service.CheckinService;
import com.tfnsnproject.to.MediaCheckin;
import com.tfnsnproject.to.PlaceInfo;

import static com.tfnsnproject.authenticator.Authenticator.ACCOUNT_TYPE;
import static com.tfnsnproject.authenticator.Authenticator.AUTHTOKEN_TYPE;

public class CheckinWithMedia extends Activity {

    private static final int PICK_PLACE_REQUEST = 1;

    private Uri imageUri;
    private PlaceInfo placeInfo;

    private EditText messageText;
    private TextView placeName;

    private String authToken;

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

        setContentView(R.layout.checkin_with_media);
        this.placeName = (TextView) findViewById(R.id.place_name);
        this.messageText = (EditText) findViewById(R.id.edit_message);

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
            if (placeInfo != null) {
                checkin.setLat(placeInfo.getLat());
                checkin.setLong(placeInfo.getLong());
                checkin.setPlaceName(placeInfo.getName());
                checkin.setPlaceId(placeInfo.getId());
            } else {
                checkin.setLat(33d);
                checkin.setLong(44d);
            }

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

    public void addPlace(View view) {
        Intent intent = new Intent(this, SearchPlace.class);
        startActivityForResult(intent, PICK_PLACE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request went well (OK) and the request was PICK_CONTACT_REQUEST
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_PLACE_REQUEST) {
            placeInfo = data.getParcelableExtra(SearchPlace.PLACE);
            placeName.setText(placeInfo.getName());

        }
    }

}