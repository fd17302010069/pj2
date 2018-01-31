package client;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javax.swing.*;

class ReminderPane extends Pane{//提示信息，帮助信息和玩家信息

    ReminderPane(Reminder reminder){
        switch(reminder){
            case CANNOT_MOVE : getChildren().add(new Text("无法前进"));break;
            case CANNOT_BACK : getChildren().add(new Text("无路可退"));break;
            case PICK_UP_A_GOOD : getChildren().add(new Text("你捡起了一个宝物(攻击+2，防御+2)"));break;
            case NO_GOOD : getChildren().add(new Text("周围没有宝物"));break;
            case KILL_A_MONSTER : getChildren().add(new Text("你攻击了一只怪物"));break;
            case NO_MONSTER : getChildren().add(new Text("周围没有怪物"));break;
            case HELP :
                Alert help=new Alert(Alert.AlertType.INFORMATION,"\n游戏目标：\n操纵人物从起点走到终点\n\n"+
                        "命令说明：\nh：查看帮助信息\ni：查看玩家信息\nw：向上走\na：向左走\ns：向下走\nd：向右走\n" +
                        "k：杀死怪物\np：捡起宝物\nb：后退\nq：放弃游戏\nx：退出游戏\n");
                help.showAndWait();
                break;
        }
    }

    ReminderPane(int numberOfSteps,int numberOfGoods,int numberOfKilledMonsters,int score){
        showInfo(numberOfSteps,numberOfGoods,numberOfKilledMonsters,score);
    }



    private void showInfo(int numberOfSteps,int numberOfGoods,int numberOfKilledMonsters,int score){
        Alert info=new Alert(Alert.AlertType.INFORMATION,"玩家信息："+"\n"
                +"一共走了" + numberOfSteps + "步"+"\n"
                +"一共捡起了" + numberOfGoods + "个宝物" +"\n"
                +"一共杀死了" + numberOfKilledMonsters + "个怪物"+"\n"
                +"当前得分："+score);
        info.showAndWait();
    }

}
