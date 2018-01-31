package client;

import core.Icon;
import server.*;
import static server.Mode.*;
import static client.Reminder.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class LayoutMaze extends Application {

    private BorderPane pane = new BorderPane();//游戏的主界面
    private GridPane mapPane = new GridPane();//地图界面

    private String path;//图片路径

    private Image wall, road, finish, hero, treasure, monster, bigMonster , step , startImage;//游戏图片
    private ImageView startIV = new ImageView(startImage);//游戏主页背景图片
    private ImageView selectIV = new ImageView(startImage);//游戏选关界面背景图片

    private void setIniImage(){//设置图片内容
        try {
            wall = new Image(path + "wall.png");
            road = new Image(path + "space.png");
            finish = new Image(path + "end.png");
            hero = new Image(path + "hero.png");
            treasure = new Image(path + "treasure.png");
            monster = new Image(path + "monster.png");
            bigMonster = new Image(path+"bigmonster.png");
            step = new Image(path + "footprint.png");
            startImage = new Image(path+"start.png");
        }
        catch (IllegalArgumentException e){//处理图片路径错误
            Alert imageExc=new Alert(Alert.AlertType.ERROR,"找不到图片");
            imageExc.showAndWait();
            System.exit(0);
        }
    }

    private MediaPlayer playWalk,playPick,playKill,playDead,playWin,playBGM;//游戏音频

    private void setSound(){//设置音频
        try {
            //获取音频URL地址
            final String WALK_URL = getClass().getResource("sound/walk.mp3").toString();
            final String PICK_URL = getClass().getResource("sound/pick.mp3").toString();
            final String KILL_URL = getClass().getResource("sound/kill.mp3").toString();
            final String DEAD_URL = getClass().getResource("sound/dead.mp3").toString();
            final String WIN_URL = getClass().getResource("sound/win.mp3").toString();
            final String BGM_URL = getClass().getResource("sound/bgm.mp3").toString();

            Media walk = new Media(WALK_URL);
            Media pick = new Media(PICK_URL);
            Media kill = new Media(KILL_URL);
            Media dead = new Media(DEAD_URL);
            Media win = new Media(WIN_URL);
            Media bgm = new Media(BGM_URL);

            playWalk = new MediaPlayer(walk);
            playPick = new MediaPlayer(pick);
            playKill = new MediaPlayer(kill);
            playDead = new MediaPlayer(dead);
            playWin = new MediaPlayer(win);
            playBGM = new MediaPlayer(bgm);
        }
        catch (Exception e){//处理音频地址错误
            e.printStackTrace();
            Alert urlExc=new Alert(Alert.AlertType.ERROR,"无法加载音频");
            urlExc.showAndWait();
            System.exit(0);
        }
    }

    //设置不同主题
    private void setGrassTheme(){
        path="image/grass/";    //更改图片路径
        setIniImage();
        startIV.setImage(startImage);
        selectIV.setImage(startImage);
    }
    private void setSnowTheme(){
        path="image/snow/";
        setIniImage();
        startIV.setImage(startImage);
        selectIV.setImage(startImage);
    }

    private ImageView[][]ivMap;

    //菜单栏
    private javafx.scene.control.MenuBar menuBar = new javafx.scene.control.MenuBar();
    private javafx.scene.control.Menu menuGame=new Menu("Game");
    private javafx.scene.control.Menu menuFile=new Menu("File");
    private javafx.scene.control.Menu menuTheme=new Menu("Theme");
    private javafx.scene.control.Menu menuHelp=new Menu("Help");

    private MenuItem rank=new MenuItem("查看排行");
    private MenuItem exit=new MenuItem("返回首页");

    private MenuItem save=new MenuItem("save");
    private MenuItem load=new MenuItem("load");

    private MenuItem selectGrass=new MenuItem("grass");
    private MenuItem selectSnow=new MenuItem("snow");

    private MenuItem help=new MenuItem("操作说明");
    private MenuItem info=new MenuItem("玩家信息");

    private Alert win=new Alert(Alert.AlertType.INFORMATION,"恭喜你到达终点");
    private Alert dead=new Alert(Alert.AlertType.INFORMATION,"怪物吃掉了你的脑子");

    private Server server=new Server();//声明服务器

    public void start(Stage primaryStage) {

        setGrassTheme();//初始设置为grass主题

        setSound();
        playBGM.play();//播放背景音乐
        playBGM.setCycleCount(MediaPlayer.INDEFINITE);
        playBGM.setVolume(0.1);

        rank.setOnAction(e -> server.getRank());//读取当前排行榜

        menuGame.getItems().addAll(rank, exit);

        save.setOnAction(e -> {
            server.saveGame();
            JOptionPane.showMessageDialog(null, "存档成功");
        });//存档

        load.setOnAction(e -> {
            server.loadGame();
            fightInfo();
            mapPane.getChildren().clear();//清空读档前的上一幅地图
            getIniMap();
            create();
        });//读档

        menuFile.getItems().addAll(save, load);

        selectGrass.setOnAction(e -> {
            setGrassTheme();
            create();
        });

        selectSnow.setOnAction(e -> {
            setSnowTheme();
            create();
        });

        menuTheme.getItems().addAll(selectGrass, selectSnow);

        help.setOnAction(e -> new ReminderPane(HELP));//帮助信息
        info.setOnAction(e -> new ReminderPane(server.player.numberOfSteps,
                server.player.numberOfGoods,
                server.player.numberOfKilledMonsters,
                server.player.score));//玩家信息

        menuHelp.getItems().addAll(help, info);

        menuBar.getMenus().addAll(menuGame, menuFile, menuTheme, menuHelp);

        primaryStage.setTitle("MazeGame");
        pane.setCenter(mapPane);
        pane.setTop(menuBar);//界面上方设置菜单栏

        Button btNewGame = new Button("新游戏");
        Button btContinue = new Button("继续游戏");
        Button btSandbox = new Button("沙盒模式");
        Button btExit = new Button("退出游戏");

        Button bt1 = new Button("第一关");
        Button bt2 = new Button("第二关");
        Button bt3 = new Button("第三关");
        Button bt4 = new Button("第四关");
        Button btRandom = new Button("随机地图");
        Button btBack = new Button("返回");

        VBox start = new VBox();
        start.getChildren().addAll(btNewGame, btContinue, btSandbox, btExit);
        start.setPadding(new Insets(360, 240, 120, 240));
        start.setAlignment(Pos.CENTER);

        StackPane startPane = new StackPane();
        startIV.setFitWidth(600);
        startIV.setFitHeight(600);
        startPane.getChildren().addAll(startIV, start);

        Scene startScene = new Scene(startPane, 600, 600);//首页
        primaryStage.setScene(startScene);

        VBox select = new VBox();
        select.getChildren().addAll(bt1, bt2, bt3, bt4, btRandom);
        select.setPadding(new Insets(360, 240, 150, 240));
        select.setAlignment(Pos.CENTER);

        StackPane selectPane = new StackPane();
        selectIV.setFitWidth(600);
        selectIV.setFitHeight(600);
        selectPane.getChildren().addAll(selectIV, select, btBack);
        Scene selectScene = new Scene(selectPane, 600, 600);//选关界面

        Scene scene = new Scene(pane, 780, 600);//游戏界面

        bt1.setOnAction(s -> {
            server.level = 1;
            server.resetMap();//重置地图
            layout(primaryStage, scene, startScene);
        });
        bt2.setOnAction(s -> {
            server.level = 2;
            server.resetMap();
            layout(primaryStage, scene, startScene);
        });
        bt3.setOnAction(s -> {
            server.level = 3;
            server.resetMap();
            layout(primaryStage, scene, startScene);
        });
        bt4.setOnAction(s -> {
            server.level = 4;
            server.resetMap();
            layout(primaryStage, scene, startScene);
        });
        btRandom.setOnAction(s -> {
            server.level = 0;
            server.resetMap();
            server.setMonsterTrace();//随机地图中，设置怪物追踪玩家的路径
            layout(primaryStage, scene, startScene);
        });
        btBack.setOnAction(e -> primaryStage.setScene(startScene));//返回首页

        exit.setOnAction(e -> {
            mapPane.getChildren().clear();
            primaryStage.setScene(startScene);
        });//返回首页

        btNewGame.setOnAction(e -> {    //开始新游戏（故事模式）

            server.level = 1;
            server.mode = STORY;
            //重置玩家信息
            server.player.numberOfSteps = 0;
            server.player.numberOfGoods = 0;
            server.player.numberOfKilledMonsters = 0;
            server.player.score = 1000;
            server.player.healthPoint=100;
            server.player.attack=10;
            server.player.defend=10;

            server.resetMap();

            mapPane.getChildren().clear();

            server.showBackGround();//背景故事

            layout(primaryStage, scene, startScene);

            server.showStory();//故事第一章
        });

        btContinue.setOnAction(e -> {
            server.loadGame();//加载存档
            layout(primaryStage, scene, startScene);
            mapPane.getChildren().clear();
            getIniMap();
            create();
        });

        btSandbox.setOnAction(e -> {//沙盒模式
            server.mode = SANDBOX;
            primaryStage.setScene(selectScene);
        });

        btExit.setOnAction(e -> System.exit(0));//退出游戏

        //设置定时器，自动执行怪物的移动
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (server.level == 4) {  //第四关中怪物随机移动
                        if ((!server.meetTheEnd()) && (server.player.healthPoint > 0)) {
                            server.moveMonster();
                            create();
                            if(server.dead()) {//计算玩家受到伤害，判断死亡
                                Platform.runLater(()->{
                                    playDead.play();
                                    playDead.seek(Duration.ZERO);//死亡音效

                                    dead.showAndWait();//死亡信息

                                    mapPane.getChildren().clear();
                                    primaryStage.setScene(startScene);
                                });
                            }
                            Platform.runLater(() -> fightInfo());//刷新玩家、怪物血量信息
                        }
                    }
                    if (server.level == 0) { //沙盒模式--随机地图中怪物追踪玩家
                        if ((!server.meetTheEnd()) && (server.player.healthPoint > 0)) {
                            server.monsterTrace();
                            create();
                            if(server.dead()) {
                                Platform.runLater(()->{
                                    playDead.play();
                                    playDead.seek(Duration.ZERO);

                                    dead.showAndWait();

                                    mapPane.getChildren().clear();
                                    primaryStage.setScene(startScene);
                                });
                            }
                            Platform.runLater(() -> fightInfo());
                        }
                    }
                }
                catch(Exception e){     //线程运行出现错误时
                    Platform.runLater(()->{
                        Alert wrong=new Alert(Alert.AlertType.WARNING,"载入中……");
                        wrong.showAndWait();
                    });
                }
            }
        }, 1000, 1000);//怪物每一秒移动一次

        primaryStage.show();//显示窗口
    }

    private void layout(Stage primaryStage,Scene scene,Scene startScene){//显示游戏地图界面

        primaryStage.setScene(scene);
        mapPane.requestFocus();//监听键盘

        pane.setBottom(null);//清空信息提示框
        fightInfo();//显示玩家、怪物的血量和攻防信息

        getIniMap();

        key(primaryStage,startScene);//根据玩家的按键执行下一步操作

    }

    private void fightInfo()  {
        pane.setRight(server.getFightInfo());
    }

    private void getIniMap(){
        Icon[][] map = server.getMap();
        this.ivMap=new ImageView[map.length][map[0].length];//new足够的ImageView待使用
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == Icon.WALL) {
                    this.ivMap[i][j]=new ImageView(wall);
                }
                if (map[i][j] == Icon.EMPTY) {
                    this.ivMap[i][j]=new ImageView(road);
                }
                if (map[i][j] == Icon.END) {
                    this.ivMap[i][j]=new ImageView(finish);
                }
                if (map[i][j] == Icon.PLAYER) {
                    this.ivMap[i][j]=new ImageView(hero);
                }
                if (map[i][j] == Icon.GOOD) {
                    this.ivMap[i][j]=new ImageView(treasure);
                }
                if (map[i][j] == Icon.MONSTER) {
                    this.ivMap[i][j]=new ImageView(monster);
                }
                if(map[i][j] == Icon.BIGMONSTER) {
                    this.ivMap[i][j]=new ImageView(bigMonster);
                }
                if (map[i][j] == Icon.STEP) {
                    this.ivMap[i][j]=new ImageView(step);
                }

                this.ivMap[i][j].setFitHeight(30);//设置图片单位大小
                this.ivMap[i][j].setFitWidth(30);
                mapPane.add(this.ivMap[i][j],j,i);
            }
        }
    }

    private void create() {
        Icon[][] map = server.getMap();
        for (int i = 0; i < map.length; i++) { //改变地图内容
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == Icon.WALL){
                    this.ivMap[i][j].setImage(wall);
                }
                if (map[i][j] == Icon.EMPTY) {
                    this.ivMap[i][j].setImage(road);
                }
                if (map[i][j] == Icon.END) {
                    this.ivMap[i][j].setImage(finish);
                }
                if (map[i][j] == Icon.PLAYER) {
                    this.ivMap[i][j].setImage(hero);
                }
                if(map[i][j]==Icon.GOOD){
                    this.ivMap[i][j].setImage(treasure);
                }
                if(map[i][j]==Icon.MONSTER){
                    this.ivMap[i][j].setImage(monster);
                }
                if(map[i][j]==Icon.BIGMONSTER){
                    this.ivMap[i][j].setImage(bigMonster);
                }
                if(map[i][j]==Icon.STEP){
                    this.ivMap[i][j].setImage(step);
                }
            }
        }
    }

    private void key(Stage primaryStage,Scene startScene) {
        mapPane.setOnKeyPressed(e -> { //监听玩家的按键
            if ((!server.meetTheEnd())&&(server.player.healthPoint>0)) {
                switch (e.getCode()) {
                    case D:
                    case A:
                    case W:
                    case S:
                        if(server.go(e.getCode())){
                            pane.setBottom(null);
                            playWalk.play();
                            playWalk.seek(Duration.ZERO);//玩家移动音效
                            if(server.level==0) {
                                server.setMonsterTrace();
                            }//人物位置变化后重新设置怪物追踪路线

                            if(server.dead()){
                                playDead.play();
                                playDead.seek(Duration.ZERO);

                                dead.showAndWait();

                                mapPane.getChildren().clear();
                                primaryStage.setScene(startScene);
                            }
                            fightInfo();
                        }
                        else
                            pane.setBottom(new ReminderPane(CANNOT_MOVE));
                        break;
                    case B:
                        if(server.go(e.getCode())){
                            pane.setBottom(null);
                            playWalk.play();
                            playWalk.seek(Duration.ZERO);
                            if(server.level==0) {
                                server.setMonsterTrace();
                            }

                            if(server.dead()){
                                playDead.play();
                                playDead.seek(Duration.ZERO);

                                dead.showAndWait();

                                mapPane.getChildren().clear();
                                primaryStage.setScene(startScene);
                            }
                            fightInfo();
                        }
                        else
                            pane.setBottom(new ReminderPane(CANNOT_BACK));
                        break;
                    case P:
                        if(server.go(e.getCode())){
                            pane.setBottom(new ReminderPane(PICK_UP_A_GOOD));
                            playPick.play();
                            playPick.seek(Duration.ZERO);

                            fightInfo();

                            if(server.mode==STORY && (server.level==2||server.level==3))
                                server.showShortStory();//捡起宝物后显示故事
                        }
                        else
                            pane.setBottom(new ReminderPane(NO_GOOD));
                        break;
                    case K:
                        if(server.go(e.getCode())){
                            pane.setBottom(new ReminderPane(KILL_A_MONSTER));
                            playKill.play();
                            playKill.seek(Duration.ZERO);

                            fightInfo();
                        }
                        else
                            pane.setBottom(new ReminderPane(NO_MONSTER));
                        break;
                    case H:
                        new ReminderPane(HELP);
                        break;
                    case I:
                        new ReminderPane(server.player.numberOfSteps,
                                server.player.numberOfGoods,
                                server.player.numberOfKilledMonsters,
                                server.player.score);
                        break;
                    case Q:
                    case X:
                        int i=JOptionPane.showConfirmDialog(null,"确认退出游戏？","退出游戏",JOptionPane.YES_NO_OPTION);
                        if(i==0)
                            System.exit(0);
                        break;
                    default:
                        break;
                }
                create();
            }
            if(server.meetTheEnd()){//判断是否到达终点

                playWin.play();
                playWin.seek(Duration.ZERO);

                win.showAndWait();

                server.player.score+=100;

                if(server.level==4||server.mode==SANDBOX) {
                    new ReminderPane(server.player.numberOfSteps,
                            server.player.numberOfGoods,
                            server.player.numberOfKilledMonsters,
                            server.player.score);
                }//沙盒模式 或 故事模式第四关，显示得分信息

                if(server.level==4&&server.mode==STORY){
                    server.getNewRank();
                }//故事模式第四关，显示排行榜

                if(server.mode==STORY) {
                    server.level++;
                    if(server.resetMap()){//故事模式下进入下一关
                        pane.setBottom(null);
                        fightInfo();
                        create();//读取下一幅地图
                        server.showStory();//显示故事
                    }
                    else {//故事模式结束后
                        mapPane.getChildren().clear();
                        primaryStage.setScene(startScene);
                        server.showStory();
                    }
                }
                else {//沙盒模式下结束
                    mapPane.getChildren().clear();
                    primaryStage.setScene(startScene);
                }
            }
        });
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
