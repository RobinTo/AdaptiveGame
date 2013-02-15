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
        public bool SaveParameters(Dictionary<string, TowerStats> towerInfo, Dictionary<string, EnemyInfo> enemyInfo)
        {
            XmlWriterSettings xmlWriterSettings = new XmlWriterSettings();
            xmlWriterSettings.Indent = true;
            xmlWriterSettings.ConformanceLevel = ConformanceLevel.Auto;
            //foreach (KeyValuePair<string, TowerStats> stringTowerStatPair in towerInfo)
            //{
            using (XmlWriter xmlWriter = XmlWriter.Create("example.xml", xmlWriterSettings))
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
