/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.tableModel;

import com.mikemane.model.Playlist;
import com.mikemane.model.Track;
import javax.swing.table.AbstractTableModel;
/**
 *
 * @author mikemane
 */
public class TrackTableModel extends AbstractTableModel{

    public Playlist playlist;
    private final String[] columnNames = {
        "Title",
        "Artist",
        "Genre",
        "Duration",
        "Year",
        "Date added"};

    public TrackTableModel() {
        playlist = null;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public int getRowCount() {
        if(playlist != null){
            return playlist.getNumberOfTracks();
        }else{
            return 0;
        }
        
        
    }

    @Override
    public int getColumnCount() {
        return (columnNames.length == 0 ? 0 : columnNames.length);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Track track = playlist.getTrack(rowIndex);
        if (columnIndex == 0) {
            return track.getTitle();
        } else if (columnIndex == 1) {
            return track.getArtistName();
        } else if (columnIndex == 2) {
            return track.getGenre();
        } else if (columnIndex == 3) {
            return track.getDuration();
        }else if (columnIndex == 4) {
            return track.getYear();
        }else if (columnIndex == 5) {
            return track.getDateAdded();
        }
        return null;
    }

    public Class getColumnClass(int column) {
        Object value = this.getValueAt(0, column);
        return (value == null ? Object.class : value.getClass());
    }

    public boolean isCellEditable(int rowIndex, int colIndex) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        //data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

    public void removePlayList() {
        this.playlist = null;
    }

}
