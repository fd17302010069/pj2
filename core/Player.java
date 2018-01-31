package core;

import javafx.scene.input.KeyCode;
import static core.Direction.*;
import static core.Icon.*;

public class Player extends Movable {
    public int[] posOfPlayer={1,1};//玩家位置
    public int[] posOfStep1={1,1} ;//足迹位置
    public int[] posOfStep2={1,1} ;//足迹位置
    public int numberOfSteps=0;//步数
    public int numberOfGoods=0;//捡起的宝物数
    public int numberOfKilledMonsters=0;//杀死的怪物数
    public int score=1000;//得分
    public StringBuilder trace=new StringBuilder();//记录玩家的历史操作

    public int healthPoint=100;//玩家血条
    public int attack=10;//玩家攻防
    public int defend=10;

    private void step(int[]posOfStep2,int[]posOfStep1,int []posOfPlayer){
        posOfStep2[0]=posOfStep1[0];
        posOfStep2[1]=posOfStep1[1];
        posOfStep1[0]=posOfPlayer[0];
        posOfStep1[1]=posOfPlayer[1];
    }//将玩家上一步的位置赋给足迹


    private void goRight(){
        step(posOfStep2,posOfStep1,posOfPlayer);
        move(posOfPlayer,RIGHT);
        numberOfSteps++;//步数加一
    }//向右前进

    private void goLeft(){
        step(posOfStep2,posOfStep1,posOfPlayer);
        move(posOfPlayer,LEFT);
        numberOfSteps++;
    }//向左前进

    private void goUp(){
        step(posOfStep2,posOfStep1,posOfPlayer);
        move(posOfPlayer,UP);
        numberOfSteps++;
    }//向上前进

    private void goDown(){
        step(posOfStep2,posOfStep1,posOfPlayer);
        move(posOfPlayer,DOWN);
        numberOfSteps++;
    }//向下前进

    private void goBack() {
        step(posOfStep2,posOfStep1,posOfPlayer);
        char lastMove = trace.charAt(0);//根据上一步的前进方向判断，向反方向前进
        switch (lastMove) {
            case ('D'):
                posOfPlayer[1]--;
                break;
            case ('A'):
                posOfPlayer[1]++;
                break;
            case ('W'):
                posOfPlayer[0]++;
                break;
            case ('S'):
                posOfPlayer[0]--;
                break;
        }
        trace.deleteCharAt(0);
        numberOfSteps--;
    }//回退

    private boolean canPickUpGood(Icon[][]map){
        return (map[posOfPlayer[0]][posOfPlayer[1]+1]==GOOD ||
                map[posOfPlayer[0]][posOfPlayer[1]-1]==GOOD ||
                map[posOfPlayer[0]-1][posOfPlayer[1]]==GOOD ||
                map[posOfPlayer[0]+1][posOfPlayer[1]]==GOOD);
    }//判断周围是否有宝物

    private void pickUp(Icon[][] map){
        if (map[posOfPlayer[0]][posOfPlayer[1]+1]==GOOD){
            map[posOfPlayer[0]][posOfPlayer[1]+1]=EMPTY;
            attack+=2;
            defend+=2;
            numberOfGoods++;
        }
        else if (map[posOfPlayer[0]][posOfPlayer[1]-1]==GOOD){
            map[posOfPlayer[0]][posOfPlayer[1]-1]=EMPTY;
            attack+=2;
            defend+=2;
            numberOfGoods++;
        }
        else if (map[posOfPlayer[0]+1][posOfPlayer[1]]==GOOD){
            map[posOfPlayer[0]+1][posOfPlayer[1]]=EMPTY;
            attack+=2;
            defend+=2;
            numberOfGoods++;
        }
        else if (map[posOfPlayer[0]-1][posOfPlayer[1]]==GOOD){
            map[posOfPlayer[0]-1][posOfPlayer[1]]=EMPTY;
            attack+=2;
            defend+=2;
            numberOfGoods++;
        }
    }//捡起宝物，每个宝物增加攻2防2

    private boolean canKillMonster(int[]posOfMonster) {
        return (null != posOfMonster)&&(
                (posOfPlayer[0] + 1 == posOfMonster[0] && posOfPlayer[1] == posOfMonster[1]) ||
                (posOfPlayer[0] - 1 == posOfMonster[0] && posOfPlayer[1] == posOfMonster[1]) ||
                (posOfPlayer[1] + 1 == posOfMonster[1] && posOfPlayer[0] == posOfMonster[0]) ||
                (posOfPlayer[1] - 1 == posOfMonster[1] && posOfPlayer[0] == posOfMonster[0]) ||
                (posOfPlayer[0] == posOfMonster[0] && posOfPlayer[1] == posOfMonster[1])
        );
    }//判断周围及玩家位置上是否有怪物

    private boolean killMonster(Icon[][]map){
        if (canKillMonster(MapOfMaze.monster1.posOfMonster)) {
            MapOfMaze.monster1.healthPoint-=attack-Monsters.defend;//计算怪物收到的伤害
            if(MapOfMaze.monster1.healthPoint<=0) {     //如果怪物血条减为零
                map[MapOfMaze.monster1.posOfMonster[0]][MapOfMaze.monster1.posOfMonster[1]]=GOOD;//怪物死亡后掉落宝物
                MapOfMaze.monster1.posOfMonster = null;//怪物位置坐标清空，怪物死亡
                numberOfKilledMonsters++;
                score += 10;
            }
            return true;
        }
        else if (canKillMonster(MapOfMaze.monster2.posOfMonster)) {
            MapOfMaze.monster2.healthPoint-=attack-Monsters.defend;
            if(MapOfMaze.monster2.healthPoint<=0) {
                map[MapOfMaze.monster2.posOfMonster[0]][MapOfMaze.monster2.posOfMonster[1]]=GOOD;
                MapOfMaze.monster2.posOfMonster = null;
                numberOfKilledMonsters++;
                score += 10;
            }
            return true;
        }
        else if (canKillMonster(MapOfMaze.monster3.posOfMonster)) {
            MapOfMaze.monster3.healthPoint-=attack-BigMonsters.defend;
            if(MapOfMaze.monster3.healthPoint<=0) {
                map[MapOfMaze.monster3.posOfMonster[0]][MapOfMaze.monster3.posOfMonster[1]]=GOOD;
                MapOfMaze.monster3.posOfMonster = null;
                numberOfKilledMonsters++;
                score += 10;
            }
            return true;
        }
        else
            return false;

    }//攻击怪物

    public boolean whereToGo(Icon[][]map, KeyCode e){//根据玩家输入的指令判断下一步操作
        switch (e){
            case D://向右
                if(canGoOrNot(map,posOfPlayer,RIGHT)){
                    trace.insert(0,e);//记录操作指令
                    goRight();
                    score--;
                    return true;
                }
                else
                    return false;

            case A://向左
                if(canGoOrNot(map,posOfPlayer,LEFT)){
                    trace.insert(0,e);
                    goLeft();
                    score--;
                    return true;
                }
                else
                    return false;

            case W://向上
                if(canGoOrNot(map,posOfPlayer,UP)){
                    trace.insert(0,e);
                    goUp();
                    score--;
                    return true;
                }
                else
                    return false;

            case S://向下
                if(canGoOrNot(map,posOfPlayer,DOWN)){
                    trace.insert(0,e);
                    goDown();
                    score--;
                    return true;
                }
                else
                    return false;
            case B://后退
                if(trace.length()!=0){
                    goBack();
                    score++;
                    return true;
                }
                else
                    return false;
            case P://捡起宝物
                if(canPickUpGood(map)){
                    pickUp(map);
                    score+=5;
                    return true;
                }
                else
                    return false;
            case K://攻击怪物
                if(killMonster(map)) {
                    score++;
                    return true;
                }
                else
                    return false;

            default://玩家输入其他指令
                return false;
        }
    }
}
