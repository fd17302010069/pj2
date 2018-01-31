package core;

import static core.Icon.*;

abstract class Movable {     //可移动的物体（玩家，怪物）继承此抽象类

    boolean canGoOrNot(Icon[][]map,int[]position,Direction direction) {//判断前进方向上是否有墙
        switch (direction) {
            case RIGHT:
                return map[position[0]][position[1] + 1] != WALL;
            case LEFT:
                return map[position[0]][position[1] - 1] != WALL;
            case UP:
                return map[position[0] - 1][position[1]] != WALL;
            case DOWN:
                return map[position[0] + 1][position[1]] != WALL;
            default:
                return false;
        }
    }

    void move(int []position,Direction direction){  //移动
        switch (direction){
            case RIGHT:
                position[1]++;
                break;
            case LEFT:
                position[1]--;
                break;
            case UP:
                position[0]--;
                break;
            case DOWN:
                position[0]++;
                break;
            default:
                break;
        }
    }
}
