package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class GuidelineParam {
   private final String name;
   private final String value;
   private final String type;

   public GuidelineParam(String name, String value, String type) {
      this.name = name;
      this.value = value;
      this.type = type;
   }

   public static GuidelineParam getEmptyParam() {
      return new GuidelineParam("N/A", "N/A", "emptyParam");
   }

   public String getName() {
      return name;
   }

   public String getValue() {
      return value;
   }

   public String getType() {
      return type;
   }

   @Override
   public String toString() {
      return String.format("{ name: %s, value: %s, type: %s }", name, value, type);
   }
}
