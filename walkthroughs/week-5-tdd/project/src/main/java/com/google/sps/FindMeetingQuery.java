// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
package com.google.sps;
 
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
 
public final class FindMeetingQuery {
  
  public final Collection<TimeRange> slots = new ArrayList<>();
  public final List<TimeRange> bookedTimes = new ArrayList<>();
  public final List<TimeRange> bookedOptionalTimes = new ArrayList<>();
 
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
 
    if(request.getDuration() >= TimeRange.WHOLE_DAY.duration()){
        return slots;
    }else if(events.isEmpty()){
        slots.add(TimeRange.WHOLE_DAY);
    }else {
        // Get blocked and available times for required members.
        for(Event event: events){
            for(String attendee: request.getAttendees()){
                if(event.getAttendees().contains(attendee)){
                    bookedTimes.add(event.getWhen());
                    break;
                }
            }
        }
        List<TimeRange> availableTimes = getAvailableSlots(bookedTimes, request.getDuration());
 
        //Get blocked and available times for optional people
        for(Event event: events){
            for(String attendee: request.getOptionalAttendees()){
                if(event.getAttendees().contains(attendee)){
                    bookedOptionalTimes.add(event.getWhen());
                    break;
                }
            }
        }
        List<TimeRange> availableOptionalTimes = getAvailableSlots(bookedOptionalTimes, request.getDuration());

        // If no required attendee is available, then return no time slot available.
        if(availableTimes.size() == 0){
            return slots;
        }
 
        // Compare the timeslots available for required attendees and optional attendees and return the intersection.
        for(int i = 0; i < availableTimes.size(); i++){
            for(int j = 0; j < availableOptionalTimes.size(); j++){
                if(availableTimes.get(i).end() < availableOptionalTimes.get(j).start()){
                    break;
                }
                else if(availableTimes.get(i).equals(availableOptionalTimes.get(j))){
                    slots.add(TimeRange.fromStartEnd(availableOptionalTimes.get(i).start(), availableOptionalTimes.get(i).end(),false)); 
                }else if(availableTimes.get(i).contains(availableOptionalTimes.get(j))){
                    slots.add(TimeRange.fromStartEnd(availableOptionalTimes.get(j).start(), availableOptionalTimes.get(j).end(),false));
                }else if(availableOptionalTimes.get(j).contains(availableTimes.get(i))){
                    slots.add(TimeRange.fromStartEnd(availableTimes.get(i).start(), availableTimes.get(i).end(),false));
                }
            }
        }

        // If no available overlap is available, return the available times for required attendees.
        if(slots.size() == 0 && availableOptionalTimes.size() != 0){
            return availableTimes;
        }

    }
 
    return slots;
  }
 
  public List<TimeRange> getAvailableSlots(List<TimeRange> timesArray, long duration){
    
    List<TimeRange> returnSlots = new ArrayList<>();
 
    if(timesArray.size() == 0){
        returnSlots.add(TimeRange.WHOLE_DAY);
        return returnSlots;
    }
 
    //Sort the booked times by start of day
    Collections.sort(timesArray, TimeRange.ORDER_BY_START);
 
    //Merge overlapping or nested timeslots into a single chunk of time
    int i = 0;
    while(i < timesArray.size()-1){
        if(timesArray.get(i).overlaps(timesArray.get(i+1)) || timesArray.get(i).contains(timesArray.get(i+1))){
            int endTime = Math.max(timesArray.get(i).end(), timesArray.get(i+1).end());
            timesArray.set(i, TimeRange.fromStartEnd(timesArray.get(i).start(), endTime, false));
            timesArray.remove(i+1); 
        }else{
            i++;
        }
    }
 
    //Get everthing in between the booked time slots as eligible slots for the request.
    if((timesArray.get(0).start() - TimeRange.START_OF_DAY) >= duration){
        returnSlots.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, timesArray.get(0).start(), false));
    }
 
    for(i = 0; i < timesArray.size() - 1; i++){
        if((timesArray.get(i+1).start() - timesArray.get(i).end()) >= duration){
            returnSlots.add(TimeRange.fromStartEnd(timesArray.get(i).end(), timesArray.get(i+1).start(), false));
        }
    }
 
    if((TimeRange.END_OF_DAY - timesArray.get(timesArray.size() - 1).end()) >= duration){
        returnSlots.add(TimeRange.fromStartEnd(timesArray.get(timesArray.size() - 1).end(), TimeRange.END_OF_DAY, true));
    }
 
    return returnSlots;
  }
  
}
