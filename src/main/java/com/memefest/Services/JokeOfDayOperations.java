package com.memefest.Services;

import java.time.LocalDate;
import java.util.Set;

import com.memefest.DataAccess.JSON.JokeOfDayJSON;
import com.memefest.DataAccess.JSON.PostJSON;

public interface JokeOfDayOperations {

    public void scheduleJokeOfDay(JokeOfDayJSON jokeOfDay);

    public void cancelScheduledJokeOfDay(JokeOfDayJSON jokeOfDay);

    public Set<JokeOfDayJSON> getScheduledJokeOfDay(JokeOfDayJSON jokeOfDay);

    public void editJokeOfDay(JokeOfDayJSON jokeOfDay);

    public void jokeOfDayComment(JokeOfDayJSON jokeOfDay);

    public Set<JokeOfDayJSON> getJokeOfDayBetween(LocalDate startDate, LocalDate endDate);

    public JokeOfDayJSON getJokeOfDayInfo(JokeOfDayJSON jokeOfDay);

    public Set<PostJSON> getComments(JokeOfDayJSON jokeOfDay);
}