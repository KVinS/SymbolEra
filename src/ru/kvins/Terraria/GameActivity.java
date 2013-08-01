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
    public int action_type = 0;
//0 - Идти
//1 - Ломать
//2 - Строить
    public int width, vertical_rate;
    public int height, horizontal_rate;
    public String log = "";
    public boolean day;
    public Set<Unit> unitsSet = new HashSet<Unit>();
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
        createBlock(3, 12, 4);
        createBlock(3, 11, 4);
        createBlock(2, 11, 6);
        createBlock(1, 11, 6);
        createBlock(0, 11, 6);
        createBlock(6, 10, 5);


        hero = new Unit(0, world[5][1], world);
        unitsSet.add(hero);

        unitsSet.add(new Unit(1, world[7][1], world));
        unitsSet.add(new Unit(2, world[9][1], world));
        unitsSet.add(new Unit(4, world[11][1], world));
        unitsSet.add(new Unit(8, world[14][1], world));
        unitsSet.add(new Unit(4, world[101][1], world));
        unitsSet.add(new Unit(8, world[140][1], world));
        unitsSet.add(new Unit(2, world[190][1], world));
		
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

            Thread t = new Thread(new Runnable() {
            Message msg;

            public void run() {
            int game_hour = 0;
            day = true;

            long start_time,delta_time;
			while (true) {
					start_time = System.currentTimeMillis();
                    if (day) {
                        world[game_hour][0].image = "☁";
                        game_hour++;
                        if (game_hour == width) {
                            day = false;
                            game_hour--;
                            world[game_hour][0].image = "☽";
                        } else {
                            world[game_hour][0].image = "<font color = '#ffcc00'>☀</font>";
                        }
                    } else {
                        world[game_hour][0].image = "☁";
                        game_hour--;
                        if (game_hour == -1) {
                            day = true;
                            game_hour++;
                            world[game_hour][0].image = "<font color = '#ffcc00'>☀</font>";
                        } else {
                            world[game_hour][0].image = "☽";
                        }
                    }
                    int rnd;
                    Set<Unit> deletedUnitsSet = new HashSet<Unit>();

                    for (Unit unit : unitsSet) {
                        if (unit.hp <= 0) {
                            unit.position.type = 3;
                            unit.position.toStart();
                            unit.position.guest = null;
                            unit.position = null;
                            unit.enemy = null;
                            deletedUnitsSet.add(unit);
                        } else {
                            if (unit.AI) {
                                if (unit.fraction == 8) {
                                    rnd = (int) (Math.random() * 6);
                                    if (rnd == 1) {
                                        unit.speedX = -1;
                                    } else if (rnd == 0) {
                                        unit.speedX = 1;
                                    }
                                    if (!((world[unit.x][unit.y + 1].input & 8) == 8)) {
                                        unit.speedY = (int) (Math.random() * -2);
                                    }
                                } else if (unit.enemy == null) {
                                    int range = 40;
                                    for (Unit target : unitsSet) {
                                        if ((unit.enemys & target.type) == target.type) {
                                            if (target.hp > 0 && range >= Math.abs(unit.x - target.x)) {
                                                unit.enemy = target;
                                            }
                                        }
                                    }
                                    if (unit.enemy == null) {
                                        rnd = (int) (Math.random() * 6);
                                        if (rnd == 1) {
                                            unit.speedX = -1;
                                        } else if (rnd == 0) {
                                            unit.speedX = 1;
                                        }
                                        if (!((world[unit.x][unit.y + 1].input & 8) == 8)) {
                                            unit.speedY = (int) (Math.random() * -2);
                                        }
                                    }
                                } else {
                                    Unit target = unit.enemy;
                                    if (unit.range >= (Math.abs(unit.x - target.x) + Math.abs(unit.y - target.y))) {
                                        int dm = (target.def - unit.attack > 2) ? target.def - unit.attack : 2;
                                        target.enemy = unit;
                                        target.hp -= dm;

                                    } else if (unit.x < target.x) {
                                        unit.speedX = 1;
                                        if ((world[unit.x + 1][unit.y].input & 2) == 2 && !((world[unit.x][unit.y + 1].input & 8) == 8)) {
                                            unit.speedY -= 3;
                                        }
                                    } else if (unit.x > target.x) {
                                        unit.speedX = -1;
                                        if ((world[unit.x - 1][unit.y].input & 1) == 1 && !((world[unit.x][unit.y + 1].input & 8) == 8)) {
                                            unit.speedY -= 3;
                                        }
                                    }
                                    if (unit.enemy != null && unit.enemy.hp <= 0) {
                                        unit.enemy = null;
                                    }

                                }
                            }
                            unit.Update();
                        }
                    }

                    unitsSet.removeAll(deletedUnitsSet);
                    deletedUnitsSet.removeAll(deletedUnitsSet);
                    msg = h.obtainMessage(0, 0, 0, render());
                    h.sendMessage(msg);

                delta_time = 350 - (System.currentTimeMillis() - start_time);

                if (delta_time > 0) {
				try{
TimeUnit.MILLISECONDS.sleep(delta_time);
} catch (InterruptedException e) {
          e.printStackTrace();
        }
				tLog(hero.hp + "❤ " + hero.x + ":" + hero.y+"| "+delta_time+"ml");
             } else {
				tLog("ahtung!");
                }
                }
            }
        });
        t.start();
    }

    public String render() {
        String str = new String();
        int startX, finishX, startY, finishY;

        startX = (hero.x - horizontal_rate < 0) ? 0 : hero.x - horizontal_rate;
        finishX = (hero.x + horizontal_rate > width) ? width : hero.x + horizontal_rate;
        startY = (hero.y - vertical_rate < 0) ? 0 : hero.y - vertical_rate;
        finishY = (hero.y + vertical_rate > height) ? height : hero.y + vertical_rate;

        for (int i = startY; i < finishY; i++) {
            for (int j = startX; j < finishX; j++) {
                str = str.concat(world[j][i].image);
            }
            str = str.concat("<br>");
        }
        return str;
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
                        action_button.setText("☩");
                        break;
                    case 1:
                        action_button.setText("⚔");
                        break;
                    case 2:
                        action_button.setText("⚒");
                        break;
                }
                break;

            case R.id.button3:
                action(hero.x, hero.y + 1);
                break;
        }
    }

    public void action(int x, int y) {
        if (x > 0 && y > 0 && x < width && y < height) {
            switch (action_type) {
                case 0:
                    if (x < hero.x) {
                        hero.speedX = -1;
                    } else if (x > hero.x) {
                        hero.speedX = 1;
                    } else if (y < hero.y) {
                        if (!((world[hero.x][hero.y + 1].input & 8) == 8)) {
                            hero.speedY = -3;
                        }
                    } else if (y > hero.y) {
                        hero.speedY = 3;
                    }
                    break;
                case 1:
                    if (world[x][y].guest == null) {
                        world[x][y].dm(hero.attack);
                    } else {
                        Unit target = world[x][y].guest;
                        int dm = (target.def - hero.attack > 2) ? target.def - hero.attack : 2;
                        target.enemy = hero;
                        target.hp -= dm;
                    }
                    break;
                case 2:
                    world[x][y].type = 1;
                    world[x][y].toStart();
                    break;
            }
        }
    }

    public void createBlock(int x, int y, int t) {
        world[x][y] = new Cell(x, y, t);
    }

    public void tLog(String s) {
        log = s;
    }
}
