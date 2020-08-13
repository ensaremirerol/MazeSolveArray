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
public class MazeSolverArrayv2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       int [] maze = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                      1, 0, 0, 0, 0, 1, 0, 0, 0, 1,
                      1, 0, 1, 1, 0, 1, 0, 1, 0, 1,
                      1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                      1, 0, 1, 1, 0, 1, 0, 1, 0, 1,
                      1, 0, 0, 1, 0, 0, 0, 1, 0, 1,
                      1, 0, 1, 0, 0, 1, 1, 1, 0, 1,
                      1, 0, 1, 1, 0, 1, 1, 1, 0, 1,
                      1, 0, 0, 0, 0, 0, 0, 0, 2, 1,
                      1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

       Explorer explorer = new Explorer(new Maze(maze),10, 1,1);
       explorer.start();
    }
    
}
