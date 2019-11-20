@echo off
javac -d bin -sourcepath src src/MainClass.java
pause
java -cp bin MainClass out.txt decompressed.txt huhu
pause