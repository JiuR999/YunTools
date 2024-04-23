package cn.swust.jiur.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.swust.jiur.entity.Formation;

@Dao
public interface FormDao {
    @Query("select * from Formation where level=(:level)")
    List<Formation> selectFormByLevel(int level);
    @Insert
    void addFormation(Formation formation);
}
