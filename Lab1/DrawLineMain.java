package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DrawLineMain extends AppCompatActivity {
    private DrawCanvas canvas;
    TextView textview_width;
    String string_stroke;
    Button button_small, button_large, button_eraser, button_clear, button_pencil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new DrawCanvas(this));
        setContentView(R.layout.activity_draw_line_main);
        canvas = (DrawCanvas) findViewById(R.id.canvasview);

        textview_width = (TextView) findViewById(R.id.strokesize);
        textview_width.setText(String.valueOf (canvas.strokeWidth));
        System.out.println(string_stroke);

        button_small = (Button) findViewById(R.id.button_small);
        button_large = (Button) findViewById(R.id.button_big);
        button_eraser = (Button) findViewById(R.id.eraser);
        button_clear = (Button) findViewById(R.id.clear);
        button_pencil = (Button) findViewById(R.id.pencil);

        button_small.setOnClickListener(new smallClick());
        button_large.setOnClickListener(new largeClick());
        button_eraser.setOnClickListener(new eraserClick());
        button_clear.setOnClickListener(new clearClick());
        button_pencil.setOnClickListener(new pencilClick());
    }
    class smallClick implements View.OnClickListener{
        public void onClick(View v){
            canvas.smallByOne();
            textview_width.setText(String.valueOf (canvas.strokeWidth));
            System.out.println(canvas.strokeWidth);
        }
    }

    class largeClick implements  View.OnClickListener{
        public void onClick(View v){
            canvas.largeByOne();
            textview_width.setText(String.valueOf (canvas.strokeWidth));
            System.out.println(canvas.strokeWidth);
        }
    }

    class eraserClick implements  View.OnClickListener{
        public void onClick(View v){
            canvas.eraser();
        }
    }

    class clearClick implements  View.OnClickListener{
        public void onClick(View v){
            canvas.clear();
        }
    }

    class pencilClick implements View.OnClickListener{
        public void onClick(View v){
            canvas.pencil();
        }
    }
}
