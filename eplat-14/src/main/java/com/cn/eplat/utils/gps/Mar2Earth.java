package com.cn.eplat.utils.gps;

/*
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

       if ((System.Math.Abs(dLat) < threshold) && (System.Math.Abs(dLon) < threshold))
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
