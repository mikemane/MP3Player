/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author udoka
 */
public class PlaylistFinder {

   public static List<String> getPlayListFileNames(Path p) {
        List<String> listOfPlaylists = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            stream.forEach(file -> {
                if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
                    listOfPlaylists.addAll(getPlayListFileNames(file));
                } else if (PlaylistReader.isValidHeader(file.toString())) {
                    listOfPlaylists.add(file.toString());
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
           
        }
        return listOfPlaylists;
    }

      
    static boolean isMusicFile(String fileName){
       //Account for wav m4a files
       return fileName.endsWith("mp3");
    }
    
}
