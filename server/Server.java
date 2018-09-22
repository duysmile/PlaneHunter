/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author duy21
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8000);
            System.out.println("Server Started");
            while(true){
                Socket s1 = server.accept();
                System.out.println("Player 1 connected");
                Socket s2 = server.accept();
                System.out.println("Player 2 connected");
                
                int [][] map1 = randomMap();
                int [][] map2 = randomMap();
                DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
                sendMap(map1, dos1);
                DataOutputStream dos2 = new DataOutputStream(s2.getOutputStream());
                sendMap(map2, dos2);
                
                DataInputStream dis1 = new DataInputStream(s1.getInputStream());
                DataInputStream dis2 = new DataInputStream(s2.getInputStream());

                int count = 1;
                while(isContain(map1, 1) && isContain(map2, 1)){
                    if (count % 2 == 0 && dis2.available() > 0){
                        System.out.println("Server received");
                        receiveData(map1, dis2, dis1, dos2, dos1);
                        count++;
                    } 
                    if (count % 2 != 0 && dis1.available() > 0){
                        System.out.println("Server received");
                        receiveData(map2, dis1, dis2, dos1, dos2);
                        count++;
                    }
                }
                System.out.println("Game Over");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //-1: fighted nothing
    //0: not fighted
    //1: has plane
    //2: plane is fighted
    public static boolean checkPoint(int x, int y, int [][] map){
        if(x < 0 || x > map.length || y < 0 || y > map[x].length){
            return false;
        }
        if (map[y][x] == -1 || map[y][x] == 2){
            return false;
        }
        return true;
    }
    
    public static void receiveData(int[][] map, DataInputStream dis1, DataInputStream dis2, DataOutputStream dos1, DataOutputStream dos2){
        try {
            int x = dis1.readInt();
            int y = dis1.readInt();
            
            if (checkPoint(x, y, map)){
                
                System.out.println("check point ok");
                if(map[y][x] == 1){
                    map[y][x] = 2;
                    sendPoint("player", x, y, 2, dos1);
                    sendPoint("enemy", x, y, 2, dos2);
                } else {
                    map[y][x] = -1;
                    sendPoint("player", x, y, -1, dos1);
                    sendPoint("enemy", x, y, -1, dos2);
                }
                if (dis2.available() > 0){
                    dis2.skipBytes(dis2.available());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static void sendPoint(String player, int x, int y, int value, DataOutputStream dos){
        try {
            dos.writeUTF(player);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(value);
        } catch (Exception e) {
        }
    }
    public static void sendMap(int[][] map, DataOutputStream dos){
        try {
            dos.writeInt(map.length);
            for (int i = 0; i < map.length; i++){
                for (int j = 0; j < map[i].length; j++){
                    dos.writeInt(map[i][j]);
                }
            }
        } catch (Exception e) {
        }
        
    }
    
    public static boolean isContain(int[][] map, int x){
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map[i].length; j++){
                if (map[i][j] == x) return true;
            }
        }
        return false;
    }
    
    public static int[][] randomPlane(){
        int[][] map = {
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0},
            {1, 1, 1}
        };
        Random rand = new Random();
        int direction = rand.nextInt(3);
        int[][] tmp = null;
        switch(direction) {
            case 0: //up
                tmp = map.clone();
                break;
            case 1: //down
                tmp = new int[4][3];
                for (int i = 0; i < 4; i++){
                    for (int j = 0; j < 3; j++){
                        tmp[i][j] = map[Math.abs(i - 3)][j];
                    }
                }
                break;
            case 2: //left
                tmp = new int[3][4];
                for (int i = 0; i < 3 ; i++){
                    for (int j = 0; j < 4; j++){
                        tmp[i][j] = map[j][2 - i];
                    }
                }
                break;
            case 3: //right
                tmp = new int[3][4];
                for (int i = 0; i < 3 ; i++){
                    for (int j = 0; j < 4; j++){
                        tmp[i][j] = map[3 - j][i];
                    }
                }
                break;
        }

        return tmp;
    }
    
    public static int[][] randomMap(){
        int[][] map = new int[10][10];
        Random rand = new Random();
        int x,y;
        int[][] plane = randomPlane();
        if(plane.length < 4) {
            x = rand.nextInt(6);
            y = rand.nextInt(6);
        } else {
            x = rand.nextInt(7);
            y = rand.nextInt(7);
        }
        for(int i = 0; i < plane.length; i++){
            for(int j = 0; j < plane[i].length; j++){
                map[y + i][x + j] = plane[i][j];
            }
        }
        return map;
    }
}