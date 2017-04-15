package com.cn.eplat.utils.gps;

public class MarsEarthTransform {
	
	double pi = 3.14159265358979324;
	
	boolean outOfChina(double lat, double lon){
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
	
	double transformLat(double x, double y)
    {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y /3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    double transformLon(double x, double y)
    {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }  


/** 
 * 地球坐标转换为火星坐标 
 * World Geodetic System ==> Mars Geodetic System 
 * 
 * @param wgLat  地球坐标 
 * @param wgLon 
 * 
 * mglat,mglon 火星坐标 
 */
 /*   
void transform2Mars(double wgLat, double wgLon, double mgLat, double mgLon)  
{
	PointLatLng pll = new PointLatLng();
	
    if (outOfChina(wgLat, wgLon) )  
    {  
    	pll.Lat  = wgLat;  
    	pll.Lng = wgLon;  
        return ;  
    }  
    double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);  
    double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);  
    double radLat = wgLat / 180.0 * pi;  
    double magic = Math.sin(radLat);  
    magic = 1 - ee * magic * magic;  
    double sqrtMagic = Math.sqrt(magic);  
    dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);  
    dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);  
    pll.Lat = wgLat + dLat;  
    pll.Lng = wgLon + dLon;  
}
	
	public PointLatLng Mar2Earth(PointLatLng Mar) {
	    PointLatLng Earth = new PointLatLng();
	    if (outOfChina(Mar.Lat, Mar.Lng))
	    {
	       return Mar;
	    }
	   PointLatLng tmp;
	   double initDelta = 0.1;
	   double threshold = 0.000001;
	   double dLat = initDelta, dLon = initDelta;
	   double mLat = Mar.Lat - dLat, mLon = Mar.Lng - dLon;
	   double pLat = Mar.Lat + dLat, pLon = Mar.Lng + dLon;
	   double wgsLat, wgsLon, i = 0;
	   double lastDlat = initDelta, lastDlng = initDelta;
	   PointLatLng lasttmp = new PointLatLng((mLat + pLat) / 2, (mLon + pLon) / 2);
	   PointLatLng minitemp=new PointLatLng();

	   while (true) {
	       wgsLat = (mLat + pLat) / 2;
	       wgsLon = (mLon + pLon) / 2;
	       transform2Mars(wgsLat,wgsLon,tmp.Lat.tmp.lng);

	       dLat = tmp.Lat - Mar.Lat;
	       dLon = tmp.Lng - Mar.Lng;

	       if ((Math.Abs(dLat) < threshold) && (Math.Abs(dLon) < threshold))
	           break;

	       if (dLat + dLon >= lastDlat + lastDlng)
	       {
	       }
	       else {
	           lastDlat = dLat;
	           lastDlng = dLon;

	           minitemp.Lat = tmp.Lat;
	           minitemp.Lng = tmp.Lng;
	       }

	       if (lasttmp.Lat == tmp.Lat && lasttmp.Lng == tmp.Lng) {
	           wgsLat = minitemp.Lat;
	           wgsLon = minitemp.Lng;

	           break;
	       }
	       lasttmp = tmp;

	       if (dLat > 0) pLat = wgsLat; else mLat = wgsLat;
	       if (dLon > 0) pLon = wgsLon; else mLon = wgsLon;


	      if (++i > 10000) break;
	   }

	   if(i>=10000){
	       return new PointLatLng();
	   }
	   Earth.Lat = wgsLat;
	   Earth.Lng = wgsLon;
	   return Earth;
	}
*/
}
