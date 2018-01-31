package core;

import static core.Direction.*;

public class Monsters extends Movable{

        public int[]posOfMonster;//怪物位置

        public final static int MAX_HEALTH_POINT=25;//怪物血条上限
        public final static int attack=20;//怪物攻防
        public final static int defend=5;

        public int healthPoint=MAX_HEALTH_POINT;//怪物当前血量，初始为上限

        public void moveRandomly(int[] posOfMonster,Icon[][]map){
            while(true){
                switch((int)(Math.random()*4+1)){
                    case 1:
                        if(canGoOrNot(map,posOfMonster,RIGHT))
                            move(posOfMonster, RIGHT);
                        else
                            continue;
                        break;
                    case 2:
                        if(canGoOrNot(map,posOfMonster,LEFT))
                            move(posOfMonster,LEFT);
                        else
                            continue;
                        break;
                    case 3:
                        if(canGoOrNot(map,posOfMonster,UP))
                            move(posOfMonster,UP);
                        else
                            continue;
                        break;
                    case 4:
                        if(canGoOrNot(map,posOfMonster,DOWN))
                            move(posOfMonster,DOWN);
                        else
                            continue;
                        break;
                }
                break;
            }
        }//怪物随机产生移动方向

        public StringBuilder traceToMove=new StringBuilder("0");//追踪玩家时怪物的移动路径
        private int[] tempPos=new int[2];//设置临时位置，在形成怪物路径时只改变临时位置

        public void setTempPos(){
            tempPos[0]=posOfMonster[0];
            tempPos[1]=posOfMonster[1];
        }



        public void tracePlayer(Icon[][]map,int[] posOfPlayer){     //产生追踪玩家的路线

            if(tempPos[0]==posOfPlayer[0]&&tempPos[1]==posOfPlayer[1])      //怪物到达玩家位置时退出
                return;

            char lastStep=traceToMove.charAt(traceToMove.length()-1);   //确定怪物上一步移动方向
            //怪物不返回上一个位置，在其他三个方向上寻找路径，如果可以前进则更新位置，进行下一步寻找
            if(lastStep!='w'){
                if(canGoOrNot(map,tempPos,DOWN)) {
                    move(tempPos, DOWN);
                    traceToMove.append('s');
                    tracePlayer(map,posOfPlayer);
                    if(tempPos[0]==posOfPlayer[0]&&tempPos[1]==posOfPlayer[1])
                        return;
                }
            }
            if(lastStep!='s'){
                if(canGoOrNot(map,tempPos,UP)) {
                    move(tempPos, UP);
                    traceToMove.append('w');
                    tracePlayer(map,posOfPlayer);
                    if(tempPos[0]==posOfPlayer[0]&&tempPos[1]==posOfPlayer[1])
                        return;
                }
            }
            if(lastStep!='a'){
                if(canGoOrNot(map,tempPos,RIGHT)) {
                    move(tempPos, RIGHT);
                    traceToMove.append('d');
                    tracePlayer(map,posOfPlayer);
                    if(tempPos[0]==posOfPlayer[0]&&tempPos[1]==posOfPlayer[1])
                        return;
                }
            }
            if(lastStep!='d'){
                if(canGoOrNot(map,tempPos,LEFT)) {
                    move(tempPos, LEFT);
                    traceToMove.append('a');
                    tracePlayer(map,posOfPlayer);
                    if(tempPos[0]==posOfPlayer[0]&&tempPos[1]==posOfPlayer[1])
                        return;
                }
            }
            //如果三个方向都是墙壁，则怪物返回上一位置，清除这一步的方向，返回寻找其他方向
            switch (lastStep) {
                case 'w':
                    move(tempPos, DOWN);
                    break;
                case 's':
                    move(tempPos, UP);
                    break;
                case 'a':
                    move(tempPos, RIGHT);
                    break;
                case 'd':
                    move(tempPos, LEFT);
                    break;
            }
            traceToMove.deleteCharAt(traceToMove.length()-1);
        }

        public void goTrace(Monsters monster){      //怪物根据确定好的路线移动

            if(traceToMove.length()<=1)             //怪物已到达玩家位置
                return;

            char nextMove=traceToMove.charAt(1);
            switch (nextMove){
                case 'w':
                    move(monster.posOfMonster, UP);
                    break;
                case 's':
                    move(monster.posOfMonster, DOWN);
                    break;
                case 'a':
                    move(monster.posOfMonster, LEFT);
                    break;
                case 'd':
                    move(monster.posOfMonster, RIGHT);
                    break;
            }
            traceToMove.deleteCharAt(1);
        }


}
