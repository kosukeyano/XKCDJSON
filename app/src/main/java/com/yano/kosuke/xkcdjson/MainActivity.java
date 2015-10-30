package com.yano.kosuke.xkcdjson;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText comicIdEditText;
    Button   processButton;
    TextView transcriptTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set UI defined in XML
        comicIdEditText    = (EditText) findViewById(R.id.comicId_edittext);
        processButton      = (Button)   findViewById(R.id.process_button);
        transcriptTextView = (TextView) findViewById(R.id.transcript_textview);

        // setup listener for button
        processButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // guard condition for no search
        if (isEmptySearchEditText()) {
            showShortToast("ERROR: Please insert search term");
        }

        if (isOnline()) {
            setTranscript(comicIdEditText.getText().toString());
        } else {
            showShortToast("ERROR: No network connectivity");
        }

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comicIdEditText.getWindowToken(), 0);
    }

    protected void setTranscript(String comicId) {
        String URL = "http://xkcd.com/"
                + comicId
                + "/info.0.json";

        // send request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String transcript     = jsonObject.getString("transcript");

                            transcriptTextView.setText(transcript);
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        queue.add(request);
    }

    protected boolean isEmptySearchEditText() {
        String search = comicIdEditText.getText().toString();

        return search.length() == 0;
    }

    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    protected void showShortToast(String message) {
        Context context = getApplication();
        int duration    = Toast.LENGTH_SHORT;
        Toast toast     = Toast.makeText(context, message, duration);
        toast.show();
    }
}
