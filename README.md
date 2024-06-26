[![GitHub stars](https://img.shields.io/github/stars/Gameye98/xtrspc-java.svg)](https://github.com/Gameye98/xtrspc-java/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/Gameye98/xtrspc-java.svg)](https://github.com/Gameye98/xtrspc-java/network/members)
[![GitHub issues](https://img.shields.io/github/issues/Gameye98/xtrspc-java.svg)](https://github.com/Gameye98/xtrspc-java/issues)
[![GitHub watchers](https://img.shields.io/github/watchers/Gameye98/xtrspc-java.svg)](https://github.com/Gameye98/xtrspc-java/watchers)
[![Java](https://img.shields.io/badge/language-Java-blue.svg)](https://www.java.com/en/)
[![MIT](https://img.shields.io/badge/license-MIT-red.svg)](https://opensource.org/license/mit)
[![BlackHole Security](https://img.shields.io/badge/team-BlackHole%20Security-ocean.svg)](https://github.com/BlackHoleSecurity)
[![Gameye98/DedSecTL](https://img.shields.io/badge/author-Gameye98/DedSecTL-red.svg)](https://github.com/Gameye98)

[![ForTheBadge built-by-developers](http://ForTheBadge.com/images/badges/built-by-developers.svg)](https://github.com/Gameye98)  

[![BlackHole Security](.gitbhs.svg)](https://github.com/BlackHoleSecurity)

# xtrspc: Android APK Comparison Tool

xtrspc is a tool designed for comparing two Android APK files. It decomposes the APKs, identifies added, removed, modified, and unchanged files, generates a detailed comparison result, and extracts relevant data into separate ZIP files. This tool aids in understanding the differences between two APKs and can be useful for version control or security analysis.

**Usage:**  
$ java xtrspc.java Original.apk Modified.apk

**Requirements**
- apktool  
- java  

**Installations for Termux**
```bash
$ apt update -y && apt upgrade -y
$ apt install openjdk-17 wget -y
$ wget https://raw.githubusercontent.com/Gameye98/DTL-X/master/assets/apktool_2.6.1_aarch64.deb
$ dpkg -i apktool_2.6.1_aarch64.deb
```

**Features:**
- Decompile APKs using apktool  
- Identify added, removed, modified, and unchanged files  
- Generate a detailed comparison result in a text file  
- Extract and compress important data based on the comparison result  

**Author:**
[Gameye98](https://github.com/Gameye98)

**License:**
[MIT](https://github.com/Gameye98/xtrspc/blob/master/README.md)
