
package com.example.baby_routine;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM event")
    List<Event> getAll();

    @Query("SELECT * FROM event WHERE id IN (:eventIds)")
    List<Event> loadAllByIds(int[] eventIds);

    @Query("SELECT * FROM event WHERE id = :id")
    Event findById(int id);

    @Query("SELECT * FROM event ORDER BY id DESC LIMIT 1")
    Event findLastEvent();

    @Query("SELECT * FROM event WHERE `action` LIKE :action LIMIT 1")
    Event findByAction(String action);

    @Query("SELECT * FROM event WHERE `action` LIKE :action ")
    List<Event> getAllByAction(String action);

    @Query("SELECT * FROM event WHERE date LIKE :date ")
    List<Event> getAllforDate(String date);

    @Query("SELECT * FROM event WHERE `action` LIKE :action AND date LIKE :date ")
    List<Event> getAllByActionDate(String action, String date);

    @Query("SELECT hour FROM event WHERE `action` LIKE :action AND date LIKE :date ")
    List<String> getHours(String action, String date);

    @Query("SELECT COUNT(*) FROM event WHERE `action` LIKE :action AND date LIKE :date ")
    int countEvent(String action, String date);

    @Query("UPDATE event SET hour=:hour WHERE id = :id")
    void updateHour(String hour, long id);

    @Query("SELECT id FROM event WHERE date = :date AND hour = :hour")
    long getId(String date, String hour);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Event event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> events);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Query("DELETE FROM event WHERE date LIKE :date AND hour like :hour")
    void delete(String date, String hour);

    @Query("UPDATE event SET hour = :hour WHERE date LIKE :date AND id like :id")
    void update(String date, String hour, long id);

    @Query("DELETE FROM event")
    void deleteAll();
}