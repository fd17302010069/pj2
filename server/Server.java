package server;

import core.*;
import static core.Icon.*;
import static server.Mode.*;

import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.io.*;

public class Server {

    public Player player=new Player();

    private MapOfMaze mapOfMaze=new MapOfMaze();

    public int level=1;//玩家所在的关卡
    public Mode mode=STORY;//游戏模式

    private Icon[][] map = mapOfMaze.getMap1();                     //游戏初始地图
    private Icon[][] printMap=new Icon[map.length][map[0].length];  //打印地图，在初始地图的基础上添加会移动的元素

    public boolean resetMap(){//根据关卡重新设置地图
        switch (level){
            case 1:
                map=mapOfMaze.getMap1();
                break;
            case 2:
                map=mapOfMaze.getMap2();
                break;
            case 3:
                map=mapOfMaze.getMap3();
                break;
            case 4:
                map=mapOfMaze.getMap4();
                break;
            case 0:
                RandomMap.resetRandomMap();     //重新设置随机地图
                map= RandomMap.getPrintRandomMap();
                break;
            default:
                return false;
        }

        printMap=new Icon[map.length][map[0].length];       //重置打印地图

        player.posOfPlayer=new int[]{1,1};//进入下一关后重置玩家位置，足迹位置和路径
        player.posOfStep1=new int[]{1,1};
        player.posOfStep2=new int[]{1,1};
        player.trace.setLength(0);

        if(mode==SANDBOX){      //重置玩家信息
            player.numberOfSteps = 0;
            player.numberOfGoods = 0;
            player.numberOfKilledMonsters = 0;
            player.score = 1000;

            player.healthPoint=100;
            player.attack=10;
            player.defend=10;
        }

        MapOfMaze.monster1.healthPoint=Monsters.MAX_HEALTH_POINT;
        MapOfMaze.monster2.healthPoint=Monsters.MAX_HEALTH_POINT;
        MapOfMaze.monster3.healthPoint=BigMonsters.MAX_HEALTH_POINT;//重置怪物血量

        return true;
    }

    public Icon[][]getMap(){

        for (int i = 0; i < printMap.length; i++) {
            System.arraycopy(map[i], 0, printMap[i], 0, printMap[i].length);
        }

        printMap[player.posOfStep1[0]][player.posOfStep1[1]]=STEP;
        printMap[player.posOfStep2[0]][player.posOfStep2[1]]=STEP;//加入足迹
        printMap[player.posOfPlayer[0]][player.posOfPlayer[1]]=PLAYER;//加入玩家

        if(null!=MapOfMaze.monster1.posOfMonster) {
            printMap[MapOfMaze.monster1.posOfMonster[0]][MapOfMaze.monster1.posOfMonster[1]] = MONSTER;//加入怪物
        }
        if(null!=MapOfMaze.monster2.posOfMonster){
            printMap[MapOfMaze.monster2.posOfMonster[0]][MapOfMaze.monster2.posOfMonster[1]] = MONSTER;
        }
        if(null!=MapOfMaze.monster3.posOfMonster) {
            printMap[MapOfMaze.monster3.posOfMonster[0]][MapOfMaze.monster3.posOfMonster[1]] = BIGMONSTER;
        }

        return printMap;
}

    public void moveMonster(){//随机移动怪物
        if(null!=MapOfMaze.monster1.posOfMonster) {
            MapOfMaze.monster1.moveRandomly(MapOfMaze.monster1.posOfMonster, map);
        }
        if(null!=MapOfMaze.monster2.posOfMonster) {
            MapOfMaze.monster2.moveRandomly(MapOfMaze.monster2.posOfMonster, map);
        }
        if(null!=MapOfMaze.monster3.posOfMonster) {
            MapOfMaze.monster3.moveRandomly(MapOfMaze.monster3.posOfMonster, map);
        }
    }

    public void monsterTrace(){//怪物追踪玩家
        if(null!=MapOfMaze.monster1.posOfMonster) {
            MapOfMaze.monster1.goTrace(MapOfMaze.monster1);
        }
        if(null!=MapOfMaze.monster2.posOfMonster) {
            MapOfMaze.monster2.goTrace(MapOfMaze.monster2);
        }
        if(null!=MapOfMaze.monster3.posOfMonster) {
            MapOfMaze.monster3.goTrace(MapOfMaze.monster3);
        }
    }

    public void setMonsterTrace(){  //设置怪物追踪路径
        if(null!=MapOfMaze.monster1.posOfMonster) {
            MapOfMaze.monster1.traceToMove.setLength(1);//清空原有路径，重新形成新路径
            MapOfMaze.monster1.setTempPos();
            MapOfMaze.monster1.tracePlayer(map, player.posOfPlayer);
        }
        if(null!=MapOfMaze.monster2.posOfMonster) {
            MapOfMaze.monster2.traceToMove.setLength(1);
            MapOfMaze.monster2.setTempPos();
            MapOfMaze.monster2.tracePlayer(map,player.posOfPlayer);
        }
        if(null!=MapOfMaze.monster3.posOfMonster) {
            MapOfMaze.monster3.traceToMove.setLength(1);
            MapOfMaze.monster3.setTempPos();
            MapOfMaze.monster3.tracePlayer(map, player.posOfPlayer);
        }
    }

    public boolean go(KeyCode d){       //玩家进行操作
        return player.whereToGo(map,d);
    }

    public boolean meetTheEnd(){//判断是否到达终点
        return player.posOfPlayer[0]==map.length-2&&player.posOfPlayer[1]==map[0].length-2;
    }

    public boolean dead(){//玩家受到攻击，判断玩家是否死亡
        if(printMap[player.posOfPlayer[0]][player.posOfPlayer[1]]==MONSTER){
            if(player.defend<=Monsters.attack)
                player.healthPoint-=Monsters.attack-player.defend;//玩家防御力低于怪物攻击时，计算玩家受到伤害，否则玩家不受伤
        }
        if(printMap[player.posOfPlayer[0]][player.posOfPlayer[1]]==BIGMONSTER){
            if(player.defend<=BigMonsters.attack)
                player.healthPoint-=BigMonsters.attack-player.defend;
        }
        return player.healthPoint<=0;
    }

    public Text getFightInfo(){     //玩家与怪物的攻防血量信息
        if(player.healthPoint<=0)
            player.healthPoint=0;
        if(MapOfMaze.monster1.healthPoint<=0)
            MapOfMaze.monster1.healthPoint=0;
        if(MapOfMaze.monster2.healthPoint<=0)
            MapOfMaze.monster2.healthPoint=0;
        if(MapOfMaze.monster3.healthPoint<=0)
            MapOfMaze.monster3.healthPoint=0;

        if(level==1||level==2)
            return new Text("玩家：\n生命值：" + player.healthPoint + "\n"
                    + "攻击：" + player.attack + "\n"
                    + "防御：" + player.defend + "\n\n");
        else {
            return new Text("玩家：\n生命值：" + player.healthPoint + "\n"
                    + "攻击：" + player.attack + "\n"
                    + "防御：" + player.defend + "\n\n"
                    +"怪物1：\n生命值："+MapOfMaze.monster1.healthPoint+"\n"
                    + "攻击：" + Monsters.attack + "\n"
                    + "防御：" + Monsters.defend + "\n\n"
                    +"怪物2：\n生命值："+MapOfMaze.monster2.healthPoint+"\n"
                    + "攻击：" + Monsters.attack + "\n"
                    + "防御：" + Monsters.defend + "\n\n"
                    +"怪物3：\n生命值："+MapOfMaze.monster3.healthPoint+"\n"
                    + "攻击：" + BigMonsters.attack + "\n"
                    + "防御：" + BigMonsters.defend + "\n\n");
        }
    }

    //显示故事
    public void showBackGround(){
        Alert story;
        story=new Alert(Alert.AlertType.INFORMATION,Story.backGround);
        story.showAndWait();
    }
    public void showStory(){
        Alert story;
        switch (level){
            case 1:
                story=new Alert(Alert.AlertType.INFORMATION,Story.story1);
                break;
            case 2:
                story=new Alert(Alert.AlertType.INFORMATION,Story.story2);
                break;
            case 3:
                story=new Alert(Alert.AlertType.INFORMATION,Story.story3);
                break;
            case 4:
                story=new Alert(Alert.AlertType.INFORMATION,Story.story4);
                break;
            default:
                story=new Alert(Alert.AlertType.INFORMATION,Story.end);
                break;
        }
        story.showAndWait();
    }
    public void showShortStory(){
        Alert story;
        switch (player.numberOfGoods){
            case 2:
                story=new Alert(Alert.AlertType.INFORMATION,Story.good1);
                break;
            case 4:
                story=new Alert(Alert.AlertType.INFORMATION,Story.good2);
                break;
            case 6:
                story=new Alert(Alert.AlertType.INFORMATION,Story.good3);
                break;
            default:
                return;
        }
        story.showAndWait();
    }

    public void saveGame(){//存档，依次储存游戏中的各个信息
        try{
            checkDirectory("save");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./save/1.save"));
            oos.writeObject(map);
            oos.writeObject(player.posOfStep1);
            oos.writeObject(player.posOfStep2);
            oos.writeObject(player.posOfPlayer);
            oos.writeObject(MapOfMaze.monster1.posOfMonster);
            oos.writeObject(MapOfMaze.monster2.posOfMonster);
            oos.writeObject(MapOfMaze.monster3.posOfMonster);
            oos.writeObject(player.trace);
            oos.writeObject(player.numberOfSteps);
            oos.writeObject(player.numberOfGoods);
            oos.writeObject(player.numberOfKilledMonsters);
            oos.writeObject(player.score);
            oos.writeObject(level);
            oos.writeObject(mode);
            oos.writeObject(MapOfMaze.monster1.traceToMove);
            oos.writeObject(MapOfMaze.monster2.traceToMove);
            oos.writeObject(MapOfMaze.monster3.traceToMove);
            oos.writeObject(player.healthPoint);
            oos.writeObject(player.attack);
            oos.writeObject(player.defend);
            oos.writeObject(MapOfMaze.monster1.healthPoint);
            oos.writeObject(MapOfMaze.monster2.healthPoint);
            oos.writeObject(MapOfMaze.monster3.healthPoint);
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void checkDirectory(String name){
        File file = new File(name);
        if (!file.exists() || file.isFile())
            while (!file.mkdir()) {
                System.out.println("can not create directory: " + name);
            }
    }

    public void loadGame(){//读档
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./save/1.save"));
            try {
                map= (Icon[][]) ois.readObject();
                player.posOfStep1=(int[]) ois.readObject();
                player.posOfStep2=(int[]) ois.readObject();
                player.posOfPlayer=(int[]) ois.readObject();
                MapOfMaze.monster1.posOfMonster=(int[]) ois.readObject();
                MapOfMaze.monster2.posOfMonster=(int[]) ois.readObject();
                MapOfMaze.monster3.posOfMonster=(int[]) ois.readObject();
                player.trace=(StringBuilder)ois.readObject();
                player.numberOfSteps=(int)ois.readObject();
                player.numberOfGoods=(int)ois.readObject();
                player.numberOfKilledMonsters=(int)ois.readObject();
                player.score=(int)ois.readObject();
                level=(int)ois.readObject();
                mode=(Mode) ois.readObject();
                MapOfMaze.monster1.traceToMove=(StringBuilder)ois.readObject();
                MapOfMaze.monster2.traceToMove=(StringBuilder)ois.readObject();
                MapOfMaze.monster3.traceToMove=(StringBuilder)ois.readObject();
                player.healthPoint=(int)ois.readObject();
                player.attack=(int)ois.readObject();
                player.defend=(int)ois.readObject();
                MapOfMaze.monster1.healthPoint=(int)ois.readObject();
                MapOfMaze.monster2.healthPoint=(int)ois.readObject();
                MapOfMaze.monster3.healthPoint=(int)ois.readObject();

                printMap=new Icon[map.length][map[0].length];
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void getRank(){
        RankingList.getRank();
    }//打印排行榜

    public void getNewRank(){
        RankingList.setNewRank(player.score);
        RankingList.getRank();
    }//形成新排行榜并显示
}

