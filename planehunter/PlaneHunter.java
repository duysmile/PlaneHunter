/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planehunter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author duy21
 */
public class PlaneHunter extends JFrame implements MouseListener{

    /**
     * @param args the command line arguments
     */
    
    ArrayList<PointStatus> player = new ArrayList<PointStatus>();
    ArrayList<PointStatus> enemy = new ArrayList<PointStatus>();
    
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    
    public static void main(String[] args) {
        new PlaneHunter();
    }
    
    int numberOfLine = 10;
    int size = 20;
    int margin = 50;
    public PlaneHunter() {
        try {
            socket = new Socket("localhost", 8000);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            
            int length = dis.readInt();
            int[][] map = new int[length][length];
            receiveMap(dis, map, length);
            
            for (int xi = 0; xi < map.length; xi++){
                for (int yj = 0; yj < map[xi].length; yj++){
                    System.out.print(map[xi][yj]);
                }
                System.out.println();
            }
            
            this.setTitle("Plane Hunter");
        this.setSize(numberOfLine*size*2 + margin*3, numberOfLine*size + margin*2);
        this.setDefaultCloseOperation(3);
        this.addMouseListener(this);
        this.setVisible(true);
        while(true){
            receivePoint(dis);
        }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void receivePoint(DataInputStream dis){
        try {
            if(dis.available() > 0){
                String pl = dis.readUTF();
                int x = dis.readInt();
                int y = dis.readInt();
                int value = dis.readInt();
                System.out.println("Point " + x + " " + y + " " + value);
                if (pl.equals("player")){
                    player.add(new PointStatus(x, y, value));    
                } else {
                    enemy.add(new PointStatus(x, y, value));
                }
                
                this.repaint();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void receiveMap(DataInputStream dis, int[][] map, int length){
        try {
            for(int i = 0; i < length; i++){
                for(int j = 0; j < length; j++){
                    map[i][j] = dis.readInt();
                    if(map[i][j] == 1) {
                        enemy.add(new PointStatus(j, i, 1));
                    }
                }
            }
        } catch (Exception e) {
        }
    }
    
    public void paint(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.drawString("My enemy", margin, margin - 10);
        g.drawString("My plane", margin*2 + numberOfLine*size, margin - 10);
        for(int i = 0; i <= numberOfLine; i++){
            g.drawLine(margin, i*size + margin, numberOfLine*size + margin, i*size + margin);
            g.drawLine(i*size + margin, margin, i*size + margin, numberOfLine*size + margin);
            g.drawLine(margin*2 + size*numberOfLine, i*size + margin, numberOfLine*size*2 + margin*2, i*size + margin);
            g.drawLine(i*size + margin*2 + size*numberOfLine, margin, i*size + margin*2 + size*numberOfLine, numberOfLine*size + margin);
        }
        for(int i = 0; i < player.size(); i++){
            if(player.get(i).value == -1){
                g.setColor(Color.CYAN);
            } else if (player.get(i).value == 2){
                g.setColor(Color.BLUE);
            }
            
            g.fillRect(player.get(i).x*size + margin, player.get(i).y*size + margin, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(player.get(i).x*size + margin, player.get(i).y*size + margin, size, size);
        }
        for(int i = 0; i < enemy.size(); i++){
            if(enemy.get(i).value == -1){
                g.setColor(Color.PINK);
            } else if (enemy.get(i).value == 2){
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.YELLOW);
            }
            g.fillRect(enemy.get(i).x*size + margin*2 + numberOfLine*size, enemy.get(i).y*size + margin, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(enemy.get(i).x*size + margin*2 + numberOfLine*size, enemy.get(i).y*size + margin, size, size);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        try{
            int x = me.getX();
            int y = me.getY();

            if(x < margin || x >= margin + size*numberOfLine) return;
            if(y < margin || y >= margin + size*numberOfLine) return;

            int mapX = (x - margin) / size;
            int mapY = (y - margin) / size;
//            if(player.contains(new PointStatus(mapX, mapY, 1))) return;
//            player.add(new PointStatus(mapX, mapY, 1));
            dos.writeInt(mapX);
            dos.writeInt(mapY);
//            receivePoint(dis);
//            this.repaint();
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
