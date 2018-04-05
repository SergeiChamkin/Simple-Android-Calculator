package sergeichamkin.ru.calculusos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static class Item {
        String MathText;
        String MathResult = "0";

        Item(String MathText, String MathResult) {
            this.MathText = MathText;
            this.MathResult = MathResult;
        }
    }

    private Button copyButton, editButton, deleteItemButton, deleteAllButton;

    private Button oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton;

    private Button deleteButton, bracketButton, percentButton, backButton, multButton, minusButton, plusButton, runButton, dotButton, divideButton;

    private LinearLayout l;
    private TextView mathText;
    private ListView listView;
    private LinearLayout lay;
    private Vibrator vibe;
    private ItemsAdapter adapter;
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private View mView;
    private int vibor;
    private DataBase dbHelper;
    SharedPreferences mSettings;
    private WebView  webView;
    private String res=""; //DELETE ME
    private class ItemsAdapter extends ArrayAdapter<Item> {
        ItemsAdapter() {
            super(MainActivity.this, R.layout.item);
        }
        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            @SuppressLint("ViewHolder") final View view = getLayoutInflater().inflate(R.layout.item, null);
            final TextView mathL = (TextView) view.findViewById(R.id.mathText);
            final TextView mathRL = (TextView) view.findViewById(R.id.mathRez);
            final Item item = getItem(position);
            mathL.setText(item.MathText);
            mathRL.setText(item.MathResult);
            return view;
        }
    }

    private void saveChanges(String prd,String prc){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("list",prd+","+prc);
        db.insert("Numers", null, cv);
    }

    private  void saveChangesWithDeletingAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Numers");
        db.execSQL("create table Numers ("
                + "id integer primary key autoincrement,"
                + "list text"
                +");");
    }

    private void saveChangesAfterDelete(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Numers");
        db.execSQL("create table Numers ("
                + "id integer primary key autoincrement,"
                + "list text"
                +");");
        for(int i=0;i<adapter.getCount()-1;i++){
            ContentValues cv = new ContentValues();
            cv.put("list",adapter.getItem(i).MathText+","+adapter.getItem(i).MathResult);
            db.insert("Numers", null, cv);
        }
    }

    private void readTable(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c =db.query("Numers", null, null, null, null, null, null);
        if(c.moveToFirst()){
            int ls_ind = c.getColumnIndex("list");
            do{
                String ls = c.getString(ls_ind);
                String[] part = ls.split(",");
                adapter.add(new Item(part[0],part[1]));
            } while (c.moveToNext());
        }
    }

    private void saveString(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("mather", adapter.getItem(adapter.getCount()-1).MathText);
        editor.apply();
    }

    private void afterInput(){
        saveString();
        adapter.notifyDataSetChanged();
        vibe.vibrate(65);
    }
    private void numericInit(){
        oneButton = (Button) findViewById(R.id.btn_one);
        twoButton = (Button) findViewById(R.id.btn_two);
        threeButton = (Button) findViewById(R.id.btn_three);
        fourButton = (Button) findViewById(R.id.btn_four);
        fiveButton = (Button) findViewById(R.id.btn_five);
        sixButton = (Button) findViewById(R.id.btn_six);
        sevenButton = (Button) findViewById(R.id.btn_seven);
        eightButton = (Button) findViewById(R.id.btn_eight);
        nineButton = (Button) findViewById(R.id.btn_nine);
        zeroButton = (Button) findViewById(R.id.btn_zero);

        Button buttons[] = {zeroButton,oneButton,twoButton,threeButton,fourButton,fiveButton,sixButton,sevenButton,eightButton,nineButton};
        for(int i=0;i<10;i++){
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getItem(adapter.getCount()-1).MathText+=String.valueOf(finalI);
                    afterInput();
                }
            });
        }
    }

    private void funcsInit(){
        deleteButton= (Button) findViewById(R.id.btn_delete);
        bracketButton = (Button) findViewById(R.id.btn_bracket);
        percentButton = (Button) findViewById(R.id.btn_percentage);
        backButton = (Button) findViewById(R.id.btn_back);
        multButton = (Button) findViewById(R.id.btn_multiply);
        minusButton = (Button) findViewById(R.id.btn_minus);
        plusButton = (Button) findViewById(R.id.btn_plus);
        runButton = (Button) findViewById(R.id.btn_equal);
        divideButton = (Button) findViewById(R.id.btn_divide);
        dotButton = (Button) findViewById(R.id.btn_dot);
        mathText = (TextView) findViewById(R.id.mathText);

        multButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+="X";
                afterInput();
            }
        });

        divideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+="/";
                afterInput();
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+="+";
                afterInput();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+="-";
                afterInput();
            }
        });

        dotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+=".";
                afterInput();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mathTexting =adapter.getItem(adapter.getCount()-1).MathText;
                if(mathTexting.length() > 0){
                    adapter.getItem(adapter.getCount()-1).MathText=mathTexting.substring(0, mathTexting.length()-1);
                    afterInput();
                }else{
                    adapter.getItem(adapter.getCount()-1).MathText="";
                    afterInput();
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText="";
                afterInput();

            }
        });

        percentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(adapter.getCount()-1).MathText+="%";
                afterInput();
            }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adapter.getItem(adapter.getCount() - 1).MathText.equals("")) {
                    String mathTextEq = adapter.getItem(adapter.getCount() - 1).MathText;
                    mathTextEq = mathTextEq.replaceAll("%", "/100");
                    mathTextEq = mathTextEq.replaceAll("X", "*");
                    String temp="";
                    //Я НЕ ЗНАЮ ПОЧЕМУ РЭПЛЭЙС НЕ РАБОТАЕТ
                    for(int i=0;i<mathTextEq.length();i++){
                        if(mathTextEq.charAt(i)=='^'){
                            temp+="**";
                        }else{
                            temp+=mathTextEq.charAt(i);
                        }
                    }
                    mathTextEq=temp;
                    webView.evaluateJavascript(
                            "eval("+mathTextEq+")",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    res=html;
                                    if(!res.equals("null")) {
                                        String subs = "";
                                        String preda = "";
                                        boolean started = false;
                                        boolean pred = true;
                                        for (int i = 0; i < res.length(); i++) {
                                            if (res.charAt(i) == '.') {
                                                started = true;
                                                pred = false;
                                                continue;
                                            }
                                            if (started) subs += res.charAt(i);
                                            if (pred) preda += res.charAt(i);
                                        }
                                        if (subs.equals("0")) res = preda;
                                        adapter.getItem(adapter.getCount() - 1).MathResult = "=" + res;
                                        saveChanges(adapter.getItem(adapter.getCount() - 1).MathText, adapter.getItem(adapter.getCount() - 1).MathResult);
                                        adapter.add(new Item("", ""));
                                        adapter.notifyDataSetChanged();
                                        saveString();
                                        listView.smoothScrollToPosition(adapter.getCount() - 1);

                                    }else {
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "Неверное выражение!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        vibe.vibrate(1000);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                    listView.smoothScrollToPosition(adapter.getCount() - 1);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите выражение!", Toast.LENGTH_SHORT);
                    toast.show();
                    vibe.vibrate(1000);
                }
            }
        });

        bracketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, bracketButton);
                popup.getMenuInflater()
                        .inflate(R.menu.pop_up, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        adapter.getItem(adapter.getCount()-1).MathText=(adapter.getItem(adapter.getCount()-1).MathText+item.getTitle().charAt(1));
                        afterInput();
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ItemsAdapter();
        mSettings = getSharedPreferences("MATH", Context.MODE_PRIVATE);
        dbHelper = new DataBase(this);
        mBuilder = new AlertDialog.Builder(MainActivity.this);
        mView = getLayoutInflater().inflate(R.layout.viborka,null);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        readTable();
        copyButton = (Button) mView.findViewById(R.id.copyBtn);
        editButton = (Button) mView.findViewById(R.id.editBtn);
        deleteItemButton = (Button) mView.findViewById(R.id.deleteItemBtn);
        deleteAllButton = (Button) mView.findViewById(R.id.deleteAllBtn);
        webView = (WebView) findViewById(R.id.weba);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(65);
                setClipboard(getApplicationContext(),adapter.getItem(vibor).MathResult.replace("=",""));
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Скопировано!", Toast.LENGTH_SHORT);
                toast.show();
                dialog.hide();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(65);
                adapter.getItem(adapter.getCount()-1).MathText=adapter.getItem(vibor).MathText;
                adapter.notifyDataSetChanged();
                dialog.hide();
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(65);
                if(!adapter.getItem(vibor).MathResult.equals("")){
                    adapter.remove(adapter.getItem(vibor));
                adapter.notifyDataSetChanged();
                }
                else{
                    adapter.remove(adapter.getItem(vibor));
                    adapter.add(new Item("",""));
                }
                dialog.hide();
                saveChangesAfterDelete();
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(65);
                if (adapter.getCount() != 1) {
                    while (!adapter.getItem(0).MathResult.equals("")){
                        adapter.remove(adapter.getItem(0));
                    }
                    adapter.notifyDataSetChanged();
                }
                dialog.hide();
                saveChangesWithDeletingAll();
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        final int heights = display.getHeight();
        numericInit();
        funcsInit();
        l = (LinearLayout) findViewById(R.id.mainButtonsList);
        final int[] height = new int[1];

        vibe = (Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);

        listView = (ListView) findViewById(R.id.listView);

        l.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(height[0] == (bottom - top))
                    return;

                height[0] = (bottom - top);
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height =heights-height[0];
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vibor=position;
                dialog.show();
            }
        });
        if(mSettings.contains("mather")) {
            adapter.add(new Item(mSettings.getString("mather", ""),""));
        } else adapter.add(new Item("",""));
        listView.smoothScrollToPosition(adapter.getCount()-1);
    }

}
