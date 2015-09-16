/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.utils;

import com.mikemane.model.Playlist;
import com.mikemane.model.Track;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author udoka
 */
public class PlaylistReader {

    public static final String PLAYLIST_HEADER = "#EXTM3U";
    public static final String PLAYLIST_METADATA = "#EXTINF";
    public static final String MP3_EXTENSION = "mp3";

    public static boolean isValidHeader(String fileName) {
        boolean validHeader = false;

        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            String firstLine = br.readLine();
            if (firstLine.startsWith(PLAYLIST_HEADER)) {
                validHeader = true;
            } else {
                JOptionPane.showMessageDialog(null, "Not a Valid Header");
            }
            br.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return validHeader;

    }

    public static int getNumberOfTracks(String fileName) {
        int numOfTracks = 0;

        if (isValidHeader(fileName)) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
                String nextLine;

                while ((nextLine = br.readLine()) != null) {
                    if (!nextLine.startsWith(PLAYLIST_METADATA) && nextLine.endsWith(MP3_EXTENSION)) {
                        numOfTracks++;
                    }
                }
                br.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error with file " + fileName + ", Error: " + ex.getMessage());
            }
        }
        return numOfTracks;
    }

    static int getNumberOfSeconds(String fileName) {
        int numberOfSeconds = 0;

        if (isValidHeader(fileName)) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
                String nextLine;
                while ((nextLine = br.readLine()) != null) {
                    //If the next line is metadata it should be possible to extract the length of the song
                    if (nextLine.startsWith(PLAYLIST_METADATA)) {
                        numberOfSeconds = getNumberOfSeconds(nextLine, numberOfSeconds);
                    }
                    br.close();
                }
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error with file " + fileName + ", Error: " + ex.getMessage());
                numberOfSeconds = 0;
            }
        }
        return numberOfSeconds;
    }

    private static int getNumberOfSeconds(String nextLine, int numberOfSeconds) throws NumberFormatException {
        int i1 = nextLine.indexOf(":");
        int i2 = nextLine.indexOf(",");
        String substr = nextLine.substring(i1 + 1, i2);
        numberOfSeconds += Integer.parseInt(substr);
        return numberOfSeconds;
    }

    public static Playlist getPlaylist(File plFilename) {
        Playlist playlist = new Playlist();

        if (plFilename.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(plFilename))) {
                if (PlaylistReader.isValidHeader(plFilename.toString())) {
                    String nextLine;
                    String trackFilename = "";
                    String trackMetaData = "";
                    while ((nextLine = br.readLine()) != null) {
                        if (nextLine.startsWith(PLAYLIST_METADATA)) {
                            trackMetaData = nextLine;
                        } else if (!nextLine.equals(PLAYLIST_HEADER) && !nextLine.isEmpty()) {
                            trackFilename = nextLine;
                            playlist.addTrack(getTrack(trackFilename, trackMetaData));
                        }
                    }
                    br.close();
                } else {
                    playlist = null;
                }
            } catch (Exception e) {
                System.err.println("getPlaylist:: error with file "
                        + plFilename + ": " + e.getMessage());
                playlist = null;
            }

        } else {
            playlist = null;
        }

        return playlist;
    }

    public static Track getTrack(String trackFilename, String trackMetaData) {
        //Set some default values just in case
        int seconds = 0;
        String metaData = "Not Set";
        Track track = new Track();

        int start = trackMetaData.indexOf(":");
        int end = trackMetaData.indexOf(",");
        if (start != -1 && end != -1) {
            seconds = Integer.parseInt(trackMetaData.substring(start + 1, end));
            metaData = trackMetaData.substring(end + 1);
        }

        track.setTitle(trackFilename);
        track.setMetaData(metaData);
        track.setDuration(start);

        return track;
    }

}
