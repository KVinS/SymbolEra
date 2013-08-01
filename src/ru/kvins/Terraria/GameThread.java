package ru.kvins.Terraria;

import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class GameThread extends Thread{
	
	public static final int INIT_NPC_LOGIC = 1;
	public static final int PLAYER_PRESSED_BUTTON = 2;
	
	
	private Looper mLooper;
	private Handler mGUIThreadHandler;
	private Set<Unit> mUnitsSet;
	private Unit mHero;
	private Set<Spawner> mSpawnersSet;
	private Cell[][] mGameWorld;
	
	private static int width, vertical_rate;
	private static int height, horizontal_rate;
	
	public GameThread(Set<Unit> unitsSet, Unit hero, Handler guiThreadHandler, Set<Spawner> spawnersSet, Cell[][] gameWorld) {
		this.mUnitsSet = unitsSet;
		this.mHero = hero;
		this.mGUIThreadHandler = guiThreadHandler;
		this.mSpawnersSet = spawnersSet;
		this.mGameWorld = gameWorld;
		width = GameActivity.width;
		vertical_rate = GameActivity.vertical_rate;
		height = GameActivity.height;
		horizontal_rate = GameActivity.horizontal_rate;
	}
	
	@Override
	public void run() {
		Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Looper.loop();
	}
	
	public Looper getLooper() {
		if (!isAlive()) {
			return null;
		}
		
		synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return mLooper;
	}
	
	public class GameThreadHandler extends Handler {
		
		public GameThreadHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case INIT_NPC_LOGIC:
					initNPCLogic();
					break;
				case PLAYER_PRESSED_BUTTON:
					break;
			}
		}
		
	}
	
	private void initNPCLogic() {
		
		for (Spawner spawner : mSpawnersSet) {
			spawner.last_spaun_time++;
			if(spawner.last_spaun_time>spawner.cooldown && mGameWorld[spawner.x][spawner.y].guest==null){
				spawner.last_spaun_time = 0;
				mUnitsSet.add(new Unit(spawner.mob_id, mGameWorld[spawner.x][spawner.y], mGameWorld));
			}
		}
		
        Set<Unit> deletedUnitsSet = new HashSet<Unit>();
        int rnd;
        for (Unit unit : mUnitsSet) {
            if (unit.hp <= 0) {
                //unit.position.type = 3;
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
                        if (!((mGameWorld[unit.x][unit.y + 1].input & 8) == 8)) {
                            unit.speedY = (int) (Math.random() * -2);
                        }
                    } else if (unit.enemy == null) {
                        int range = 40;
                        for (Unit target : mUnitsSet) {
                            if ((unit.enemys & target.fraction) == target.fraction) {
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
                            if (!((mGameWorld[unit.x][unit.y + 1].input & 8) == 8)) {
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
                            if ((mGameWorld[unit.x + 1][unit.y].input & 2) == 2 && !((mGameWorld[unit.x][unit.y + 1].input & 8) == 8)) {
                                unit.speedY -= 3;
                            }
                        } else if (unit.x > target.x) {
                            unit.speedX = -1;
                            if ((mGameWorld[unit.x - 1][unit.y].input & 1) == 1 && !((mGameWorld[unit.x][unit.y + 1].input & 8) == 8)) {
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
        mUnitsSet.removeAll(deletedUnitsSet);
        deletedUnitsSet.removeAll(deletedUnitsSet);
        mGUIThreadHandler.sendMessage(mGUIThreadHandler.obtainMessage(0, 0, 0, render()));
	}
	
	public String render() {
        String str = new String();
        int startX, finishX, startY, finishY;

        startX = (mHero.x - horizontal_rate < 0) ? 0 : mHero.x - horizontal_rate;
        finishX = (mHero.x + horizontal_rate > width) ? width : mHero.x + horizontal_rate;
        startY = (mHero.y - vertical_rate < 0) ? 0 : mHero.y - vertical_rate;
        finishY = (mHero.y + vertical_rate > height) ? height : mHero.y + vertical_rate;

        for (int i = startY; i < finishY; i++) {
            for (int j = startX; j < finishX; j++) {
                str = str.concat(mGameWorld[j][i].image);
            }
            str = str.concat("<br>");
        }
        return str;
    }
	
}
