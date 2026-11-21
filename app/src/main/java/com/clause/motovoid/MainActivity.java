package com.clause.motovoid;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Pantalla de chat. Todo se construye con código (sin archivos de diseño)
 * y solo usa funciones que existen desde Android 2.3 (API 9).
 */
public class MainActivity extends Activity {

    private final Brain brain = new Brain();
    private final Handler handler = new Handler();

    private LinearLayout chat;
    private ScrollView scroll;
    private EditText input;
    private float density = 1f;
    private int maxBubbleWidth = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getResources().getDisplayMetrics().density;
        maxBubbleWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.8f);
        buildUi();
        addBubble(brain.welcome(), false);
    }

    private int dp(int v) {
        return (int) (v * density + 0.5f);
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(30, 42, 51));

        // Encabezado
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(Color.rgb(24, 33, 40));
        header.setPadding(dp(14), dp(10), dp(14), dp(10));

        TextView title = new TextView(this);
        title.setText("MotoVoid IA");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("pequeño y local · hecho para Android 2.3");
        subtitle.setTextColor(Color.rgb(150, 170, 182));
        subtitle.setTextSize(12);
        header.addView(subtitle);

        root.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Zona de mensajes
        scroll = new ScrollView(this);
        chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(dp(8), dp(8), dp(8), dp(8));
        scroll.addView(chat, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        // Barra para escribir
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(Color.rgb(24, 33, 40));
        bar.setPadding(dp(6), dp(6), dp(6), dp(6));

        input = new EditText(this);
        input.setHint("Escribe aquí...");
        input.setHintTextColor(Color.rgb(130, 150, 162));
        input.setTextColor(Color.WHITE);
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    send();
                    return true;
                }
                return false;
            }
        });
        bar.addView(input, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button sendButton = new Button(this);
        sendButton.setText("Enviar");
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send();
            }
        });
        bar.addView(sendButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        root.addView(bar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    private void send() {
        String text = input.getText().toString().trim();
        if (text.length() == 0) return;
        input.setText("");
        addBubble(text, true);
        final String answer = brain.reply(text);
        handler.postDelayed(new Runnable() {
            public void run() {
                addBubble(answer, false);
            }
        }, 350);
    }

    private void addBubble(String text, boolean fromUser) {
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setTextSize(16);
        bubble.setTextColor(Color.WHITE);
        bubble.setPadding(dp(12), dp(8), dp(12), dp(8));
        bubble.setMaxWidth(maxBubbleWidth);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(fromUser ? Color.rgb(54, 128, 150) : Color.rgb(48, 64, 76));
        bg.setCornerRadius(dp(14));
        bubble.setBackgroundDrawable(bg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = fromUser ? Gravity.RIGHT : Gravity.LEFT;
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        chat.addView(bubble, lp);

        scroll.post(new Runnable() {
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
