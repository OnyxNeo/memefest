package com.memefest.Jaxrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.JokeOfDayJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.SponsorJSON;
import com.memefest.Services.JokeOfDayOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@PermitAll
@Path("Joke")
public class JokeOfDayResource extends Resource{
    
    @Inject
    private JokeOfDayOperations jokeOps;


    @OPTIONS
    public Response jokeOfDayOptions(){
        return Response.ok().build();
    }

    @GET
    @Produces("application/json")
    public Response getJokeOfDay() throws JsonProcessingException{
       Set<JokeOfDayJSON> jokes =  jokeOps.getJokeOfDayBetween(LocalDate.now(), LocalDate.now());
       if(jokes.size() == 1)
            return Response.ok().entity(mapper.writeValueAsString(jokes.iterator().next())).build();
        else
            return Response.serverError().entity("Too many jokes of the day").build();
    }

    @OPTIONS
    @Path("Schedules")
    public Response jokeOfDayScheduleOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("Schedules/{Punchline}/{Date}/{SponsorName}")
    @Produces("application/json")
    public Response getScheduledJokeOfDay(@QueryParam("Punchline") String jokeOfDay,
                                    @QueryParam("Date") String date, 
                                    @QueryParam("SponsorName") String sponsor) throws JsonProcessingException, ParseException{
        JokeOfDayJSON jokeOfDayJSON = new JokeOfDayJSON(null, jokeOfDay, null, 0, null, null);
        SponsorJSON sponsorJSON = new SponsorJSON(null, sponsor);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
        LocalDate day = LocalDateTime.ofInstant(formatter.parse(date).toInstant(), ZoneId.systemDefault()).toLocalDate();
        jokeOfDayJSON.setUser(sponsorJSON);
        jokeOfDayJSON.setDate(day);
        Set<JokeOfDayJSON> jokes = jokeOps.getScheduledJokeOfDay(jokeOfDayJSON);
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<JokeOfDayJSON> iterator = jokes.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                JokeOfDayJSON comment = iterator.next();
                comment.setComments(jokeOps.getComments(comment));
                String entity = mapper.writeValueAsString(comment);
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @OPTIONS
    @Path("{StartDate}/{EndDate}")
    public Response jokeOfDayBetweenOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("{StartDate}/{EndDate}")
    @Produces("application/json")
    public Response getJokeOfDayBetween(@QueryParam("StartDate") String startDate, @QueryParam("EndDate") String endDate ) throws JsonProcessingException, ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
        LocalDate afterDay = LocalDateTime.ofInstant(formatter.parse(startDate).toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate beforeDay = LocalDateTime.ofInstant(formatter.parse(startDate).toInstant(), ZoneId.systemDefault()).toLocalDate();
        Set<JokeOfDayJSON> jokes = jokeOps.getJokeOfDayBetween(afterDay, beforeDay);
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<JokeOfDayJSON> iterator = jokes.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                JokeOfDayJSON comment = iterator.next();
                comment.setComments(jokeOps.getComments(comment));
                String entity = mapper.writeValueAsString(comment);
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();    
    }


    @OPTIONS
    @Path("{JokeId}/Comments")
    public Response jokeOfDayCommentsOptions(){
        return Response.ok().build();
    }


    @GET
    @Path("{JokeId}/Comments")
    @Produces("application/json")
    public Response getComments(@QueryParam("JokeId") int jokeId){
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<PostJSON> iterator = jokeOps.getComments(new JokeOfDayJSON(Long.valueOf(jokeId), null, null,
                                         0, null, null)).stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON comment = iterator.next();
                String entity = mapper.writeValueAsString(comment);
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @POST
    @Consumes("application/json")
    public Response scheduleJokeOfDay(String jokeOfDay) throws JsonProcessingException{
        JokeOfDayJSON jokeOfDayJSON = mapper.readValue(jokeOfDay, JokeOfDayJSON.class);
        jokeOps.scheduleJokeOfDay(jokeOfDayJSON);
        return Response.ok().build();
    }

    @OPTIONS
    @Path("{JokeId}")
    public Response jokeOfDayEditOptions(){
        return Response.ok().build();
    }

    @PUT
    @Path("{JokeId}") 
    @Consumes("appliaction/json")
    public Response editJokeOfDay(@QueryParam("JokeId") int jokeId, String jokeOfDay) throws JsonProcessingException{
        JokeOfDayJSON jokeOfDayJSON = mapper.readValue(jokeOfDay, JokeOfDayJSON.class);
        jokeOfDayJSON.setJokeId(Long.valueOf(jokeId));
        jokeOps.editJokeOfDay(jokeOfDayJSON);
        return Response.ok().build();
    }

    @OPTIONS
    @Path("Schedules/Cancel")
    public Response jokeOfDayCancelOptions(){
        return Response.ok().build();
    }

    @PUT
    @Path("Schedules/Cancel")
    @Consumes("application/json")
    public Response cancelScheduledJokeOfDay(String jokeOfDay) throws JsonProcessingException{
        JokeOfDayJSON jokeOfDayJSON = mapper.readValue(jokeOfDay, JokeOfDayJSON.class);
        jokeOps.cancelScheduledJokeOfDay(jokeOfDayJSON);
        return Response.ok().build();
    }
}