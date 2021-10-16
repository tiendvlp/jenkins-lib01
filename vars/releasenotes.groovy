import java.io.*;
import groovy.io.*;

@NonCPS
def call(Map config=[:]) {
    println("hello i'm lib01");
    def dir = new File (pwd());
    
    new File (dir.path + '/releasenote.txt').withWriter('utf-8') {
        writer -> 
            dir.eachFileRecurse(FileType.ANY) { file -> 
                if (file.isDirectory()) {
                    writer.writeLine(file.name);
                } else {
                    writer.writeLine('\t' + file.name + '\t' + file.length());
                }
            }
    }
}