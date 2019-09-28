import java.awt.*;
import java.util.*;
import java.io.*;


public class VectorTester {

   public static int WIDTH;
   public static int HEIGHT;
   public static int ZOOM;
   public static int FRAMES;
  
   public static void main(String [] args) {
      WIDTH = 100;
      HEIGHT = 100;
      ZOOM = 400;
      double AMBIENT = .2;
      Vector setAmb = new Vector (0, 0, 0, 0, 1, 0);
      setAmb.setAmbient(AMBIENT);
      FRAMES = 48;
      int lastFrame = 1;
      Color [] [] [] pixels = new Color [WIDTH][HEIGHT][FRAMES];
      for (double frameNum = lastFrame; frameNum < FRAMES; frameNum++) {
         System.out.println(new Vector (0, 0, 0, 1, 0, 0).intersect(new Edge (10, 1, 0, 0, -1, 0, 5)));
         File f = null;
         Scanner sc = null;
         ArrayList<Shader> shade = new ArrayList<Shader> ();
         ArrayList<Material> mat = new ArrayList<Material> ();
         ArrayList<Light> lit = new ArrayList<Light>();
         try {
           /* f = new File ("Input.txt");
            sc =  new Scanner (f);
            WIDTH = sc.nextInt();
            HEIGHT = sc.nextInt();
            ZOOM = sc.nextInt();
            double AMBIENT = sc.nextDouble();
            sc.nextLine();*/
           /* while (sc.hasNextLine()) {
               String s = sc.nextLine();
               Scanner scan = new Scanner(s);
               String object = scan.next();
               if (object.equals("#")) {
                  scan.nextLine();
               }
               if (object.equals("Shader")) {
                  if (scan.next().equals("VectorShader")) {
                     VectorShader sha = new VectorShader (scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                     shade.add(sha);
                     System.out.println("Happened");
                  }
               }
               else if (object.equals("Sphere")) {
                  Sphere sp = new Sphere(scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), new Color (scan.nextInt(), scan.nextInt(), scan.nextInt()), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                  mat.add(sp);
               }
               else if (object.equals("Chessboard")) {
                  Chessboard c = new Chessboard(scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), new Color (scan.nextInt(), scan.nextInt(), scan.nextInt()),new Color (scan.nextInt(), scan.nextInt(), scan.nextInt()), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                  mat.add(c);
               }
               else if (object.equals("Plane")){ 
                  Plane p = new Plane(scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), new Color (scan.nextInt(), scan.nextInt(), scan.nextInt()), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                  mat.add(p);
               }
               else if (object.equals("Light")) {
                  Light l = new Light (scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                  lit.add(l);
                  mat.add(l);
               }
               else if (object.equals("Polygon")) {
                  ArrayList<double []> j = new ArrayList<double []> ();
                  while (scan.hasNextDouble()) {
                     j.add(new double [] {scan.nextDouble(), scan.nextDouble(), scan.nextDouble()});
                  }
                  scan.next();
                  Polygon p = new Polygon(j.toArray(new double [j.size()] [3]) , new Color(scan.nextInt(), scan.nextInt(), scan.nextInt()), scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
                  mat.add(p);
               }
            }*/
            mat.add(new Sphere(4, 5.0-frameNum+(frameNum*frameNum)/48, 50, 2.0,new Color(0, 255, 255), .6, 0, 1));
            
            if (frameNum<9) {
               mat.add(new Sphere(-4, -5.0+frameNum+(frameNum*frameNum)/50, 50, 2.0,new Color(255, 0, 0), .6, 0, 1));
            }
            else {
               System.out.println(-5.0-(frameNum-19)+(frameNum-9)*(frameNum-9)/52);
               mat.add(new Sphere(-4, -5.0-(frameNum-19)+(frameNum-9)*(frameNum-9)/52, 50, 2.0, new Color(255, 255, 0), .6, 0, 1));
            }
            mat.add(new Chessboard(0, -1, 0 , 7, new Color(255, 0, 0), new Color (0, 0, 255), 1, 0, 1, 50));
            double [] g = {.5, 2, 2};
            double [] fi = {.5, 2.5, 2};
            double [] h = {3, 2, 2};
            double [] i = {3, 2.5, 2};
            double [][] j = {fi, g, h, i};
            lit.add(new Light(0, -10, 0, 1, 1));
         }
         catch (Exception e) {
            System.out.println(e);
         }
         Material [] objects = mat.toArray(new Material[mat.size()]);
         Light [] lights = lit.toArray(new Light [lit.size()]); 
         Shader [] shaders = shade.toArray(new Shader[shade.size()]);
         for (int a = 0; a < WIDTH; a++) {
            for (int b = 0; b < HEIGHT; b ++) {
            
               Vector v = new Vector (0, 0, 0, a-WIDTH/2, b-HEIGHT/2, ZOOM);
               pixels[a][b][(int)frameNum] = v.trace(objects, 0, lights, shaders);
               
            }
         }
      }
      try {
         for (int frame = lastFrame; frame < FRAMES; frame++) {
          
            DrawingPanel canvas = new DrawingPanel (WIDTH, HEIGHT);
            Graphics g = canvas.getGraphics(); 
         
         
            for (int a = 0; a < WIDTH; a++) {
               for (int b = 0; b < HEIGHT; b++) {
                     
                  g.setColor(pixels[a][b][frame%FRAMES]);
                     
                  g.drawLine(a, b, a, b);
               }
            }
            Thread.sleep(100);
         }
      }
      catch (InterruptedException e){ }
   }   
}
