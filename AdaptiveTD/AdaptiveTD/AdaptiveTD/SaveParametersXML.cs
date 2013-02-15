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
    class SaveParametersXML
    {
        public bool SaveTowerParameters(Dictionary<string, TowerStats> towerInfo, Dictionary<string, EnemyInfo> enemyInfo, string Path)
        {
            XmlWriterSettings xmlWriterSettings = new XmlWriterSettings();
            xmlWriterSettings.Indent = true;
            xmlWriterSettings.ConformanceLevel = ConformanceLevel.Auto;
            //foreach (KeyValuePair<string, TowerStats> stringTowerStatPair in towerInfo)
            //{
            using (XmlWriter xmlWriter = XmlWriter.Create(Path + "Towers.xml", xmlWriterSettings))
            {

                IntermediateSerializer.Serialize(xmlWriter, towerInfo, null);
            }
            //}

            return true;
        }/*
        public Dictionary<string, TowerStats> ReadParameters()
        {
         //   TowerStats = Content.Load<MonsterParameters>("monsterParameters");
        }*/
    }
}
