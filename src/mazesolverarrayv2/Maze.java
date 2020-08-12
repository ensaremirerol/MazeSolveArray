/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazesolverarrayv2;

/**
 *
 * @author ensar
 */
public class Maze {
    private int [] maze;
    public Maze(int [] maze){
        this.maze = maze;
    }
    
    public int getVal(int index){
        return maze[index];
    }
    
}
