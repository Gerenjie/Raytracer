import java.awt.Color;
import java.util.*;
public class Vector
{
  Color DEFAULT = Color.BLACK;
  double[] slopes = new double[3];
  double[] point = new double[3];
  static double AMBIENT;
  static double RECURSE_MAX = 10.0;
  static double EPSILON = .0001; 
  Color c;
  
  public Vector(double x, double y, double z, double xSlope, double ySlope, double zSlope)
  {
    double slopeMag = Math.sqrt(xSlope * xSlope + ySlope * ySlope + zSlope * zSlope);
    
    slopes[0] = (xSlope / slopeMag);
    slopes[1] = (ySlope / slopeMag);
    slopes[2] = (zSlope / slopeMag);
    
    point[0] = x;
    point[1] = y;
    point[2] = z;
    
    c = DEFAULT;
  }
  
  public Vector(double[] pos, double[] Slop)
  {
    slopes = unit(Slop);
    point = pos;
    c = DEFAULT;
  }
  
  public double[] getSlope() { return slopes; }
  
  public double[] getPoint()
  {
    return point;
  }
  
  public double specTrace(Material[] objects, int recursionLevel, Light[] lights) {
    double smallestDist = Double.MAX_VALUE;
    int smallestIndex = -1;
    for (int a = 0; a < objects.length; a++) {
      double currentDist = measureIntersect(objects[a]);
      if ((currentDist < smallestDist) && (currentDist != -1.0) && (currentDist > EPSILON)) {
        smallestDist = currentDist;
        smallestIndex = a;
      }
    }
    if (smallestIndex == -1) {
      return 0.0;
    }
    if ((objects[smallestIndex] instanceof Light)) {
      return measureBrightness(objects[smallestIndex]);
    }
    return 0.0;
  }
  
  public Color trace(Material[] objects, int recursionLevel, Light[] lights, Shader [] shaders) {
    if (recursionLevel > RECURSE_MAX) {
      return DEFAULT;
    }
    double smallestDist = Double.MAX_VALUE;
    int smallestIndex = -1;
    for (int a = 0; a < objects.length; a++) {
      double currentDist = measureIntersect(objects[a]);
      if ((currentDist < smallestDist) && (currentDist != -1.0) && (currentDist > EPSILON)) {
        smallestDist = currentDist;
        smallestIndex = a;
      }
    }
    if (smallestIndex == -1)
      return DEFAULT;
    if ((objects[smallestIndex] instanceof Light)) {
      return Color.WHITE;
    }
    if (smallestIndex == 0) {}
    

    return intersect(smallestIndex, recursionLevel + 1, smallestDist, lights, objects, shaders);
    
  }
  
  public Color intersect(int index, int recursionLevel, double distAway, Light[] lights, Material[] objects, Shader [] shaders)
  {
    Material object = objects[index];
    if ((object instanceof Light)) {
      return scale(Color.WHITE, ((Light)object).getMagnitude());
    }
    double[] intersectionPoint = { point[0] + slopes[0] * distAway, point[1] + slopes[1] * distAway, point[2] + slopes[2] * distAway };
    Shader activeShader = null;
    for (int a = 0; a < shaders.length; a++) {
      if (shaders[a].containsPoint(intersectionPoint)) {
         activeShader = shaders[a];

      } 
    }
    Double brightness = Double.valueOf(0.0);
    double[] objectNormal = object.getNormal(intersectionPoint);
    if (activeShader instanceof VectorShader) {
    //  System.out.println("replaced");
      objectNormal = ((VectorShader)activeShader).getVector();
    }
    double[] reflectionSlopes = reflect(objectNormal);
    Vector reflectorVector = new Vector(intersectionPoint, reflectionSlopes);
    for (int a = 0; a < lights.length; a++)
    {
      double brightmag = 1.0;
      Vector lightVector = new Vector(intersectionPoint, minus(lights[a].getPosition(), intersectionPoint));
      double lightDist = magnitude(minus(lights[a].getPosition(), intersectionPoint));
      double smallestDist = Double.MAX_VALUE;
      
      for (int b = 0; b < objects.length; b++) {
          double currentDist; 
          if (((objects[b] instanceof Light)) || (objects[b] == object)) {
          currentDist = -1.0;
        }
        else {
          currentDist = lightVector.measureIntersect(objects[b]);
        }
        if ((currentDist < smallestDist) && (currentDist != -1.0) && (currentDist > EPSILON)) {
          smallestDist = currentDist;
        }
      }
      if ((Math.abs(smallestDist - lightDist) < 0.01) || (smallestDist > lightDist) || (smallestDist < 0.0)) {
        brightness = Double.valueOf(brightness.doubleValue() + lights[a].getMagnitude() * minZero(dot(unit(minus(lights[a].getPosition(), intersectionPoint)), objectNormal)));
      }
    }
    

    double brightnessOverflow = 0.0;
    if (brightness.doubleValue() > 1.0) {
      brightnessOverflow = (brightness.doubleValue() - 1.0) / 100.0;
      brightness = Double.valueOf(1.0);
      if (brightnessOverflow > 1.0) {
        brightnessOverflow = 1.0;
      }
    }
    brightness = Double.valueOf(brightness.doubleValue() * (1.0 - AMBIENT));
    brightness = Double.valueOf(brightness.doubleValue() + AMBIENT);
    double spec = reflectorVector.specTrace(objects, recursionLevel, lights);
    if (brightness.doubleValue() > 1.0) {
      brightness = 1.0;
    }

    Color objectColor = object.getColor(intersectionPoint);
    double c1 = -dot(objectNormal, slopes);
    double c2 = Math.sqrt(1.0 - Math.pow(object.getRefractivity(), 2.0) * (1.0 - c1 * c1));
    Vector refractionVector = new Vector(intersectionPoint, sum(scale(slopes, object.getRefractivity()), scale(objectNormal, object.getRefractivity() * c1 - c2)));
    Color transColor;
    if (object.getTransparency()!=0) {
      transColor = refractionVector.trace(objects, recursionLevel + 1, lights, shaders);
    }
    else {
      transColor = Color.BLACK;
    }
    Color reflectorColor;
    
    if (object.getReflectivity()!=0) {
      reflectorColor = reflectorVector.trace(objects, recursionLevel, lights, shaders);
    }
    else {
      reflectorColor = Color.BLACK;
    }
    //System.out.println(reflectorColor + " * " + object.getReflectivity());

    float bright = (float)brightness.doubleValue();
    objectColor = overflow (objectColor, spec);
    Color c = combineColors(scale(objectColor, bright), transColor, reflectorColor, object.getReflectivity(), object.getTransparency());
    return c;
  }
  
  public Color overflow(Color c, double overflow) {
    double r = c.getRed() / 255.0;
    double g = c.getGreen() / 255.0;
    double b = c.getBlue() / 255.0; 
    return new Color((float)(overflow + (1.0 - overflow) * r), (float)(overflow + (1.0 - overflow) * g), (float)(overflow + (1.0 - overflow) * b));
  }
  
  public float max1(float f) {
    if (f > 1.0) {
      return (float)1.0;
    }
    return f;
  }
  
  public double measureIntersect(Material object)
  {
    if ((object instanceof Sphere)) {
      Sphere sphere = (Sphere)object;
      double []  center = sphere.getCenter();
      double radius = sphere.getRadius();
      if ((point[0]<center[0]-radius&&slopes[0]<0) || (point[1]<center[1]-radius&&slopes[1]<0) || (point[2]<center[2]-radius&&slopes[2]<0)
        ||(point[0]>center[0]+radius&&slopes[0]>0) || (point[1]>center[1]+radius&&slopes[1]>0) || (point[2]>center[2]+radius&&slopes[2]>0)) {
         return -1;
         
      }
      double[] vectorToSphereVector = minus(sphere.getCenter(), point);
      double distanceToClosestPoint = dot(vectorToSphereVector, slopes);
      double centerToClosestPointDistance = magnitude(minus(sphere.getCenter(), sum(scale(slopes, distanceToClosestPoint), point)));
      if (centerToClosestPointDistance < sphere.getRadius()) {
        return distanceToClosestPoint - Math.sqrt(sphere.getRadius() * sphere.getRadius() - centerToClosestPointDistance * centerToClosestPointDistance);
      }
      
      return -1.0;
    }
    
    if ((object instanceof Plane)) {
      Plane plane = (Plane)object;
      double[] planeSlopes = plane.getSlopes();
      double d = plane.getD();
      double distance = -(planeSlopes[0] * point[0] + planeSlopes[1] * point[1] + planeSlopes[2] * point[2] + d) / (planeSlopes[0] * slopes[0] + planeSlopes[1] * slopes[1] + planeSlopes[2] * slopes[2]);
      if (distance > 0.0) {
        return distance;
      }
      
      return -1.0;
    }
    
    if ((object instanceof Polygon)) {
      Polygon polygon = (Polygon)object;
      double [] polygonSlopes = polygon.getSlope();
      double d = polygon.getD();
      double [] [] polygonPoints = polygon.getPoints();
      double [] polygonPoint = polygonPoints[0];
      double distance = -(polygonSlopes[0] * point[0] + polygonSlopes[1] * point[1] + polygonSlopes[2] * point[2] + d) / (polygonSlopes[0] * slopes[0] + polygonSlopes[1] * slopes[1] + polygonSlopes[2] * slopes[2]);
      double [] intersectionPoint = plus(point, scale(slopes, distance));
      double [] mins = polygon.getMins();
      double [] maxes = polygon.getMaxes();
      for (int a = 0; a < intersectionPoint.length; a++) { // Bounding box!
         if (intersectionPoint [a] < mins[a]-EPSILON || intersectionPoint[a] > maxes[a]+EPSILON) {
            return -1;
         }
      }
      double angle = 0;
      for (int a = 0; a < polygonPoints.length; a++) {
         double [] vec1 = minus(polygonPoints[a], intersectionPoint);
         double [] vec2 = minus(polygonPoints[(a+1)%polygonPoints.length], intersectionPoint);
         double theta = fastAcos(dot(vec1,vec2)/(magnitude(vec1)*magnitude(vec2)));
         if (dot(cross(vec1, vec2), slopes) > 0) {
            angle += theta;
         }
         else {
            angle -= theta;
         }
      }
      if (Math.abs(angle)>Math.PI) {
         return distance;
      }
      else {
         return -1;
      }
         
      
     
     
     
      /*int intersections = 0;
      Vector randomVector = new Vector (intersectionPoint, minus(polygon.getCentroid(), intersectionPoint));
      
      for (int a = 0; a < edges.length; a++) {
         if (randomVector.intersect(edges[a])) {
            intersections++;

         }
      }
      
     // System.out.println(intersections);
      if (intersections%2 == 1){
         return distance;
      }
      return -1.0;*/
      /*
      Vector randomVector = new Vector(intersectionPoint, minus(intersectionPoint, polygonPoint));
      for (int a = 0; a < edges.length; a++) {
        if (randomVector.intersect(edges[a])) {
          inside = !inside;
        }
      }
      if ((distance > 0.0) && (inside)) {
         return distance;
      }*/
      
      
    }
    
    if ((object instanceof Light)) {
      Light light = (Light)object;
      double[] vectorToLightVector = minus(light.getPosition(), point);
      double distanceToClosestPoint = dot(vectorToLightVector, slopes);
      double centerToClosestPointDistance = magnitude(minus(light.getPosition(), sum(scale(slopes, distanceToClosestPoint), point)));
      if (centerToClosestPointDistance < light.getRadius()) {
        return distanceToClosestPoint - Math.sqrt(Math.pow(light.getRadius(), 2.0) - centerToClosestPointDistance * centerToClosestPointDistance);
      }
    }
    
    return -1.0;
  }
  
  public double measureBrightness(Material material)
  {
    Light light = (Light)material;
    double[] vectorToLightVector = minus(light.getPosition(), point);
    double distanceToClosestPoint = dot(vectorToLightVector, slopes);
    double centerToClosestPointDistance = magnitude(minus(light.getPosition(), sum(scale(slopes, distanceToClosestPoint), point)));
    return Math.pow(Math.sqrt(light.getRadius() * light.getRadius() - centerToClosestPointDistance * centerToClosestPointDistance) / light.getRadius(), 2.0);
  }
  
  public static void setAmbient(double amb)
  {
    AMBIENT = amb;
  }
  
  public static double magnitude(double[] vector) {
    return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
  }
  
  public static double[] sum(double[] one, double[] two) {
    double[] summed = { one[0] + two[0], one[1] + two[1], one[2] + two[2] };
    return summed;
  }
  
  public static double[] scale(double[] v, double scale) {
    double[] scaled = { v[0] * scale, v[1] * scale, v[2] * scale };
    return scaled;
  }
  
  public static double[] minus(double[] one, double[] two) {
    double[] Returnvalue = { one[0] - two[0], one[1] - two[1], one[2] - two[2] };
    return Returnvalue;
  }
  
  public static double[] plus(double[] one, double[] two) { return new double[] { one[0] + two[0], one[1] + two[1], one[2] + two[2] }; }
  
  public static double dot(double[] vector1, double[] vector2)
  {
    return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2];  

  }
  
  public static double minZero(double d)
  {
    if (d > 0.0) {
      return d;
    }
    return 0.0;
  }
  
  public static double[] unit(double[] vector) {
    double mag = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    if (mag < 0.0) {}
    
    double[] unitVector = { vector[0] / mag, vector[1] / mag, vector[2] / mag };
    return unitVector;
  }
  
  public String toString() { return "[" + point[0] + "," + point[1] + "," + point[2] + "] ==> [" + slopes[0] + "," + slopes[1] + "," + slopes[2] + "]"; }
  
  public double[] reflect(double[] normal)
  {
    double[] newNormal = scale(normal, -1.0);
    return minus(slopes, scale(newNormal, 2.0 * dot(slopes, newNormal)));
  }
  
  public static Color combineColors(Color a, Color b, double mixA) {
    float redMix = (float)(a.getRed() * mixA + b.getRed() * (1.0 - mixA));
    float greenMix = (float)(a.getGreen() * mixA + b.getGreen() * (1.0 - mixA));
    float blueMix = (float)(a.getBlue() * mixA + b.getBlue() * (1.0 - mixA));
    return new Color(redMix, greenMix, blueMix);
  }
  
  public static Color combineColors(Color a, Color b, Color c, double reflection, double transparency)
  {
    if ((transparency < 0.0) || (reflection < 0.0))
    {
      throw new IllegalArgumentException();
    }
    
    float redMix = (float)((a.getRed() * (1.0 - reflection - transparency) + b.getRed() * transparency + c.getRed() * reflection) / 255.0);
    float greenMix = (float)((a.getGreen() * (1.0 - reflection - transparency) + b.getGreen() * transparency + c.getGreen() * reflection) / 255.0);
    float blueMix = (float)((a.getBlue() * (1.0 - reflection - transparency) + b.getBlue() * transparency + c.getBlue() * reflection) / 255.0);
    return new Color(redMix, greenMix, blueMix);
  }
  
  public static Color scale (Color a, double d) {
    return new Color((float)(a.getRed() * d / 255.0), (float)(a.getGreen() * d / 255.0), (float)(a.getBlue() * d / 255.0));
  }
  
  public static double [] cross (double [] slopes1, double [] slopes2) {
     return new double [] {slopes2[1]*slopes1[2]-slopes2[2]*slopes1[1], slopes2[2]*slopes1[0]-slopes2[0]*slopes1[2], slopes2[1]*slopes1[0]-slopes2[0]*slopes1[1]};
  }
  
  public boolean intersect (Edge edge) {
         double [] vec1 = cross(minus(edge.getPoint(), point), edge.getSlope());
         double [] vec2 = cross(slopes, edge.getSlope());
         double [] unVec1 = unit(vec1);
         double [] unVec2 = unit(vec2);
         if (magnitude(minus(unVec1, unVec2))<EPSILON || magnitude(plus(unVec1, unVec2)) <EPSILON) {
//             (Math.abs(unVec1[0]+unVec2[0]) < EPSILON && Math.abs(unVec1[1]+unVec2[1]) < EPSILON && Math.abs(unVec1[2]+unVec2[2])<EPSILON)) {
            
            double magVec2 = magnitude(vec2);
            double magVec1 = magnitude(vec1);
            double len = magVec1/magVec2;
            if (Math.abs(len)<EPSILON) {
               return false;
            }
            if (magnitude(plus(unVec1, unVec2)) <EPSILON) {
               len *= -1;
            }
            double edgeLen;
            if (Math.abs(edge.getSlope()[0]) < EPSILON) {
               if (Math.abs(edge.getSlope()[1]) < EPSILON) {
                  edgeLen = (point[2]-edge.getPoint()[2]+len*slopes[2])/edge.getSlope()[2];
               }
               else {
                  edgeLen = (point[1]-edge.getPoint()[1]+len*slopes[1])/edge.getSlope()[1];
               }
            }
            else {
               edgeLen = (point[0]-edge.getPoint()[0]+len*slopes[0])/edge.getSlope()[0];
            }
           /* if (Math.abs(unVec1[0]+unVec2[0])<EPSILON && Math.abs(unVec1[1]+unVec2[1])<EPSILON && Math.abs(unVec1[2]+unVec2[2])<EPSILON) {
               return false;
            }*/
            if (edgeLen > EPSILON && edgeLen <= (edge.getLength()-EPSILON) && len>EPSILON) {
               return true;
            }
         }
         return false;
         
      }
    
    public double fastAcos (double d) {
      return 1.57079632-d-d*d*d*.27777777; // pi/2 - x - (x^3)/3.6 == acos(x), with error.
    }
  
 /* public boolean intersect (Edge e) {
    double[] point1 = e.getPoint();
    double[] slopes1 = e.getSlope();
    //intersection point's distance along vector 1
    double n1 = (slopes1[1] * (point[0] - point1[0]) - point[1] * slopes1[0] + point1[1] * slopes1[0]) / (slopes[1] * slopes1[0] - slopes[0] * slopes1[1]);
    //intersection point's distance along vector 2
    double n2 = (point[0] - point1[0] + n1 * slopes[0]) / slopes1[0];

    return (point[2] + n1 * slopes[2]) - (point1[2] + n2 * slopes1[2]) < EPSILON && Math.abs(n2)<=e.getLength() && n2>=0 && n1>=0;
  }*/
}