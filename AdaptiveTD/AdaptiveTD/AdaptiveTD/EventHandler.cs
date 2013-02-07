using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AdaptiveTD
{
    class EventHandler
    {

        List<Event> events = new List<Event>();

        List<Event> queuedEvents = new List<Event>();

        public List<Event> Events
        {
            get
            {
                return events;
            }
        }

        public void NewRound()
        {
            events.Clear();
            for (int i = 0; i < queuedEvents.Count; i++)
                events.Add(queuedEvents[i]);
            queuedEvents.Clear();
        }

        public void QueueEvent(Event e)
        {
            queuedEvents.Add(e);
        }
    }
}
