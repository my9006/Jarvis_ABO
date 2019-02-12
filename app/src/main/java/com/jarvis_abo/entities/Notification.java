package com.jarvis_abo.entities;

import com.jarvis_abo.entities.DaoSession;
import de.greenrobot.dao.DaoException;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jarvis_abo.JA_application;
import com.jarvis_abo.model.Type;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "mbl_notification".
 */
public class Notification {

    private Long pk_id;
    private double lat;
    private double lng;
    /** Not-null value. */
    private String type;
    /** Not-null value. */
    private String message;
    private java.util.Date inactiveUntil;
    private long fk_user;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient NotificationDao myDao;

    private User user;
    private Long user__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Notification() {
    }

    public Notification(Long pk_id) {
        this.pk_id = pk_id;
    }

    public Notification(Long pk_id, double lat, double lng, String type, String message, java.util.Date inactiveUntil, long fk_user) {
        this.pk_id = pk_id;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.message = message;
        this.inactiveUntil = inactiveUntil;
        this.fk_user = fk_user;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNotificationDao() : null;
    }

    public Long getPk_id() {
        return pk_id;
    }

    public void setPk_id(Long pk_id) {
        this.pk_id = pk_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    /** Not-null value. */
    public String getType() {
        return type;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setType(String type) {
        this.type = type;
    }

    /** Not-null value. */
    public String getMessage() {
        return message;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMessage(String message) {
        this.message = message;
    }

    public java.util.Date getInactiveUntil() {
        return inactiveUntil;
    }

    public void setInactiveUntil(java.util.Date inactiveUntil) {
        this.inactiveUntil = inactiveUntil;
    }

    public long getFk_user() {
        return fk_user;
    }

    public void setFk_user(long fk_user) {
        this.fk_user = fk_user;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.fk_user;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new DaoException("To-one property 'fk_user' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            fk_user = user.getPk_id();
            user__resolvedKey = fk_user;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    /*
    Translates Google data into our DB acceptable form
     */

    public Notification(LatLng latLng, String title, String type) {

        this.lat = latLng.latitude;
        this.lng = latLng.longitude;

        this.message = title;

        this.type = type;

        this.fk_user = JA_application.getInstance().loggedUser().getPk_id();
    }

    public MarkerOptions reverseEngineer() {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(this.message);
        markerOptions.position(new LatLng(this.lat, this.lng));
        markerOptions.icon(Type.getCustomMarker(this.type));

        return markerOptions;


    }


    // KEEP METHODS END

}