==========================================
||       KOMPILASI & MENJALANKAN        ||
==========================================

[LANGKAH 1] Menghapus folder 'bin' lama jika ada
rm -rf bin

[LANGKAH 2] Membuat folder 'bin' baru
mkdir bin

[LANGKAH 3] Menyalin folder 'assets' dari 'src' ke dalam 'bin'
xcopy src\assets bin\assets /s /i /y

[LANGKAH 4] Memulai kompilasi tahap demi tahap
Mengompilasi paket 'utils'
javac -d bin -cp 'lib/*' src/utils/*.java

Mengompilasi paket 'model'
javac -d bin -cp 'lib/*' src/model/*.java

Mengompilasi paket 'database'
javac -d bin -cp 'lib/*' src/database/*.java

Mengompilasi paket 'viewmodel'
javac -d bin -cp 'lib/*' src/viewmodel/*.java

Mengompilasi paket 'view'
javac -d bin -cp 'bin:lib/*' src/view/*.java

Mengompilasi 'Main.java'
javac -d bin -cp 'bin:lib/*' src/Main.java

[LANGKAH 5] Menjalankan Program
java -cp "bin;lib/*" Main


[KESELURUHAN]
rm -rf bin
mkdir bin
xcopy src\assets bin\assets /s /i /y
javac -d bin -cp "lib/*" src/utils/*.java
javac -d bin -cp "bin;lib/*" src/model/*.java
javac -d bin -cp "bin;lib/*" src/database/*.java
javac -d bin -cp "bin;lib/*" src/viewmodel/*.java
javac -d bin -cp "bin;lib/*" src/view/*.java
javac -d bin -cp "bin;lib/*" src/Main.java
java -cp "bin;lib/*" Main
