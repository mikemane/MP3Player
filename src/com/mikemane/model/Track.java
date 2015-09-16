/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.model;

import com.mikemane.utils.PlaylistIO;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author udoka
 */
public class Track {

    private String title;
    private String artistName;
    private int duration;
    private String genre;
    private String filePath;
    private String year;
    private String metaData;
    private LocalDate dateAdded;
    private int kbps;
    private ImageIcon albumArtWork;

    public ImageIcon trackAlbumArtWork() {
        
        try {
            //this.albumArtWork = albumArtWork;
            Mp3File song = new Mp3File(getFilePath());
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                byte[] imageData = id3v2tag.getAlbumImage();
                if (imageData != null) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                    ImageIcon icon = new ImageIcon(img);
                    Image image = icon.getImage(); // transform it 
                    Image newimg = image.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH); // set the resolution yu want        
                    this.albumArtWork =  new ImageIcon(newimg);
                }else
                {
                    ImageIcon icon = new ImageIcon("ImageIcons/missing.png");
                    Image image = icon.getImage();
                    Image newImg = image.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH); 
                    this.albumArtWork = new ImageIcon(newImg);
                }
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.albumArtWork;
    }

    public int getKbps() {
        return kbps;
    }

    public void setKbps(int kbps) {
        this.kbps = kbps;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    private void setDateAdded() {
        this.dateAdded = LocalDate.now();
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (year.contains("T")) {
            Date result = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                result = sf.parse(year);
            } catch (ParseException ex) {
                System.out.println("Date Error");
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(result);
            this.year = String.valueOf(cal.get(Calendar.YEAR));
        } else {
            this.year = year;
        }

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String filename) {
        this.title = filename;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Track() {
        setDateAdded();
    }

    public Track(Path path) {
        setDateAdded();

        Track track = PlaylistIO.tagMetaData(path);
        this.artistName = track.getArtistName();
        this.duration = track.getDuration();
        this.filePath = track.getFilePath();
        this.genre = track.getGenre();
        this.title = track.getTitle();
        this.year = track.getYear();
        this.kbps = track.getKbps();
    }

}
