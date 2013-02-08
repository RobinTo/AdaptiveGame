using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;
using System.Reflection;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    public enum EventType
    {
        [Description("build")]
        build,
        [Description("sell")]
        sell,
        [Description("upgrade")]
        upgrade
    }
    public class Event
    {
        EventType type;
        public EventType Type
        {
            get { return type; }
        }
        Vector2 tilePosition;
        public Vector2 TilePosition
        {
            get { return tilePosition; }
        }
        string towerType;

        public string TowerType
        {
            get { return towerType; }
        }

        public Event(EventType type, Vector2 tilePosition, string towerType)
        {
            this.type = type;
            this.tilePosition = tilePosition;
            this.towerType = towerType;
        }

        public string SaveString()
        {
            return "e:"+GetDescription(type) + ":" + towerType + ":" + tilePosition.X.ToString() + ":" + tilePosition.Y.ToString();
        }

        public static string GetDescription(object enumValue)
        {
            string defDesc = "";
            FieldInfo fi = enumValue.GetType().GetField(enumValue.ToString());

            if (null != fi)
            {
                object[] attrs = fi.GetCustomAttributes(typeof(DescriptionAttribute), true);
                if (attrs != null && attrs.Length > 0)
                    return ((DescriptionAttribute)attrs[0]).Description;
            }

            return defDesc;
        }
    }
}
