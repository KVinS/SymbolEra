package ru.kvins.Terraria;

public class Cell {
public Unit guest;
public int type, input, output, input_s, def;
public String image;
public int x, y, admission;

public Cell(int _x, int _y, int _t){
type = _t;
x = _x;
y = _y;
guest = null;
admission = 15;
toStart();
}


public void toStart(){
switch (type){
case 0:
input = 15;
input_s = 15;
output = 15;
image="¡";
def = 0;
break;

case 1:
input = 0;
input_s = 0;
output = 0;
image="█";
def = 1;
break;

case 2:
input = 15;
input_s = 15;
output = 15;
image="<font color = '#00ff00'>າ</font>";
def = 1;
break;

case 3:
input = 0;
input_s = 0;
output = 0;
image="☗";
def = 20;
break;

case 4:
input = 0;
input_s = 12;
output = 15;
image="≣";
def = 2;
break;

case 5:
input = 15;
input_s = 0;
output = 15;
image="⚐";
def = 1;
break;

case 6:
input = 0;
input_s = 0;
output = 0;
image="<font color = '#99958c'>█</font>";
def = 100;
break;

case 7:
input = 3;
input_s = 0;
output = 0;
image="<font color = '#99958c'>╂</font>";
def = 100;
admission = 1;
break;

}
}

public void dm(int d){
def -= d;
if (def <=0){
type = 0;
toStart();
}
}
}