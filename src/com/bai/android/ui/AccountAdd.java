/**
 * Copyright (c) 2014, Bait Al-Hikma LTD. All rights reserved.
 * 
 * This file is part of Waseda Connect.
 *
 * Waseda Connect is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Waseda Connect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Waseda Connect. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bai.android.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.proxy.ProxyInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bai.android.data.Application;
import com.bai.android.data.NetworkException;
import com.bai.android.data.account.AccountManager;
import com.bai.android.data.account.AccountProtocol;
import com.bai.android.data.account.AccountType;
import com.bai.android.data.connection.ConnectionManager;
import com.bai.android.data.extension.vcard.VCardManager;
import com.bai.android.data.intent.AccountIntentBuilder;
import com.bai.android.ui.dialog.OrbotInstallerDialogBuilder;
import com.bai.android.ui.helper.ManagedActivity;
import com.bai.androiddev.R;
import com.bai.xmpp.vcard.VCard;

public class AccountAdd extends ManagedActivity implements
View.OnClickListener {
  private static final int ORBOT_DIALOG_ID = 9050;

  private CheckBox storePasswordView;
  private boolean noConnection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (isFinishing()) {
      return;
    }

    setContentView(R.layout.account_add);

    storePasswordView = (CheckBox) findViewById(R.id.store_password);
    //		useOrbotView = (CheckBox) findViewById(R.id.use_orbot);
    //		syncableView = (CheckBox) findViewById(R.id.syncable);
    //		if (!Application.getInstance().isContactsSupported()) {
    //			syncableView.setVisibility(View.GONE);
    //			syncableView.setChecked(false);
    //		}
    //
    //		accountTypeView = (Spinner) findViewById(R.id.account_type);
    //		accountTypeView.setAdapter(new AccountTypeAdapter(this));
    //		accountTypeView.setOnItemSelectedListener(this);

    //		String accountType;
    //		if (savedInstanceState == null)
    //			accountType = null;
    //		else
    //			accountType = savedInstanceState.getString(SAVED_ACCOUNT_TYPE);
    //		accountTypeView.setSelection(0);
    //		for (int position = 0; position < accountTypeView.getCount(); position++)
    //			if (((AccountType) accountTypeView.getItemAtPosition(position))
    //					.getName().equals(accountType)) {
    //				accountTypeView.setSelection(position);
    //				break;
    //			}

    ((Button) findViewById(R.id.ok)).setOnClickListener(this);
    //((Button) findViewById(R.id.register_account_button)).setOnClickListener(this);
    ((Button) findViewById(R.id.register_account_button)).setOnClickListener(registerClickListener);
    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(findViewById(R.id.ok)
        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
  }

  View.OnClickListener registerClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      setRegisterNewAccount();
    }
  };


  public void setRegisterNewAccount() {
    setContentView(R.layout.register_new_account);
    //		tabBar = new TabBar(this, 0);
    ((Button) findViewById(R.id.confirm_registration)).setOnClickListener(this);
    ((Button) findViewById(R.id.cancel_registration)).setOnClickListener(cancelClickListener);
  }

  View.OnClickListener cancelClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      finish();
    }
  };


  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    //		outState.putString(SAVED_ACCOUNT_TYPE,
    //			((AccountType) accountTypeView.getSelectedItem()).getName());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //		if (requestCode == OAUTH_WML_REQUEST_CODE) {
    //			if (resultCode == RESULT_OK && !OAuthActivity.isInvalidated(data)) {
    ////				String token = OAuthActivity.getToken(data);
    //				//				if (token == null) {
    //				//					Application.getInstance().onError(
    //				//							R.string.AUTHENTICATION_FAILED);
    //				//				} else {
    //				//					String account;
    //				//					try {
    //				//						account = AccountManager.getInstance()
    //				//								.addAccount(
    //				//										null,
    //				//										token,
    //				//										(AccountType) accountTypeView
    //				//												.getSelectedItem(),
    //				//										syncableView.isChecked(),
    //				//										storePasswordView.isChecked(),
    //				//										useOrbotView.isChecked());
    //				//					} catch (NetworkException e) {
    //				//						Application.getInstance().onError(e);
    //				//						return;
    //				//					}
    //				//					setResult(RESULT_OK,
    //				//							createAuthenticatorResult(this, account));
    //				//					finish();
    //				//				}
    //			}
    //		}
  }


  @Override
  public void onClick(View view) {

    List<String> server = Arrays.asList(getString(R.string.host_name));
    AccountType accountType = new AccountType(
        R.array.account_type_xmpp, 
        AccountProtocol.xmpp,
        "XMPP",
        null,
        null,
        true,
        getString(R.string.server_address),
        5222,
        false,
        server);
    EditText userView = (EditText) findViewById(R.id.account_user_name);
    EditText passwordView = (EditText) findViewById(R.id.account_password);
    String account;

    switch (view.getId()) {
    case R.id.ok:

      XMPPConnection checkPWConnection = createPreAuthenticaitonConnection();
      try {
        checkPWConnection.login(userView.getText().toString() + "@" + getString(R.string.host_name), passwordView.getText().toString());
      } catch (Exception e) {
        Log.e("WasedaConnect", "Erro: " + e.getMessage());
        if (noConnection == false) {
          Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_LONG).show();
        }
        return;
      }
      checkPWConnection.disconnect();
      try {
        account = AccountManager.getInstance().addAccount(
            userView.getText().toString() + "@" + getString(R.string.host_name),
            passwordView.getText().toString(),
            accountType,
            false,
            storePasswordView.isChecked(),
            false);
      } catch (NetworkException e) {
        Application.getInstance().onError(e);
        return;
      }
      setResult(RESULT_OK, createAuthenticatorResult(this, account));
      finish();
      break;
    case R.id.confirm_registration:

      XMPPConnection preAuthXmppConnection = createPreAuthenticaitonConnection();

      userView = (EditText) findViewById(R.id.account_user_name_edittext);

      EditText fullNameEditText = (EditText) findViewById(R.id.full_name_edittext);
      EditText emailEditText = (EditText) findViewById(R.id.email_edittext);
      Spinner countrySpinner = (Spinner) findViewById(R.id.country_spinner);
      Spinner statusSpinner = (Spinner) findViewById(R.id.status_spinner);
      Spinner yearSpinner = (Spinner) findViewById(R.id.years_spinner);
      Spinner facultySpinner = (Spinner) findViewById(R.id.faculty_spinner);

      passwordView = (EditText) findViewById(R.id.account_password_edittext);
      EditText confirmPasswordView = (EditText) findViewById(R.id.confirm_password_edittext);

      //Validation
      //Username
      // TODO Analogical to OtherActivity profile updating (some fields and checks can be reused - full name, email?, graduation, status, faculty)
      String username = userView.getText().toString().toLowerCase(Locale.US);
      String usernamePattern = "^[0-9a-z@_\\.-]+$";
      if (username == null) {
        Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_LONG).show();
        return;
      } else if (username.length() > 16) {
        Toast.makeText(getApplicationContext(), "Username too long. (Max 16 characters long).", Toast.LENGTH_LONG).show();
        return;
      } else if (username.length() < 4) {
        Toast.makeText(getApplicationContext(), "Username too short. (Min 4 characters long).", Toast.LENGTH_LONG).show();
        return;
      } else if (username.matches(usernamePattern) == false) {
        Toast.makeText(getApplicationContext(), "Invalid username. Allowed: \n letters a - z \n dumbers 0 - 9 \n . (dot) \n - (dash)", Toast.LENGTH_LONG).show();
        return;
      }

      String fullName = fullNameEditText.getText().toString();
      if (fullName == null || fullName.trim().length() == 0) {
        Toast.makeText(getApplicationContext(), "Please enter a display name.", Toast.LENGTH_LONG).show();
        return;
      } else if (fullName.length() > 64) {
        Toast.makeText(getApplicationContext(), "Name too long. (Max 64 characters)", Toast.LENGTH_LONG).show();
        return;
      }

      //Email
      String email = emailEditText.getText().toString();
      if (email == null) {
        Toast.makeText(getApplicationContext(), "Empty email address", Toast.LENGTH_LONG).show();
        return;
      } else if (email.indexOf("@") == -1 || email.indexOf(".") == -1 || // contains zero or 2 or more "@", contains no "." , there is no "." after "@" 
          email.lastIndexOf(".") < email.indexOf("@") ||  email.indexOf("@") !=  email.lastIndexOf("@")) {
        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_LONG).show();
        return;
      }

      if (countrySpinner.getSelectedItemPosition() == 0) {
        Toast.makeText(getApplicationContext(), "Please select nationality.", Toast.LENGTH_LONG).show();
        return;
      }

      if (statusSpinner.getSelectedItemPosition() == 0) {
        Toast.makeText(getApplicationContext(), "Please select state.", Toast.LENGTH_LONG).show();
        return;
      }

      if (confirmPasswordView.getText().toString().length() < 6) {
        Toast.makeText(getApplicationContext(), "Password too short. (Min 6 characters long).", Toast.LENGTH_LONG).show();
        return;
      }

      //Check if the password matches with the confirmation password
      if (confirmPasswordView.getText().toString().equals(passwordView.getText().toString())) {

        username += "@" + getString(R.string.host_name);

        Map<String, String> arguements = new HashMap<String, String>();
        arguements.put("email", email);

        final org.jivesoftware.smack.AccountManager am = new org.jivesoftware.smack.AccountManager(preAuthXmppConnection);

        try {
          String creationResult = am.createAccount(
              username, 
              passwordView.getText().toString(),
              arguements);
          if (creationResult != null) {
            //error
            Toast.makeText(getApplicationContext(), creationResult, Toast.LENGTH_LONG).show();
            return;
          }
        } catch (XMPPException e) {
          e.printStackTrace();
          return;
        }

        preAuthXmppConnection.disconnect();

        try {
          account = AccountManager.getInstance().addAccount(
              username,
              passwordView.getText().toString(),
              accountType,
              false,
              storePasswordView.isChecked(),
              false);
        } catch (NetworkException e) {
          Application.getInstance().onError(e);
          return;
        }

        VCard vcard = new VCard();
        vcard.setFormattedName(fullName);
        if (email != null) vcard.setEmail(email);
        vcard.setNationality(countrySpinner.getItemAtPosition(countrySpinner.getSelectedItemPosition()).toString());
        vcard.setStatus(statusSpinner.getSelectedItem().toString());
        if (yearSpinner.getSelectedItemPosition() > 0) {
          vcard.setGraduation(yearSpinner.getSelectedItem().toString());
        }
        if (facultySpinner.getSelectedItemPosition() > 0) {
          vcard.setFaculty(facultySpinner.getSelectedItem().toString());
        }

        VCardManager.getInstance().set(
            account,
            username,
            vcard);

        setContentView(R.layout.welcome);
        setResult(RESULT_OK, createAuthenticatorResult(this, account));
        finish();
      } else {
        Toast.makeText(getApplicationContext(), "The passwords don't match!", Toast.LENGTH_LONG).show(); // TODO : put this in a string file
      }

      break;
    default:
      break;
    }
  }

  private XMPPConnection createPreAuthenticaitonConnection() {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO MOVE TO ASYNC
    StrictMode.setThreadPolicy(policy);

    ProxyInfo mProxyInfo = ProxyInfo.forNoProxy();
    ConnectionConfiguration config = new ConnectionConfiguration(
        getString(R.string.server_address),
        5222,
        getString(R.string.host_name),
        mProxyInfo);

    if (Application.SDK_INT >= 14) {
      config.setTruststoreType("AndroidCAStore");
      config.setTruststorePassword(null);
      config.setTruststorePath(null);
    } else {
      config.setTruststoreType("BKS");
      config.setTruststorePath(ConnectionManager.TRUST_STORE_PATH);
    }

    // Disable smack`s reconnection.
    config.setReconnectionAllowed(false);
    // We will send custom presence.
    config.setSendPresence(false);
    // We use own roaster management.
    config.setRosterLoadedAtLogin(false);
    config.setExpiredCertificatesCheckEnabled(false);
    config.setNotMatchingDomainCheckEnabled(false);
    config.setSelfSignedCertificateEnabled(true);
    config.setVerifyChainEnabled(false);
    config.setVerifyRootCAEnabled(false);
    
    config.setSASLAuthenticationEnabled(true);
    //				config.setSecurityMode(tlsMode.getSecurityMode());
    config.setCompressionEnabled(true);

    XMPPConnection xmppConnection = new XMPPConnection(config);
    try {
      xmppConnection.connect();
    } catch (XMPPException e1) {
      e1.printStackTrace();
      Toast.makeText(getApplicationContext(), "No connection to the server!", Toast.LENGTH_LONG).show();
      noConnection = true;
    }
    return xmppConnection;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == ORBOT_DIALOG_ID) {
      return new OrbotInstallerDialogBuilder(this, ORBOT_DIALOG_ID).create();
    }
    return super.onCreateDialog(id);
  }

  public static Intent createIntent(Context context) {
    return new Intent(context, AccountAdd.class);
  }

  private static Intent createAuthenticatorResult(Context context,
      String account) {
    return new AccountIntentBuilder(null, null).setAccount(account).build();
  }

  public static String getAuthenticatorResultAccount(Intent intent) {
    return AccountIntentBuilder.getAccount(intent);
  }

}
