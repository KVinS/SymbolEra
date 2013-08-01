package ru.kvins.Terraria;

public class Spawner {
	
	public int x, y, cooldown, mob_id, last_spaun_time;

	public Spawner(int _x, int _y, int _t){
		cooldown = 120;
		last_spaun_time=0;
		x = _x;
		y = _y;
		mob_id = _t;
	}
}