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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author mikemane
 */
public class PlaylistIO {

    public static final String MP3_EXTENSION = "mp3";
    public static final String M3U_HEADER = "#EXTM3U";
    public static final String M3U_METADATA = "#EXTINF";

    public static List<String> readDirectory(Path p) {
        List<String> dirName = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            stream.forEach((Path file) -> {
                if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
                    dirName.addAll(readDirectory(file));
                } else {
                    if (file.toString().endsWith(MP3_EXTENSION)) {
                        dirName.add(file.toString());
                    }
                }
            });
        } catch (IOException ex) {
            System.out.println("Error " + ex.getMessage());
            System.exit(1);
        }
        return dirName;
    }
    
        public static List<String> readM3UDirectory(Path p) {
        List<String> dirName = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            stream.forEach((Path file) -> {
                if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
                    dirName.addAll(readDirectory(file));
                } else {
                    if (file.toString().endsWith("m3u")) {
                        dirName.add(file.toString());
                    }
                }
            });
        } catch (IOException ex) {
            System.out.println("Error " + ex.getMessage());
            System.exit(1);
        }
        return dirName;
    }

    public static Playlist getPlaylistFromPaths(List<String> trackPaths) {
        Playlist playlist = new Playlist();
        trackPaths.forEach((String path) -> {
            Track track = new Track();
            track.setFilePath(path);
            playlist.addTrack(track);
        });
        return playlist;
    }

    public static Playlist getPlayistFromPath(Path path) {
        Playlist playlist = getPlaylistFromPaths(readDirectory(path));
        return playlist;
    }

    public static boolean isMP3File(String fileName) {
        return fileName.endsWith("mp3");
    }

    public static Track tagMetaData(Path p) {
        Track newTrack = new Track();
        if (Files.exists(p) && isMP3File(p.toString())) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(p.toString()));
                Tag tag = audioFile.getTag();
                AudioHeader audioHeader = audioFile.getAudioHeader();

                newTrack.setFilePath(p.toString());
                newTrack.setDuration(audioHeader.getTrackLength());
                newTrack.setArtistName(tag.getFirst(FieldKey.ARTIST));
                newTrack.setTitle(tag.getFirst(FieldKey.TITLE));
                newTrack.setGenre(tag.getFirst(FieldKey.GENRE));
                newTrack.setYear(tag.getFirst(FieldKey.YEAR));
                newTrack.setKbps((int) audioHeader.getBitRateAsNumber());
            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
                System.out.println("Error with the path " + p.toString() + " Error of: " + ex.getMessage());
            }
        }
        return newTrack;
    }

    public static String playlistAsM3UString(Playlist p) {
        StringBuilder m3uContents = new StringBuilder();
        m3uContents.append(M3U_HEADER);
        m3uContents.append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"));

        p.getTracks().forEach((Track track) -> {
            m3uContents.append(M3U_METADATA + ":")
                    .append(track.getDuration())
                    .append(", ")
                    .append(track.getArtistName())
                    .append(" - ")
                    .append(track.getTitle())
                    .append(System.getProperty("line.separator"))
                    .append(track.getFilePath())
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));

        });
        return m3uContents.toString();
    }

    public static Playlist createPlaylistFromSelectedTracks(Track[] tracks) {
        Playlist playlist = new Playlist();
        for (Track track : tracks) {
            playlist.addTrack(track);
        }
        return playlist;
    }

    public static void deleteTracksFromPlaylist(Playlist p, int[] arr) {
        int[] sortedNum = sortNumbers(arr);

        for (int i : sortedNum) {
            p.removeTrack(i);
        }

    }

    public static int[] sortNumbers(int[] arr) {
        int temp;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 1; j < (arr.length - i); j++) {
                //if numbers[j-1] < numbers[j], swap the elements
                if (arr[j - 1] < arr[j]) {
                    temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }

    public static Playlist readTracksFromFile(File file) {
        Playlist p = new Playlist();
        Track t;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String s = reader.readLine();
            String nextLine;
            String path;
           // System.out.println("S : " + s);
            if (s.equals(M3U_HEADER)) {
                while ((nextLine = reader.readLine()) != null) {
                    path = reader.readLine();
                    if (path != null) {
                        if ((path.startsWith(M3U_METADATA))) {
                            String filePath = reader.readLine();
//                            System.out.println(xx);
                            if (Files.exists(Paths.get(filePath))) {
                                t = new Track(Paths.get(filePath));
                                p.addTrack(t);
                            } else {
                                System.out.println("File " + filePath + " does not exist");
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PlaylistIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public static ArrayList<Playlist> checkForFilesAtStart(String path) {
        ArrayList<Playlist> listOfPlayList = new ArrayList<>();
        List<String> listOfString = readM3UDirectory(Paths.get(path));
        listOfString.forEach((String filename) -> {   
          File f = new File(filename);  
          Playlist p = readTracksFromFile(f);
          p.setName(f.getName().replaceAll(".m3u", ""));
          listOfPlayList.add(p);
        });
        return listOfPlayList;
    }

    public static boolean saveAllMyPlayList(List<Playlist> playlists, String path) {
        playlists.forEach((Playlist p) -> {
            try (FileWriter fileToWrite = new FileWriter(new File(path + p.getName() + ".m3u"))) {
                fileToWrite.write(playlistAsM3UString(p));
            } catch (IOException ex) {
                System.out.println("Error Writing file " + p.getName());
            }
        });
        return true;
    }

    class IntegerComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }

    }

}
