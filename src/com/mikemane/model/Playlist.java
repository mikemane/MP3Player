/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.model;

import com.mikemane.utils.PlaylistIO;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author udoka
 */
public class Playlist {

    private String Id;
    private static int ID = 1;
    private ArrayList<Track> tracks;
    private String name;
    private String playlistPath;
    private String dateCreated;
    private String timeCreated;
    public String format;

    public Playlist() {
        setDateCreated();
        setTimeCreated();
        setId(String.valueOf(ID));
        ID++;
        this.tracks = new ArrayList<>();

    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    private void setTimeCreated() {
        LocalTime lt = LocalTime.now();
        this.timeCreated = String.valueOf(lt);
    }

    public String getPlaylistPath() {
        return playlistPath;
    }

    public void setPlaylistPath(String playlistPath) {
        this.playlistPath = playlistPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    private void setDateCreated() {
        LocalDate ld = LocalDate.now();
        this.dateCreated = String.valueOf(ld);
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
    }

    public void removeTrack(int index) {
        this.tracks.remove(index);
    }

    public void removeTracks(int[] indexes) {
        int[] sortedNum = PlaylistIO.sortNumbers(indexes);
        for (int i : sortedNum) {
            this.tracks.remove(i);
        }
    }

    public int getNumberOfTracks() {
        return this.tracks.size();
    }

    public Track getTrack(int row) {
        return tracks.get(row);
    }

    public ArrayList<Track> returnPlaylist() {
        return this.tracks;
    }

    public void sortTrackInPlaylistByArtist() {
        Collections.sort(tracks, new ArtistComparator());
    }

    public void sortTrackInPlaylistByTitle() {
        Collections.sort(tracks, new TitleComparator());
    }

    public void sortTrackByDuration() {
        Collections.sort(tracks, new DurationComparator());
    }

    public void ShuffleTracks() {
        long seed = 1000;
        Collections.shuffle(tracks, new Random(seed));
    }

    public void tagMetaData() {
        tracks.forEach((Track track) -> {
            Path path = Paths.get(track.getFilePath());
            if (!track.getFilePath().isEmpty()) {
                Track theTrack = PlaylistIO.tagMetaData(path);
                track.setArtistName(theTrack.getArtistName());
                track.setDuration(theTrack.getDuration());
                track.setGenre(theTrack.getGenre());
                track.setYear(theTrack.getYear());
                track.setTitle(theTrack.getTitle());
                track.setKbps(theTrack.getKbps());
            }
        });

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.tracks);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Playlist other = (Playlist) obj;
        if (!Objects.equals(this.tracks, other.tracks)) {
            return false;
        }
        return true;
    }

}

class ArtistComparator implements Comparator<Track> {

    @Override
    public int compare(Track o1, Track o2) {
        return o1.getArtistName().compareTo(o2.getArtistName());
    }

}

class TitleComparator implements Comparator<Track> {

    @Override
    public int compare(Track o1, Track o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }

}

class DurationComparator implements Comparator<Track> {

    @Override
    public int compare(Track o1, Track o2) {
        return (o1.getDuration() < o2.getDuration()) ? -1 : (o1.getDuration() > o2.getDuration()) ? 1 : 0;
    }

}
