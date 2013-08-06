package ru.kvins.Terraria;

import android.os.Handler;
import ru.kvins.Terraria.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.text.Html;
import android.os.Message;
import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;
import android.widget.Toast;
import android.util.DisplayMetrics;
import java.util.*;
import android.widget.Button;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameActivity extends Activity {

    public Cell[][] world;
    public TextView gameView, tlog;
    public RelativeLayout gameBc;
    public Button action_button;
    public Handler h;
    public Unit hero;
    private GameThread.GameThreadHandler mGameThreadHandler;
    private GameThread mGameThread;
    public int action_type = 0;
//0 - Р�РґС‚Рё
//1 - Р›РѕРјР°С‚СЊ
//2 - РЎС‚СЂРѕРёС‚СЊ
    public static int width, vertical_rate;
    public static int height, horizontal_rate;
    public String log = "";
    public boolean day;
    public Set<Unit> unitsSet = new HashSet<Unit>();
    public Set<Spawner> spaunersSet = new HashSet<Spawner>();
	
	
    DisplayMetrics metrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        vertical_rate = 0;
        horizontal_rate = 0;

        width = 200;
        height = 40;

        gameView = (TextView) findViewById(R.id.GameTextView);
        tlog = (TextView) findViewById(R.id.tlog);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/DejaVuSansMono.ttf");
        gameView.setTypeface(tf);
        action_button = (Button) findViewById(R.id.button1);
        action_button.setTypeface(tf);
        action_button = (Button) findViewById(R.id.button2);
        action_button.setTypeface(tf);
        action_button = (Button) findViewById(R.id.button3);
        action_button.setTypeface(tf);
        action_button = (Button) findViewById(R.id.button4);
        action_button.setTypeface(tf);

        action_button = (Button) findViewById(R.id.button5);

        action_button.setTypeface(tf);

        Rect bounds = new Rect();
        Paint textPaint = gameView.getPaint();
        textPaint.getTextBounds("1", 0, 1, bounds);
        int symbol_width = (int) (bounds.width() * getResources().getDisplayMetrics().density);
        int symbol_height = (int) (bounds.height() * getResources().getDisplayMetrics().density);

        while (horizontal_rate * symbol_width < metrics.widthPixels / 2) {
            horizontal_rate++;
        }
        gameView.setText("");
        while (vertical_rate * symbol_height < metrics.heightPixels / 2) {
            vertical_rate++;
        }
        tLog(symbol_width + " " + symbol_height);

        world = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 25; j++) {
                if (j == height - 26 && Math.random() * 10 < 5) {
                    world[i][j] = new Cell(i, j, 2);
                } else {
                    world[i][j] = new Cell(i, j, 0);
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = height - 25; j < height; j++) {
                world[i][j] = new Cell(i, j, 1);
            }
        }

        createBlock(6, 14, 7);
        createBlock(6, 13, 6);
        createBlock(6, 12, 6);
        createBlock(6, 11, 6);
        createBlock(5, 11, 6);
        createBlock(4, 11, 6);
        createBlock(3, 14, 4);
        createBlock(3, 13, 4);
        createBlock(3, 12, 4);
        createBlock(3, 11, 4);
        createBlock(2, 11, 6);
        createBlock(1, 11, 6);
        createBlock(0, 11, 6);
        createBlock(6, 10, 5);

        createBlock(33, 14, 8);
        createBlock(40, 14, 8);
        createBlock(33, 13, 6);
        createBlock(40, 13, 6);
        createBlock(33, 12, 6);
        createBlock(40, 12, 6);
        createBlock(33, 11, 6);
        createBlock(40, 11, 6);
        createBlock(34, 10, 6);
        createBlock(35, 10, 6);
        createBlock(36, 10, 6);
        createBlock(37, 10, 6);
        createBlock(38, 10, 6);
        createBlock(39, 10, 6);
		
        hero = new Unit(0, world[5][1], world);
        unitsSet.add(hero);

        unitsSet.add(new Unit(1, world[7][1], world));
        unitsSet.add(new Unit(2, world[9][1], world));
        unitsSet.add(new Unit(4, world[11][1], world));
        unitsSet.add(new Unit(8, world[14][1], world));
		
		spaunersSet.add(new Spawner(37, 12, 2));
		spaunersSet.add(new Spawner(1, 12, 1));
		
        h = new Handler() {
            public void handleMessage(Message msg) {
                if (day) {
                    gameView.setBackgroundColor(getResources().getColor(R.color.white));
                    gameView.setTextColor(getResources().getColor(R.color.black));
                } else {
                    gameView.setBackgroundColor(getResources().getColor(R.color.black));
                    gameView.setTextColor(getResources().getColor(R.color.white));
                }
                tlog.setText(log);
                gameView.setText(Html.fromHtml((String) msg.obj));
            };
        };
        
		mGameThread = new GameThread(unitsSet, hero, h, spaunersSet, world); 
		mGameThread.start();
		mGameThreadHandler = mGameThread.new GameThreadHandler(mGameThread.getLooper());
		CallerThread callerThread = new CallerThread(mGameThreadHandler);
		callerThread.start();
    }

    

    public void butClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                action(hero.x - 1, hero.y);
                break;

            case R.id.button4:
                action(hero.x + 1, hero.y);
                break;

            case R.id.button2:
                action(hero.x, hero.y - 1);
                break;


            case R.id.button5:
                action_type++;
                if (action_type > 2) {
                    action_type = 0;
                }

                switch (action_type) {
                    case 0:
                        action_button.setText("в�©");
                        break;
                    case 1:
                        action_button.setText("вљ”");
                        break;
                    case 2:
                        action_button.setText("вљ’");
                        break;
                }
                break;

            case R.id.button3:
                action(hero.x, hero.y + 1);
                break;
        }
    }

    public void action(int x, int y) {
    	mGameThreadHandler.sendMessage(mGameThreadHandler.obtainMessage(GameThread.PLAYER_PRESSED_BUTTON, x, y, action_type));
    }

    public void createBlock(int x, int y, int t) {
        world[x][y] = new Cell(x, y, t);
    }

    public void tLog(String s) {
        log = s;
    }
}
