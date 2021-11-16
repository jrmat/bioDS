import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.math.BigInteger;
import java.lang.Math;

/** input:
  * file with 4 initial genotypes and their fitnesses.
  * initial x*(equilibrium) fitness and genotype of these.
  * number of loci wanted in final landscape
  * 
  * output: 
  * array of hard landscape genotypes (both 16 and 64 sets) and their fitnesses (formed from initial four genotypes).
  * new values of s+, s-, and new x*.
  * 
  * NB: assumes lowest fitness genotype is all 0's i.e 00, 0000, etc.
  **/
public class code{
  public static void main(String[] args) throws FileNotFoundException, IOException{
    double lowX = 0.0, lowY, highY, eqm = 6.0, sPlus, sMinus; //initial x*(eqm) given
    int row, loci = 6; //row holds the row of the genotype that has the new lowest fitness
    String eqmGenotype = "11";
    
    String [][] initial = new String [4][2]; //array of initial 4 genotypes
    String [][] myArray = new String [16][2]; //array of new genotypes
    
    
    File file = new File("/Users/Jada 1/Desktop/code/data.txt"); 
    Scanner sc = new Scanner(file);
    
    //reads file into array of initial 4 genotypes
    for (int i=0; sc.hasNext(); i++) {
      for (int j=0; j<initial[i].length; j++) {
        initial[i][j]= sc.next();
      }
    }
    sc.close();
    
    //initial values
    row = lowXRow(initial);    
    lowX = Double.parseDouble(initial[row][1]);    
    highY = highY(initial, row);
    sPlus = sPlus(lowX, highY);
    
    lowY = lowY(initial, row, highY, sPlus, lowX);
    sMinus = sMinus(lowX, lowY, sPlus);
    
    
    for(int i=0; i<(loci/2)-1; i++){
      int length = (int)Math.pow(2,4+2*i); //new length of array to fill
      String[][] temp = new String [length][2]; //used to empty myArray
      
      myArray = Arrays.copyOf(temp, length); //resizes array and empties it
      
      myArray = fitnessArray(initial, myArray, sPlus, sMinus, eqm, eqmGenotype); //fills array
      
      eqm = newEQM(myArray);
      eqmGenotype = "00" + eqmGenotype;
      row = lowXRow(myArray);    
      lowX = Double.parseDouble(myArray[row][1]);    
      highY = highY(myArray, row);
      sPlus = sPlus(lowX, highY);
      
      lowY = lowY(myArray, row, highY, sPlus, lowX); 
      sMinus = sMinus(lowX, lowY, sPlus);
      
      initial = Arrays.copyOf(myArray, myArray.length); //sets myArray to initial array for next loop
    }
    
    
    BufferedWriter br = new BufferedWriter(new FileWriter("myfile.csv")); //reads file into csv file
    StringBuilder sb = new StringBuilder();
    
    sb.append("sample, fitness, A, B, C, D, E, F\n");
    
    for(int i=0; i<myArray.length; i++){
      sb.append(i+1 + ", " + myArray[i][1] + ", ");     
      for(int j=0; j<loci; j++){
        String gene = myArray[i][0];
        sb.append(gene.charAt(j));
        if(j==loci-1){
          break;
        }
        sb.append(", ");
      }
      sb.append("\n");
    }
    
    br.write(sb.toString());
    br.close();
    
    BufferedWriter br2 = new BufferedWriter(new FileWriter("visual.txt")); //reads into file to be visualised
    StringBuilder sb2 = new StringBuilder();
    
    for(int i=0; i<myArray.length; i++){
      sb2.append(myArray[i][0] + "\t" + myArray[i][1] + "\n");
    }

    br2.write(sb2.toString());
    br2.close();
    
    
    //prints array of genotypes and new fitness landscape values
    for(int i=0; i<myArray.length; i++){
      System.out.println(myArray[i][0] + "\t" + myArray[i][1]);
    }
    System.out.println("\nnew equilibrium is " + eqm + "\nnew s+ is " + sPlus + "\nnew s- is " + sMinus +"\n");
    
  }
  
  public static double sPlus(double lowX, double highY){
    return highY - lowX;
  }
  
  public static double sMinus(double lowX, double lowY, double sPlus){
    if((lowX + sPlus)>lowY){
      return lowY-lowX;
    } else{
      return sPlus/2;
    }
  }
  
  public static double newEQM(String[][] myArray){
    int num = 0, zCount = (myArray[0][0].length())-3; //how many zeroes in eqm genotype
    String zeroes="0", genotype, eqm=null;
    
    while(num<zCount){
      zeroes += "0";
      num++;
    }
    
    genotype = zeroes + "11"; //eqm genotype
    
    for(int i=0; i<myArray.length; i++){
      if(myArray[i][0].equals(genotype)){
        eqm = myArray[i][1];
        break;
      }
    }
    return Double.parseDouble(eqm);
  }
  
  public static int lowXRow(String[][] myArray){
    int x = 0;
    
    for(int i=0; i<myArray.length-1; i++){
      if(Double.parseDouble(myArray[i][1])<Double.parseDouble(myArray[x][1])){
        x = i;
      }
    }
    return x;
  }
  
  public static double highY(String[][] myArray, int row){
    String x = myArray[row][0];
    double highY = Double.parseDouble(myArray[row][1]);
    String comp;
    
    for(int i=0; i<myArray.length; i++){
      int diff=0; 
      double fitness = Double.parseDouble(myArray[i][1]);
      comp = myArray[i][0];
      
      for(int index=0; index<comp.length(); index++){ //finds how much the two genotypes differ by
        if(x.charAt(index)!=comp.charAt(index)){
          diff++;
        }
      }
      
      if(diff==1 && fitness>highY){ //finds highest fitness neighbour of lowest finess genotype
        highY = fitness;
      }   
    }
    return highY;
  }
  
  public static double lowY(String[][] myArray, int row, double highY, double sPlus, double lowX){
    String x = myArray[row][0];
    double lowY = highY+1, temp = highY;
    String comp;
    
    for(int i=0; i<myArray.length; i++){
      int diff=0; 
      double fitness = Double.parseDouble(myArray[i][1]);
      comp = myArray[i][0];
      
      for(int index=0; index<comp.length(); index++){
        if(x.charAt(index)!=comp.charAt(index)){
          diff++;
        }
      }
      
      if(diff==1 && fitness<lowY && fitness>lowX){
        temp = fitness;
        if((sPlus + lowX) > fitness){
          lowY = fitness;
        }
      }
    }
    
    if(lowY==highY+1){
      lowY=temp;
    }
    return lowY;
  }  
  
  public static String c1(double x, double sMinus){
    double result = x + sMinus;
    return String.valueOf(result);
  }
  
  
  public static String c2(double x, double sPlus){
    double result = x + sPlus;
    return String.valueOf(result);
  }
  
  public static String c3(String xGenotype, String eqmGenotype, double sPlus, String[][] initial, double eqmFit){
    double xor=0.0, val;
    
    BigInteger x = new BigInteger(xGenotype); 
    BigInteger eqm = new BigInteger(eqmGenotype); 
    BigInteger oplus = x.xor(eqm);
    
    String genotype = oplus.toString();
    
    for(int i=0; i<initial.length; i++){
      if(initial[i][0].equals(genotype)){
        xor = Double.parseDouble(initial[i][1]);
        break;
      }
    }
    val = xor + eqmFit + 2*sPlus;
    return String.valueOf(val);
  }
  
  public static String[][] fitnessArray(String[][] initial, String[][] myArray, double sPlus, double sMinus, double eqm, String eqmGenotype){
    double x = 0.0;
    String xGenotype = null;
    
    for(int i=0; i<initial.length; i++){
      int j=0, k=4*i;
      xGenotype = initial[i][j];
      x = Double.parseDouble(initial[i][j+1]);
      
      myArray[k][j] = initial[i][j] + "00";
      myArray[k][j+1] = initial[i][j+1];
      
      myArray[k+1][j] = initial[i][j] + "01";
      myArray[k+1][j+1] = c1(x, sMinus);          
      
      myArray[k+2][j] = initial[i][j] + "10";
      if(x==eqm){
        myArray[k+2][j+1] = c2(x, sPlus);
      } else{
        myArray[k+2][j+1] = c1(x, sMinus);
      }
      
      myArray[k+3][j] = initial[i][j] + "11";
      myArray[k+3][j+1] = c3(xGenotype, eqmGenotype, sPlus, initial, eqm);
      
    }
    return myArray;
  }
}