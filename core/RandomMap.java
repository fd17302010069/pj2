package core;

import static core.Icon.*;

public class RandomMap extends MapOfMaze{

    private final static int HEIGHT=17;             //地图高度
    private final static int WIDTH=23;              //地图宽度
    private final static int NUMBER_OF_GOOD=10;     //宝物数量
    private final static int NUMBER_OF_MONSTER=3;   //怪物数量

    private static boolean[][] randomMap = new boolean[HEIGHT][WIDTH];
    private static Icon[][] printRandomMap = new Icon[HEIGHT][WIDTH];


    private static void createBorder(boolean[][] randomMap) {
        for (int i = 0; i < WIDTH; i++) {
            randomMap[0][i] = true;
            randomMap[HEIGHT-1][i] = true;
        }
        for (int j = 0; j < HEIGHT; j++) {
            randomMap[j][0] = true;
            randomMap[j][WIDTH-1] = true;
        }
    }//添加迷宫地图边框

    private static void createWall(int xStart, int yStart, int height, int width) {
        if (height < 3 || width < 3)
            return;
        int xWall, yWall;
        do {
            xWall = xStart + (int) (Math.random() * height) / 2 * 2 + 1;
        } while (xWall == xStart + height);
        for (int j = yStart; j < yStart + width; j++) {
            randomMap[xWall][j] = true;
        }
        do {
            yWall = yStart + (int) (Math.random() * width) / 2 * 2 + 1;
        } while (yWall == yStart + width);
        for (int i = xStart; i < xStart + height; i++) {
            randomMap[i][yWall] = true;
        }
        //在横竖方向随机设置两道墙壁，然后在划分出的四块区域上执行同样的操作直到无法添加墙壁

        openDoors(xStart, yStart, xWall, yWall, height, width);
        createWall(xStart, yStart, xWall - xStart, yWall - yStart);//左上角
        createWall(xStart, yWall + 1, xWall - xStart, width - 1 - yWall + yStart);//右上角
        createWall(xWall + 1, yStart, height - 1 - xWall + xStart, yWall - yStart);//左下角
        createWall(xWall + 1, yWall + 1, height - 1 - xWall + xStart, width - 1 - yWall + yStart);//右下角
    }


    private static void openDoors(int xStart, int yStart, int xWall, int yWall, int height, int width) {
        int xDoor, yDoor;
        switch ((int) (Math.random() * 4) + 1) {
            case 1:
                yDoor = yStart + (int) (Math.random() * (yWall - yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                xDoor = xWall + 1 + (int) (Math.random() * (height - xWall + xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                yDoor = yWall + 1 + (int) (Math.random() * (width - yWall + yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                break;
            case 2:
                xDoor = xWall + 1 + (int) (Math.random() * (height - xWall + xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                yDoor = yWall + 1 + (int) (Math.random() * (width - yWall + yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                xDoor = xStart + (int) (Math.random() * (xWall - xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                break;
            case 3:
                yDoor = yWall + 1 + (int) (Math.random() * (width - yWall + yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                xDoor = xStart + (int) (Math.random() * (xWall - xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                yDoor = yStart + (int) (Math.random() * (yWall - yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                break;
            case 4:
                xDoor = xStart + (int) (Math.random() * (xWall - xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                yDoor = yStart + (int) (Math.random() * (yWall - yStart)) / 2 * 2;
                randomMap[xWall][yDoor] = false;
                xDoor = xWall + 1 + (int) (Math.random() * (height - xWall + xStart)) / 2 * 2;
                randomMap[xDoor][yWall] = false;
                break;
        }
    }//在添加的墙壁上随机产生三个缺口

    private static void formRandomMap() {
        createBorder(randomMap);
        createWall(1, 1, HEIGHT-2, WIDTH-2);


        for (int i = 0; i < randomMap.length; i++) {
            for (int j = 0; j < randomMap[0].length; j++) {
                printRandomMap[i][j] = (randomMap[i][j]) ? WALL : EMPTY;
            }
        }

        int numberOfGoods = 1;
        while (numberOfGoods <= NUMBER_OF_GOOD) {
            int i = (int) (Math.random() * HEIGHT-2) + 1, j = (int) (Math.random() * WIDTH-2) + 1;
            if (i == 1 && j == 1)       //宝物与玩家位置不能相同
                continue;
            if (i == HEIGHT-2 && j == WIDTH-2)      //宝物与终点位置不相同
                continue;
            if (printRandomMap[i][j] == EMPTY) {
                printRandomMap[i][j] = GOOD;
                numberOfGoods++;
            }
        }//在随机地图中随机产生10个宝物

        int numberOfMonsters=1;
        int[][] posOfRandomMonsters=new int[NUMBER_OF_MONSTER][2];
        while (numberOfMonsters<=NUMBER_OF_MONSTER){
            int i = (int) (Math.random() * HEIGHT-2) + 1, j = (int) (Math.random() * WIDTH-2) + 1;
            if ((i == 1 && j == 1)||(i == 1 && j == 2)||(i == 2 && j == 1))
                continue;
            if (i == HEIGHT-2 && j == WIDTH-2)
                continue;

            if (printRandomMap[i][j] == EMPTY) {
                posOfRandomMonsters[numberOfMonsters-1][0]=i;
                posOfRandomMonsters[numberOfMonsters-1][1]=j;
                printRandomMap[i][j]=MONSTER;//在地图上标记已有怪物的坐标，防止两个怪物坐标相同
                numberOfMonsters++;
            }
        }//在随机地图中随机产生三个怪物

        for(int n = 0 ; n < NUMBER_OF_MONSTER ; n++){
            printRandomMap[posOfRandomMonsters[n][0]][posOfRandomMonsters[n][1]]=EMPTY;//清除之前的标记
        }

        //将随机产生的位置坐标赋给怪物
        monster1.posOfMonster=posOfRandomMonsters[0];
        monster2.posOfMonster=posOfRandomMonsters[1];
        monster3.posOfMonster=posOfRandomMonsters[2];

        printRandomMap[HEIGHT-2][WIDTH-2] = END;//设置终点

    }

    public static Icon[][] getPrintRandomMap() {
        formRandomMap();
        return printRandomMap;
    }//返回随机地图

    public static void resetRandomMap(){
        randomMap = new boolean[HEIGHT][WIDTH];
        printRandomMap = new Icon[HEIGHT][WIDTH];
    }//刷新随机地图信息

}
