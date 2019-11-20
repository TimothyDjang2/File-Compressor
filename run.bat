@echo off
javac -d bin -sourcepath src src/MainClass.java
pause
java -cp bin MainClass in.txt out.txt
pause