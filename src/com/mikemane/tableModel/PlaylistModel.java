/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.tableModel;

import com.mikemane.model.Playlist;
import com.mikemane.utils.PlaylistIO;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author udoka
 */
public class PlaylistModel extends AbstractTableModel {

    private final ArrayList<Playlist> listOfPlayList;
    private final String[] columns = {
        "ID",
        "Playlist Name ",
        "Date Created",
        "Time Created"};

    public PlaylistModel() {
        listOfPlayList = new ArrayList<>();
    }

    public ArrayList<Playlist> getListOfPlayList() {
        return listOfPlayList;
    }

    public void addPlaylist(Playlist p) {
        listOfPlayList.add(p);
    }

    public void removePlaylists(int[] indexes) {
        int[] sortedNums = PlaylistIO.sortNumbers(indexes);
        for (int i : sortedNums) {
            this.listOfPlayList.remove(i);
        }
    }
    
      public void removePlaylist(int index) {
            this.listOfPlayList.remove(index);     
    }

    public Playlist getPlayList(int index) {
        return listOfPlayList.get(index);
    }

    @Override
    public int getRowCount() {
        try {
            return listOfPlayList.size();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return (columns.length == 0 ? 0 : columns.length);
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Playlist playlist = listOfPlayList.get(rowIndex);
        if (columnIndex == 0) {
            return playlist.getId();
        } else if (columnIndex == 1) {
            return playlist.getName();
        } else if (columnIndex == 2) {
            return playlist.getDateCreated();
        } else if (columnIndex == 3) {
            return playlist.getTimeCreated();
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

    public void updateTable() {
        fireTableDataChanged();
    }
    
    public void addPlaylists(ArrayList<Playlist> listToAdd){
        if(listToAdd != null){
            this.listOfPlayList.addAll(listToAdd);
        }
    }

}
