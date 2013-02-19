using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
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
            if (File.Exists(path + "Towers.xml"))
            {
                using (FileStream fs = new FileStream(path + "Towers.xml", FileMode.Open))
                {
                    Dictionary<string, TowerStats> towers = IntermediateSerializer.Deserialize<Dictionary<string, TowerStats>>(XmlReader.Create(fs), null);
                    fs.Close();
                    return towers;
                }
            }
            else
            {
                using (FileStream fs = new FileStream(".\\Content\\defaultTowers.xml", FileMode.Open))
                {
                    Dictionary<string, TowerStats> towers = IntermediateSerializer.Deserialize<Dictionary<string, TowerStats>>(XmlReader.Create(fs), null);
                    fs.Close();
                    return towers;
                }
            }
        }
        
        public Dictionary<string, EnemyInfo> ReadMonsterParameters(ContentManager contentManager, string path)
        {
            if (File.Exists(path + "Monsters.xml"))
            {
                using (FileStream fs = new FileStream(path + "Monsters.xml", FileMode.Open))
                {
                    Dictionary<string, EnemyInfo> monsters = IntermediateSerializer.Deserialize<Dictionary<string, EnemyInfo>>(XmlReader.Create(fs), null);
                    fs.Close();
                    return monsters;
                }
            }
            else
            {
                using (FileStream fs = new FileStream(".\\Content\\defaultMonsters.xml", FileMode.Open))
                {
                    Dictionary<string, EnemyInfo> monsters = IntermediateSerializer.Deserialize<Dictionary<string, EnemyInfo>>(XmlReader.Create(fs), null);
                    fs.Close();
                    return monsters;
                }
            }
        
            
        }
    }
}
