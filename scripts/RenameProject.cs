using System;
using System.IO;

namespace RenameProject
{
    public static class MainClass
    {
        public static int Main(string[] args)
        {
            string oldProjName = "Junbo";
            string newProjName = "SilkCloud";       // or Oculus

            RenameDirectory(oldProjName, newProjName, "../");

            return 0;
        }

        public static void RenameDirectory(string oldName, string newName, string directory)
        {
            if (directory.EndsWith("/.git")) return;
            
            Console.WriteLine("Processing " + directory);
            foreach (string filename in Directory.GetFiles(directory))
            {
                string textContent = File.ReadAllText(filename);
                string newContent = textContent.ReplaceName(oldName, newName);

                if (textContent != newContent)
                {
                    Console.WriteLine("Updated File {0}", Path.GetFileName(filename));
                    File.SetAttributes(filename, File.GetAttributes(filename) & ~FileAttributes.ReadOnly);
                    File.WriteAllText(filename, newContent);
                }

                string newFilename = Path.GetFullPath(
                    Path.Combine(Path.GetDirectoryName(filename), Path.GetFileName(filename).ReplaceName(oldName, newName)));
                if (newFilename != Path.GetFullPath(filename))
                {
                    Console.WriteLine("Rename File {0} to {1}", Path.GetFileName(filename), Path.GetFileName(newFilename));
                    File.Move(filename, newFilename);
                }
            }

            foreach (string subdir in Directory.GetDirectories(directory))
            {
                DirectoryInfo dirInfo = new DirectoryInfo(subdir);
                string newDirName = Path.Combine(dirInfo.Parent.FullName, dirInfo.Name.ReplaceName(oldName, newName));

                if (Path.GetFullPath(subdir) != Path.GetFullPath(newDirName))
                {
                    Console.WriteLine("Rename Dir {0} to {1}", new DirectoryInfo(subdir).Name, new DirectoryInfo(newDirName).Name);
                    Directory.Move(subdir, newDirName);
                }

                RenameDirectory(oldName, newName, newDirName);
            }
        }

        public static string ReplaceName(this string textcontent, string oldName, string newName)
        {
            return textcontent.Replace(oldName, newName)
                    .Replace(oldName.ToUpper(), newName.ToUpper())
                    .Replace(oldName.ToLower(), newName.ToLower());
        }
    }
}

