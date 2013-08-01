package ru.kvins.Terraria;

public class CallerThread extends Thread {

	GameThread.GameThreadHandler mHandler;
	
	public CallerThread(GameThread.GameThreadHandler handler) {
		mHandler = handler;
	}
	
	@Override
	public void run() {
		while (true) {
			mHandler.sendMessage(mHandler.obtainMessage(GameThread.INIT_NPC_LOGIC));
			try {
				sleep(350);
			} catch (InterruptedException e) {
				//todo: impl various utils
				System.out.println("Caller thread interrupted suddenly\n" + e.getStackTrace());
			}
		}
	}
	
}
