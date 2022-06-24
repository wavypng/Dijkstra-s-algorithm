import java.io.*;
import java.util.*;

//Homework 10 - Dijkstra’s algorithm & Forwarding Table
//By Hung Pham & Rigoberto Hinojos

public class ShortestPath {
   private static int findMin(ArrayList<Integer> D, ArrayList<Integer> N) {
      int minimumIndex = -1;
      int minimum = -1;

      for(int i = 0; i < D.size(); i++) {
         if (!N.contains(i) && D.get(i) != -1) {
            minimum = D.get(i);
            minimumIndex = i;
            break;
         }
      }

      if (minimum == -1) {
         return minimumIndex;
      }
      for (int i = 1; i < D.size(); i++){
         if (D.get(i) <= minimum && !N.contains(i) && D.get(i) != -1) {
            minimum = D.get(i);
            minimumIndex = i;
         }
      }
      return minimumIndex;
   }

   private static String calculateLinks(Hashtable<Integer, Integer> Y, int target) {
      String result = ", V" + target + ")";
      if (Y.get(target) == 0) {
         result = "(V0" + result;
      }
      else {
         while(Y.get(target) != 0) {
            result = ", V" + Y.get(target) + result;
            target = Y.get(target);
         }
         result = "(V0" + result;
      }
      return result;
   }


   public static void main(String[] args) throws IOException{

      Scanner sysIn = new Scanner(System.in);
      String fileName = "topo.txt";
      
      int n;
      int cost[][];
      String[] data;
      boolean topoStatus;
   
      System.out.println("Enter the number of routers");

      // Get valid input for n
      n = 0;

      try {
         double intitalinput = sysIn.nextDouble();

         if (intitalinput < 2) {
            System.err.println("Enter number larger than 2");
            System.exit(2);
         }
         else
            n = (int) intitalinput;
      }
      catch (InputMismatchException e) {
         System.err.println("Not a number");
         System.exit(2);
      }

      cost = new int[n][n];
      for(int i = 0; i < n; i++) {
         for(int j = 0; j < n; j++){
            if (i == j) {
               cost[i][j] = 0;
            }
            else {
               cost[i][j] = -1;
            }
         }
      }

      int rowNumber = 1;

      // add weight to cost matrix
      while(true) {
         topoStatus = true;
         try {
            Scanner in = new Scanner(new FileInputStream(fileName));
            while(in.hasNextLine()) {
               String line = in.nextLine();
               data = line.split("\t");
               int city = Integer.parseInt(data[0]);
               if (city >= n || city < 0) {
                  System.out.println("City " + city + " located at line " + rowNumber + " is invalid.");
                  topoStatus = false;
                  break;
               }
               int city2 = Integer.parseInt(data[1]);
               if (city2 >= n || city2 < 0) {
                  System.out.println("City " + city2 + " located at line " + rowNumber + " is invalid.");
                  topoStatus = false;
                  break;
               }
               int ew = Integer.parseInt(data[2]);
               if (ew < 0) {
                  System.out.println("Cost " + ew + " located at row " + rowNumber + " is invalid.");
                  topoStatus = false;
                  break;
               }
               cost[city][city2] = ew;
               cost[city2][city] = ew;
               rowNumber++;
            }
            in.close();
         }
         catch (IOException e) {
            System.out.println("\nCannot open " + fileName);
            topoStatus = false;
         }
         
         if (!topoStatus) {
            System.out.print("Please enter a new input file name: ");
            fileName = sysIn.nextLine();
         }
         else {
            break;
         }
      }

      // Djiktra's Algorithm
      ArrayList<Integer> nPrime = new ArrayList<Integer>();
      Hashtable<Integer, Integer> yPrime = new Hashtable<Integer, Integer>();
      ArrayList<Integer> D = new ArrayList<Integer>();
      ArrayList<Integer> P = new ArrayList<Integer>();
            
      nPrime.add(0);
      for (int i = 0; i < n; i++){
         P.add(-1);
         D.add(cost[0][i]);
         if (D.get(i) != -1) {
            P.set(i, 0);
         }
      }

      System.out.println("\n------------------- Iteration 0 -------------------");
      System.out.println("N' : ");
      for (int i = 0; i < nPrime.size(); i++) {
         System.out.print("V" + nPrime.get(i) + "  ");
      }
      System.out.println("\nY' : ");
      for (int item: yPrime.keySet()) {
         System.out.print("[V"+ yPrime.get(item) + ", V" + item + "] ");
      }
      System.out.println("\nDistance Vector D:\n" + Arrays.toString(D.toArray()));
      System.out.println("Predecessor Vector P: " );
      for (int i = 0; i < P.size(); i++) {
         System.out.print("V" + P.get(i) + "  ");
      }

      for (int count = 0; count < n; count++) {
         int k = findMin(D, nPrime);
         if (k == -1) {
            break;
         }
         nPrime.add(k);
         yPrime.put(k, P.get(k));
 
         for (int counter = 0; counter < n; counter++) {
            if(D.get(counter) == -1 && cost[counter][k] != -1 && !nPrime.contains(counter)) {
               D.set(counter, D.get(k) + cost[counter][k]);
               P.set(counter, k);
            }
            else if(cost[counter][k] != -1 && D.get(k) + cost[counter][k] < D.get(counter) && !nPrime.contains(counter)) {
               D.set(counter, D.get(k) + cost[counter][k]);
               P.set(counter, k);
            }
         }

         System.out.println("\n------------------- Iteration " + (count + 1) + "-------------------" );
         System.out.println("N' : ");
         for (int i = 0; i < nPrime.size(); i++) {
            System.out.print("V" + nPrime.get(i) + "  ");
         }
         System.out.println("\nY' : ");
         for (int item: yPrime.keySet()) {
            System.out.print("[V"+ yPrime.get(item) + ", V" + item + "]  ");
         }
         System.out.println("\nDistance Vector D:\n" + Arrays.toString(D.toArray()));
         System.out.println("Predecessor Vector P: " );
         for (int i = 0; i < P.size(); i++) {
            System.out.print("V" + P.get(i) + "  ");
         }
      }

       // Forwarding Table
       String linkPath;
       System.out.println("\n------------------- Forwarding Table -------------------");
       System.out.printf("%-30.30s  %-30.30s%n", "Destination", "Link");
       for (int num = 1; num < n; num++) {
          linkPath = calculateLinks(yPrime, num);
          System.out.printf("%-30.30s  %-30.30s%n", ("V"+(num)), linkPath);
       }
    }
}