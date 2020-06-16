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

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    if(request.getDuration() >= TimeRange.WHOLE_DAY.duration()){
        return slots;
    }else if(events.isEmpty()){
        slots.add(TimeRange.WHOLE_DAY);
    }else {
        for(Event event: events){
            for(String attendee: request.getAttendees()){
                if(event.getAttendees().contains(attendee)){
                    bookedTimes.add(event.getWhen());
                    break;
                }
            }
        }

        if(bookedTimes.size() == 0){
            slots.add(TimeRange.WHOLE_DAY);
            return slots;
        }

        //Sort the booked times by start of day
        Collections.sort(bookedTimes, TimeRange.ORDER_BY_START);

        //Merge overlapping or nested timeslots into a single chunk of time
        for (int i = 0; i < bookedTimes.size() - 1; i++){
            if(bookedTimes.get(i).overlaps(bookedTimes.get(i+1)) || bookedTimes.get(i).contains(bookedTimes.get(i+1))){
                if(bookedTimes.get(i).end() > bookedTimes.get(i+1).end()){
                    bookedTimes.set(i, TimeRange.fromStartEnd(bookedTimes.get(i).start(), bookedTimes.get(i).end(), false));
                }else{
                    bookedTimes.set(i, TimeRange.fromStartEnd(bookedTimes.get(i).start(), bookedTimes.get(i+1).end(), false));
                }
                bookedTimes.remove(i+1);
            }
        }

        //Get everthing in between the booked time slots as eligible slots for the request
        long duration = request.getDuration();
        
        if((bookedTimes.get(0).start() - TimeRange.START_OF_DAY) >= duration){
            slots.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, bookedTimes.get(0).start(), false));
        }

        for(int i = 0; i < bookedTimes.size() - 1; i++){
            if((bookedTimes.get(i+1).start() - bookedTimes.get(i).end()) >= duration){
                slots.add(TimeRange.fromStartEnd(bookedTimes.get(i).end(), bookedTimes.get(i+1).start(), false));
            }
        }
        
        if((TimeRange.END_OF_DAY - bookedTimes.get(bookedTimes.size() - 1).end()) >= duration){
            slots.add(TimeRange.fromStartEnd(bookedTimes.get(bookedTimes.size() - 1).end(), TimeRange.END_OF_DAY, true));
        }
    }

    return slots;
  }
  
}
