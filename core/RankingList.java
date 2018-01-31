package core;

import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.*;

public class RankingList {
    private static void iniRank(){      //初始化排行榜信息
        try{
            checkDirectory("rankingList");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./rankingList/1.save"));
            for(int i=0;i<5;i++) {
                oos.writeUTF("system");
                oos.writeInt(1000);
            }
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void inputNewRank(String[]nameRank,int[]scoreRank){      //储存新的排行榜信息
        try{
            checkDirectory("rankingList");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./rankingList/1.save"));
            for(int i=0;i<5;i++) {
                oos.writeUTF(nameRank[i]);
                oos.writeInt(scoreRank[i]);
            }
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void checkDirectory(String name){        //检查文件路径
        File file = new File(name);
        if (!file.exists() || file.isFile())
            while (!file.mkdir()) {
                System.out.println("can not create directory: " + name);
            }
    }

    public static void setNewRank(int score){       //设置新排行榜

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./rankingList/1.save"));
            String [] nameRank=new String[5];
            int[] scoreRank=new int[5];
            for (int i = 0; i < 5; i++) {
                nameRank[i]=ois.readUTF();
                scoreRank[i]=ois.readInt();
            }

            if(score<scoreRank[4])      //如果玩家分数低于排行榜最低分则返回，不记录玩家名和分数
                return;

            String name=JOptionPane.showInputDialog("请输入玩家名称");
            if(null==name)
                name="null";

            for(int i = 0; i < 5; i++){
                if(score>=scoreRank[i]){
                    for(int j = 4 ; j > i ; j--){
                        scoreRank[j]=scoreRank[j-1];
                        nameRank[j]=nameRank[j-1];
                    }
                    scoreRank[i]=score;
                    nameRank[i]=name;
                    break;
                }
            }//比较玩家分数和排行榜上分数，依次替换

            inputNewRank(nameRank,scoreRank);

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void getRank(){       //显示排行
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./rankingList/1.save"));
            StringBuilder rankingList=new StringBuilder();
            for (int i = 0; i < 5; i++) {
                rankingList.append(ois.readUTF()+"   "+ois.readInt()+"\n");
            }
            Alert rank=new Alert(Alert.AlertType.INFORMATION, rankingList.toString());
            rank.showAndWait();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        iniRank();
    }
}
