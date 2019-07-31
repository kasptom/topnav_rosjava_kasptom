package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class GuidelineParam {
   private final String name;
   private final String value;
   private final String type;

   public static String EMPTY_PARAM_VALUE = "N/A";

   /**
    * @param name available values {@link com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ApproachMarker#PARAM_NAMES}
    * @param value {@link com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy}
    * @param type e.g. String, Long, ...
    */
   public GuidelineParam(String name, String value, String type) {
      this.name = name;
      this.value = value;
      this.type = type;
   }

   public static GuidelineParam getEmptyParam() {
      return new GuidelineParam("N/A", EMPTY_PARAM_VALUE, "emptyParam");
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
