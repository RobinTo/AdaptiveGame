using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace AdaptiveTD
{
    class WaveHandler
    {
        public Dictionary<float, string> LoadWave(string Path)
        {
            Dictionary<float, string> enemyWave = new Dictionary<float, string>();
            string[] fileContent = File.ReadAllLines(Path);
            foreach (string s in fileContent)
            {
                string[] split = s.Split(':');
                enemyWave.Add(float.Parse(split[0]), split[1]);
            }
            return enemyWave;
        }

        public Dictionary<float, string> GenerateNextWave(string savePath)
        {
            Dictionary<float, string> enemyBaseWave = new Dictionary<float, string>();
            if (!File.Exists(savePath + "wave.txt"))
                enemyBaseWave = LoadWave(".\\Content\\defaultWave.txt");
            else
                enemyBaseWave = LoadWave(savePath + "wave.txt");

            if (enemyBaseWave.Count == 0)
                enemyBaseWave.Add(0.5f, "basic");
            return enemyBaseWave;
        }

        public void SaveWave(Dictionary<float, string> enemyWave, string Path)
        {
            List<string> fileContent = new List<string>();
            foreach (KeyValuePair<float, string> enemy in enemyWave)
            {
                fileContent.Add(enemy.Key.ToString() + ":" + enemy.Value);
            }

            File.WriteAllLines(Path + "wave.txt", fileContent);
        }
    }
}
