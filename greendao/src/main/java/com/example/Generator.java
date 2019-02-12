package com.example;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        Schema schema = new Schema(1, "com.jarvis_abo.entities");
        schema.enableKeepSectionsByDefault();

        Entity userEntity = schema.addEntity("User");
        userEntity.setTableName("mbl_user");
        userEntity.addLongProperty("pk_id").primaryKey().autoincrement();
        userEntity.addStringProperty("username").notNull().unique();
        userEntity.addStringProperty("password").notNull();


        Entity notificationEntity = schema.addEntity("Notification");
        notificationEntity.setTableName("mbl_notification");
        notificationEntity.addLongProperty("pk_id").primaryKey().autoincrement();
        notificationEntity.addDoubleProperty("lat").notNull();
        notificationEntity.addDoubleProperty("lng").notNull();
        notificationEntity.addStringProperty("type").notNull();
        notificationEntity.addStringProperty("message").notNull();
        notificationEntity.addDateProperty("inactiveUntil");


        notificationEntity.addImport("com.google.android.gms.maps.model.LatLng");
        notificationEntity.addImport("com.google.android.gms.maps.model.MarkerOptions");
        notificationEntity.addImport("com.jarvis_abo.JA_application");
        notificationEntity.addImport("com.jarvis_abo.model.Type");


        Property userProperty = notificationEntity.addLongProperty("fk_user").notNull().getProperty();

        notificationEntity.addToOne(userEntity, userProperty, "user");


        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
