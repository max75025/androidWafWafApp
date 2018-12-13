package com.wafwaf.wafwaf.WafLibraryPackege;

import java.util.List;

public class Event{
   public int DataTime;
   public List<String> TypeTrace;
   public List<String> ResultTypes;
   public String       IpAddr;
   public String       Country;



   public Event(){}


   public Event( int DataTime, List<String> TypeTrace, List<String> ResultTypes, String  IpAddr, String  Country){
      this.DataTime = DataTime;
      this.TypeTrace = TypeTrace;
      this.ResultTypes = ResultTypes;
      this.IpAddr = IpAddr;
      this.Country = Country;
   }

}