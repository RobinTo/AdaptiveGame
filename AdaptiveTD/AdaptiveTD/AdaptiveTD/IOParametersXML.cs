using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Content.Pipeline.Serialization.Intermediate;

namespace AdaptiveTD
{
    class IOParametersXML
    {
        public bool SaveParameters(Dictionary<string, TowerStats> towerInfo, Dictionary<string, EnemyInfo> enemyInfo, string path)
        {
            XmlWriterSettings xmlWriterSettings = new XmlWriterSettings();
            xmlWriterSettings.Indent = true;
            
            using (XmlWriter xmlWriter = XmlWriter.Create(path + "Towers.xml", xmlWriterSettings))
            {
                IntermediateSerializer.Serialize(xmlWriter, towerInfo, null);
            }

            using (XmlWriter xmlWriter = XmlWriter.Create(path + "Monsters.xml", xmlWriterSettings))
            {
                IntermediateSerializer.Serialize(xmlWriter, enemyInfo, null);
            }

            return true;
        }
        public Dictionary<string, TowerStats> ReadTowerParameters(ContentManager contentManager, string path)
        {
            return contentManager.Load<Dictionary<string, TowerStats>>(path + "Towers.xml");
        }
        
        public Dictionary<string, TowerStats> ReadMonsterParameters(ContentManager contentManager, string path)
        {
            return contentManager.Load<Dictionary<string, TowerStats>>(path + "Monsters.xml");
        }
    }
}
