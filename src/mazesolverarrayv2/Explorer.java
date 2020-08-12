/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazesolverarrayv2;

import java.util.Scanner;

/**
 *
 * @author ensar
 */
/**
 * 
 * Yönler 0: North / 1: East / 2: South / 3: West
 * Konuma göre index hesabı: Y * (arrayin bir kenar uzunluğu) + x
 * Arraydaki sayıların anlamları:
 *  -1: bilgi yok
 *  -2: duvar
 *  0 ve 0 dan büyük her değer yol olduğunu ve değeri ise ziyaret sayısını belirtir
 * Algoritma bazen hareketde tekrara düşmekde eğerki bir çözüm bulamazsam
 * Robotun düzgün hareket etmesi için aklımda bir fikir var
 */
public class Explorer {
    private Maze staticMaze;
    private int maze [];
    private int mazeSide;
    private int x;
    private int y;
    private int startX, startY;
    private int endX, endY, endIndex;
    private final Scanner keyboard;
    private final int DEAD_END_VAL = 99999;
    private String [] path = new String [10];
    
    public Explorer(int endX, int endY){
        for (int i = 0; i < path.length; i++) path[i] = "";
        // Bu blok bilgisayarda çalışması içindir
        // Bitiş pozisyonuna göre array in ayarını çeker
        // Arduino da başta sabit bir array vereceğiz
        if (endX >= endY){
            this.mazeSide = endX + 1;
        }
        else{
            this.mazeSide = endY + 1;
        }
        // Blok son
        this.maze = new int [mazeSide * mazeSide]; // Maze Array i
        for(int i = 0; i < maze.length; i++) maze[i] = -1; // Maze in 
                                                 //  tüm elemanlarını -1 yapar
        // Pozisyon ataması Arduino da 5 e 5 atanması uygundur (Düşüncem böyle)
        this.x = 1; 
        this.y = 1;
        this.startX = x; // Başlangıç pozisyonunu tutar
        this.startY = y;
        this.endX = endX; // Bilgisayarda çalışma için Maze in çıkış pozisyonu
        this.endY = endY;
        this.endIndex = getIndex(endX, endY);
        keyboard = new Scanner(System.in); // Sensor girişlei için Scanner
    }
    
    public Explorer(Maze staticMaze, int mazeSide, int startX, int startY){
        for (int i = 0; i < path.length; i++) path[i] = "";
        this.mazeSide = mazeSide;
        this.maze = new int [mazeSide * mazeSide]; // Maze Array i
        for(int i = 0; i < maze.length; i++) maze[i] = -1; // Maze in 
                                                 //  tüm elemanlarını -1 yapar
        this.x = startX; 
        this.y = startY;
        this.startX = startX; // Başlangıç pozisyonunu tutar
        this.startY = startY;
        this.staticMaze = staticMaze;
        this.keyboard = null;
    }

    /**
     * Temel Metodlar
     */
    
    // X ve Y nin değerine göre index verisi döndürür
    private int getIndex(int x, int y){
        return y * mazeSide + x;
    }
    
    // Arrayi genişletir eski array yeni array in ortasına yerleştirilir
    private void expandArray(){
        int newSideSize = mazeSide * 2;
        int offset = (newSideSize - mazeSide) / 2;
        int [] temp = new int [newSideSize * newSideSize];
        for(int i = 0; i < temp.length; i++) temp[i] = -1; // Yeni array in tüm elamanlarını -1 yapar
        // Bu blok eski array i yeni arrayin ortasına yazar
        for (int i = 0; i < mazeSide; i++) {
            for (int j = 0; j < mazeSide; j++) {
                temp[(i + offset) * newSideSize + j + offset] = maze[getIndex(j, i)];
            }
            
        }
        // Koordinat ayarlamalrı
        x += offset;
        y += offset;
        startX += offset;
        startY += offset;
        endX += offset;
        endY += offset;
        endIndex = getIndex(endX, endY);
        maze = temp;
        mazeSide = newSideSize;
        // Bilgilendirme
        System.out.println("Array expanded");
        System.out.println("New end pos:"+ endX + ", " + endY);
    }
    
    private int [] getMazeVal(int [] index){
        int [] returnArray = new int [4];
        returnArray[0] = maze[index[0]];
        returnArray[1] = maze[index[1]];
        returnArray[2] = maze[index[2]];
        returnArray[3] = maze[index[3]];
        return returnArray;
    }
    
    // X ve Y bloğunu verilen yöne göre değiştirir
    private void changeCorr(int direction){
        switch(direction){
            case 0: y++; break;
            case 1: x++; break;
            case 2: y--; break;
            case 3: x--; break;
            default:break;
        }
        // Eğerki X veya Y Sınıra dayandıysa array genişletilir
        if (x <= 0 || y <= 0 || x >= mazeSide  || y >= mazeSide - 1) expandArray();
    }
    
    // Sensor okuma fonksiyonu
    private int readSensor(int index){
        if (keyboard != null){
            return keyboard.nextInt();
        }
        else{
            int val = staticMaze.getVal(index);
            if (val == 2){
                endIndex = index;
                return 0;
            }
            else{
                return val;
            }
        }
    }
    
    // Verilen yön indexinin yazdırılmasını sağlar
    private void printDirection(int direction){
        switch(direction){
            case 0: System.out.println("N"); break;
            case 1: System.out.println("E"); break;
            case 2: System.out.println("S"); break;
            case 3: System.out.println("W"); break;
            default:break;
        }
    }
    
    // Yukardakinin aynısı sadece direk String döndürür
    // Reverse yolun çizilmesinde kullanılır
    private String returnDirection(int direction){
        switch(direction){
                case 0: return "N";
                case 1: return "E";
                case 2: return "S";
                case 3: return "W";
                default: return null;
            }
    }
    
    // Labirenti yazdırır
    private void printMaze(){
        for (int i = 0; i < mazeSide; i++) {
            for (int j = 0; j < mazeSide; j++) {
                
                System.out.printf("%10s","|"+maze[i*mazeSide + j] + "|");
            }
            System.out.println();
            for (int j = 0; j < mazeSide; j++) {
                System.out.printf("-----");
            }
            System.out.println();
        }
    }
    
    private boolean anyZeros(){
        for(int val:maze){
            if (val == 0){
                return true;
            }
        }
        return false;
    }
    
    // Array i oluşturur
    private void explore(){
        
        maze[getIndex(x, y)] = 0;
        
        int [] surroundIndexes = new int[4];
        
        int [] surroundValues;
        
        int lowestVal;
        
        int lowestValIndex = 4;
        
        int wallCount = 0;
        
        boolean deadEndTrigger;
        
        boolean zeroTriger;
        
        while(anyZeros()){     
           
           // Mevcut bloğun etrafındaki indexleri yazar
           surroundIndexes[0] = getIndex(x, y + 1);
           surroundIndexes[1] = getIndex(x + 1, y);
           surroundIndexes[2] = getIndex(x, y - 1);
           surroundIndexes[3] = getIndex(x - 1, y);
           
           // Etraftaki sayıları bu array e yazar
           surroundValues = getMazeVal(surroundIndexes);
           
           deadEndTrigger = false;
           
           zeroTriger = false;
           
           for(int val: surroundValues){
               if(val == DEAD_END_VAL){
                   deadEndTrigger = true;
               }
               else if(val == 0){
                   zeroTriger = true;
               }
           }
           
           if(!zeroTriger && deadEndTrigger){
               maze[getIndex(x, y)] = DEAD_END_VAL;
           }
           
           System.out.println("X: " + x + " Y: " + y); // Bilgilendirme
           
           if (maze[getIndex(x, y)] != DEAD_END_VAL){
              maze[getIndex(x, y)]++; // Mevcut konumun sayısını 1 artırır (Ziyaret sayısı) 
           }
           
           printMaze(); // Bilgilendirme
           
           for(int i=0; i< surroundIndexes.length; i++){              
               if(surroundValues[i] == -1){    
                   printDirection(i);
                   // readSensor den gelen bilgi eğer 0 ise 0 yaz değilse -2 yaz
                   maze[surroundIndexes[i]] = readSensor(surroundIndexes[i]) == 0 ? 0 : -2;
                   surroundValues[i] = maze[surroundIndexes[i]];
               }
           }
           
           lowestVal = DEAD_END_VAL; // Çıkmaz sokaklar bu sayı ile işaretlendiği için
                              // lowestVal da bu sayı kullanılıyor
           lowestValIndex = 4;
           
           wallCount = 0;
           
           for (int i=0; i < surroundIndexes.length; i++){
               
               // Duvar sayısını sayar
               if (maze[surroundIndexes[i]] == -2){
                   wallCount++;
               }
               // Düşük değer kontrolü
               if(surroundValues[i] != -2 && lowestVal
                       > surroundValues[i]){
                   lowestVal = surroundValues[i];
                   lowestValIndex = i;
               }
           }
           
           if (wallCount == 3 && !(startX == x && startY == y)){
               maze[getIndex(x, y)] = DEAD_END_VAL;
           }
           
            changeCorr(lowestValIndex);
        }
    }
    
    private void changeToZero(){
        for(int i = 0; i < maze.length; i++){
            maze[i] = maze[i] >= 0?0:-2;
        }
    }
    
    private void findPath(String currPath, int tX, int tY, int prevIndex){
        if (getIndex(tX, tY) == endIndex){
            for(int i = 0; i < path.length; i++){
                if(path[i] != null){
                    path[i] = currPath;
                    break;
                }
            }
        }

        else{
            boolean loopDedect = false;
            int [] surroundIndexes = new int [4];
            int [] surroundValues;
            int currVal = maze[getIndex(tX, tY)];
            int newX = tX;
            int newY = tY;
            surroundIndexes[0] = getIndex(tX, tY + 1);
            surroundIndexes[1] = getIndex(tX + 1, tY);
            surroundIndexes[2] = getIndex(tX, tY - 1);
            surroundIndexes[3] = getIndex(tX - 1, tY);
            
            surroundValues = getMazeVal(surroundIndexes);
            
            for(int i = 0; i < surroundIndexes.length; i++){
                if(surroundIndexes[i] != prevIndex && surroundValues[i] >= 0){
                    if((surroundValues[i] == 0) || (currVal + 1 <= surroundValues[i])){
                        maze[surroundIndexes[i]] = currVal + 1;
                        newX = tX;
                        newY = tY;
                        switch(i){
                            case 0: newY++; break;
                            case 1: newX++; break;
                            case 2: newY--; break;
                            case 3: newX--; break;
                            default: break;
                        }
                        System.out.println("\n");
                        printMaze();
                        findPath(currPath + returnDirection(i), newX, newY, getIndex(tX, tY));
                    }
                    else if(currVal >= surroundValues[i]){
                        loopDedect = true;
                        break;
                    } 
                }

            }
            
        }
    }
    
    // Ana metod
    
    public void start(){
        explore();
        changeToZero();
        findPath("", startX, startY, -1);
        for(String val: path){
            System.out.println(val);
        }
    }
    
    
}
