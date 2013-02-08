using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    public struct NextUpdate
    {
        float gametime;
        public float Gametime
        {
            get { return gametime; }
            set { gametime = value; }
        }
        List<Event> events;

        public List<Event> Events
        {
            get { return events; }
            set { events = value; }
        }

        public NextUpdate(float gametime, List<Event> events)
        {
            this.gametime = gametime;
            this.events = events;
        }
    }
    class ReplayHandler
    {
        SortedList<float, List<Event>> totalTimeRecordEvents = new SortedList<float, List<Event>>();
        SortedList<float, List<Event>> totalTimePlaybackEvents = new SortedList<float, List<Event>>();

        public ReplayHandler()
        {

        }

        public void Clear()
        {
            totalTimeRecordEvents.Clear();
            totalTimePlaybackEvents.Clear();
            lastTime = 0;
        }
        float lastTime = 0;
        public NextUpdate GetNextUpdate()
        {
            if (totalTimePlaybackEvents.Count > 0)
            {
                NextUpdate next = new NextUpdate(totalTimePlaybackEvents.Keys[0] - lastTime, totalTimePlaybackEvents[totalTimePlaybackEvents.Keys[0]]);
                lastTime = totalTimePlaybackEvents.Keys[0];
                totalTimePlaybackEvents.Remove(totalTimePlaybackEvents.Keys[0]);
                return next;
            }
            else
            {
                return new NextUpdate(0.0f, new List<Event>());
            }
        }

        public void Update(float totalTime, List<Event> events)
        {
            List<Event> eventCopy = new List<Event>();
            foreach (Event e in events)
                eventCopy.Add(e);

            totalTimeRecordEvents.Add(totalTime, eventCopy);
        }

        public void LoadReplay(string fileName)
        {
            string[] fileLines = File.ReadAllLines(fileName);
            float currentTime = 0;
            foreach (string s in fileLines)
            {
                string[] split = s.Split(':');
                switch (split[0])
                {
                    case "t":
                        currentTime = float.Parse(split[1]);
                        totalTimePlaybackEvents.Add(currentTime, new List<Event>());
                        break;
                    case "e":
                        switch (split[1])
                        {
                            case "build":
                                totalTimePlaybackEvents[currentTime].Add(new Event(EventType.build, new Vector2(float.Parse(split[3]), float.Parse(split[4])), split[2]));
                                break;
                            case "sell":
                                totalTimePlaybackEvents[currentTime].Add(new Event(EventType.sell, new Vector2(float.Parse(split[3]), float.Parse(split[4])), split[2]));
                                break;
                            case "upgrade":
                                totalTimePlaybackEvents[currentTime].Add(new Event(EventType.upgrade, new Vector2(float.Parse(split[3]), float.Parse(split[4])), split[2]));
                                break;
                        }
                        break;
                }
            }
        }

        public void SaveReplay()
        {
            string fileName = DateTime.Now.ToString().Replace(" ", String.Empty).Replace(":", String.Empty).Replace(".",String.Empty)+ ".txt";
            List<string> fileContent = new List<string>();

            foreach (KeyValuePair<float, List<Event>> gametime in totalTimeRecordEvents)
            {
                fileContent.Add("t:"+gametime.Key.ToString());
                foreach (Event e in gametime.Value)
                {
                    fileContent.Add(e.SaveString());
                }
            }
            File.WriteAllLines(".\\Replay" + fileName, fileContent);
        }
    }

}
