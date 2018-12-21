import java.awt.Color;

class RGB {
    float red,green,blue;
    Color color;

    RGB(float r, float g, float b)
    { if (r>1) r=1; else if (r<0) r=0;
        if (g>1) g=1; else if (g<0) g=0;
        if (b>1) b=1; else if (b<0) b=0;
        red=r; green=g; blue=b;
    }

    RGB() {

    }



    Color color()
    { if (color!=null) return color;
        color=new Color((int) (red*255),(int) (green*255), (int) (blue*255));
        return color;
    }

    RGB rayTrace(Ray ray, int rec) {
        if (rec>SDRaytracer.maxRec) return SDRaytracer.black;
        IPoint ip = (IPoint) IPoint.hitObject(ray);  // (ray, p, n, triangle);
        if (ip.dist>IPoint.epsilon)
          return lighting(ray, ip, rec);
        else
          return SDRaytracer.black;
     }
    
    RGB addColors(RGB c1, RGB c2, float ratio)
    { return new RGB( (c1.red+c2.red*ratio),
              (c1.green+c2.green*ratio),
              (c1.blue+c2.blue*ratio));
     }
     
   RGB lighting(Ray ray, IPoint ip, int rec) {
     Vec3D point=ip.ipoint;
     Triangle triangle=ip.triangle;
     RGB color = addColors(triangle.color,SDRaytracer.getAmbientColor(),1);
     Ray shadow_ray=new Ray();
      for(Light light : SDRaytracer.getLights())
          { shadow_ray.start=point;
            shadow_ray.dir=light.position.minus(point).mult(-1);
            shadow_ray.dir.normalize();
            IPoint ip2=IPoint.hitObject(shadow_ray);
            if(ip2.dist<IPoint.epsilon)
            {
              float ratio=Math.max(0,shadow_ray.dir.dot(triangle.normal));
              color = addColors(color,light.color,ratio);
            }
          }
        Ray reflection=new Ray();
        //R = 2N(N*L)-L)    L ausgehender Vektor
        Vec3D L=ray.dir.mult(-1);
        reflection.start=point;
        reflection.dir=triangle.normal.mult(2*triangle.normal.dot(L)).minus(L);
        reflection.dir.normalize();
        RGB rcolor=rayTrace(reflection, rec+1);
        float ratio =  (float) Math.pow(Math.max(0,reflection.dir.dot(L)), triangle.shininess);
        color = addColors(color,rcolor,ratio);
        return(color);
     }

}