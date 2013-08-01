package ru.kvins.Terraria;

public class Unit {
public String image;
public Cell position;
public int speedX, speedY;
private Cell[][] world;
public boolean AI;
public int x, y, fraction, hp, maxHp, enemys,def, attack, range;
public Unit enemy;
public Unit(int _t, Cell _p,Cell[][] w ){
position = _p;
x=_p.x;
y=_p.y;
world = w;
speedX = 0;
speedY = 0;
fraction = _t;
AI = true;
range = 2;

switch(fraction){
case 0:
fraction=1;
AI = false;
image = "<font color = '#ffcc00'>☺</font>";
maxHp = 100;
enemys = 7;
def = 5;
attack = 8;
break;

//Люди
case 1:
image = "<font color = '#4f7942'>♀</font>";
maxHp = 100;
enemys = 6;
def = 5;
attack = 8;
break;

//Гоблины
case 2:
image = "<font color = '#806b2a'>☻</font>";
maxHp = 50;
enemys = 13;
def = 6;
attack = 10;
break;

//Хищники
case 4:
image = "<font color = '#de3163'>☣</font>";
maxHp = 20;
enemys = 9;
def = 2;
attack = 6;
break;


//Травоядные
case 8:
image = "<font color = '#bbbbbb'>♞</font>";
maxHp = 10;
enemys = 0;
def = 1;
attack = 1;
break;
}

hp=maxHp;
}



public void Update (){
if (speedX>0){
if(x+1<world.length&&(world[x+1][y].admission&fraction)==fraction&&((world[x+1][y].input&1)==1||(world[x+1][y].input_s&1)==1)){
changePosition(x+1, y);
speedX--;
} else if(y-1>=0&&x+1<world.length&&(world[x+1][y-1].admission&fraction)==fraction&&((world[x+1][y-1].input&1)==1) && ((world[x][y-1].input&8)==8)){
changePosition(x+1, y-1);
speedX--;
}else{
speedX=0;
}
}else if (speedX<0){
if(x-1>=0&&(world[x-1][y].admission&fraction)==fraction&&((world[x-1][y].input&2)==2 ||(world[x-1][y].input_s&2)==2)){
changePosition(x-1, y);
speedX++;
}else if (x-1>=0&&y-1>=0&&(world[x-1][y-1].admission&fraction)==fraction&&((world[x-1][y-1].input&2)==2) && ((world[x][y-1].input&8)==8)){
changePosition(x-1, y-1);
speedX++;
}else{
speedX=0;
}
}

if(speedY >=0 && y+1 < world[0].length){
if ((world[x][y+1].input&4)==4 || ( speedY>0 && (world[x][y+1].input_s&4)==4)){
changePosition(x, y+1);
}
}

if (speedY <0 && ((world[x][y-1].input&8)==8||(world[x][y-1].input_s&8)==8)){
speedY ++;
changePosition(x, y-1);
}else{
speedY=0;
}

if(hp<maxHp){
hp++;
}
}

public void changePosition(int _x, int _y){
position.toStart();
position.guest = null;
position = world[_x][_y];

x=position.x;
y=position.y;
position.image = image;
position.guest = this;
position.input = 0;
position.input_s = 0;
}
}