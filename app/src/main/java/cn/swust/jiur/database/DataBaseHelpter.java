package cn.swust.jiur.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.swust.jiur.entity.Formation;
import cn.swust.jiur.database.dao.FormDao;

@Database(entities = {Formation.class}, version = 1)
public abstract class DataBaseHelpter extends RoomDatabase {
       public abstract FormDao formDao();

       private static DataBaseHelpter dataBaseHelpter;

       public static DataBaseHelpter getInstance(Context context){
              if(dataBaseHelpter==null){
                 synchronized (DataBaseHelpter.class){
                        dataBaseHelpter = Room.databaseBuilder(context.getApplicationContext()
                        ,DataBaseHelpter.class,"COC.db").build();
                 }
              }
              return dataBaseHelpter;
       }
}
