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
        List<TimeRange> mandatoryTimes = getAvailableSlots(bookedTimes, request.getDuration());
 
        // Get blocked and available times for optional people
        for(Event event: events){
            for(String attendee: request.getOptionalAttendees()){
                if(event.getAttendees().contains(attendee)){
                    bookedOptionalTimes.add(event.getWhen());
                    break;
                }
            }
        }
        List<TimeRange> optionalTimes = getAvailableSlots(bookedOptionalTimes, request.getDuration());

        // If no required attendee is available, then return no time slot available.
        if (optionalTimes.size() == 0){
            if(mandatoryTimes.get(0) == TimeRange.WHOLE_DAY){
                return slots;
            }else{
                return mandatoryTimes;
            }
        }

        int i = 0;
        int j = 0;

        while((i < mandatoryTimes.size()) && (j < optionalTimes.size())){
            if(mandatoryTimes.get(i).overlaps(optionalTimes.get(j))){

                if(mandatoryTimes.get(i).contains(optionalTimes.get(j))){
                    slots.add(TimeRange.fromStartEnd(optionalTimes.get(j).start(), optionalTimes.get(j).end(),false));
                    j++;
                }else if(optionalTimes.get(j).contains(mandatoryTimes.get(i))){
                    slots.add(TimeRange.fromStartEnd(mandatoryTimes.get(i).start(), mandatoryTimes.get(i).end(),false));
                    i++;
                }else if(mandatoryTimes.get(i).end() < optionalTimes.get(j).end()){
                    i++;
                }else{
                    j++;
                }

            }else{
                if(mandatoryTimes.get(i).end() < optionalTimes.get(j).end()){
                    i++;
                }else{
                    j++;
                }
            }
        }
 
        // If no available overlap is available, return the available times for required attendees.
        if(slots.size() == 0 && optionalTimes.size() != 0){
            return mandatoryTimes;
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
 
    // Sort the booked times by start of day
    Collections.sort(timesArray, TimeRange.ORDER_BY_START);
 
    // Merge overlapping or nested timeslots into a single chunk of time
    int i = 0;
    while(i < timesArray.size()-1){
        if(timesArray.get(i).overlaps(timesArray.get(i+1))){
            int endTime = Math.max(timesArray.get(i).end(), timesArray.get(i+1).end());
            timesArray.set(i, TimeRange.fromStartEnd(timesArray.get(i).start(), endTime, false));
            timesArray.remove(i+1); 
        }else{
            i++;
        }
    }
 
    // Get everthing in between the booked time slots as eligible slots for the request.
    returnSlots.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, timesArray.get(0).start(), false));
    for(i = 0; i < timesArray.size() - 1; i++){
        returnSlots.add(TimeRange.fromStartEnd(timesArray.get(i).end(), timesArray.get(i+1).start(), false));     
    }
    returnSlots.add(TimeRange.fromStartEnd(timesArray.get(timesArray.size() - 1).end(), TimeRange.END_OF_DAY, true));
    
    // Filter to remove timeslots that are less than duration requested
    i = 0;
    while(i < returnSlots.size()){
        if(returnSlots.get(i).duration() < duration){
            returnSlots.remove(i);
        }else{
            i++;
        }
    }
 
    return returnSlots;
  }
  
}
