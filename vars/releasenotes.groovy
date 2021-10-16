import java.io.*;
import groovy.io.*;
import java.util.Calendar.*;
import java.text.SimpleDateFormat;
import hudson.model.*;

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

    def date = new Date();
    def sdf = new SimpleDateFormat('dd/MM/yyyy HH:mm')
    echo "Date and time is: " + sdf.format(date)

    echo "Build number is: ${BUILD_NUMBER}"

    def changeLogSets = currentBuild.changeSets;

    for (change in changeLogSets) {
        def browser = change.browser
        def entries = change.items;
        echo browser.getRepoUrl().toString()
        for (entry in entries) {
            echo "  ${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
            for (file in entry.affectedFiles) {
                echo "      ${file.editType.name} ${file.path}";
            }
        }
    }

    if (config.changes != "false") {
        echo "changes"
    }
}