/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.gui;

import com.mikemane.model.Playlist;
import com.mikemane.model.Track;
import com.mikemane.utils.CustomPlayer;
import com.mikemane.utils.PlaylistIO;
import com.mikemane.tableModel.PlaylistModel;
import com.mikemane.tableModel.TrackTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author udoka
 */
public class MainView extends JFrame implements ActionListener {

    public static final String DIRECTORY_LOCATION = "MyPlayList/";
    private int count;
    private final JTextField searchText;
    private final JScrollPane trackModelPane;
    private final JTable trackTable, playlistTable;
    private final JPanel mainPanel, buttomLeftPanel, buttomRightPanel;
    private final JSlider slideBar;
    private final PlaylistModel playlistModel;
    private final TrackTableModel trackModel;
    private final JMenuBar menuBar;
    private JMenu menu;
    private JLabel albumArt;
    private final ButtonGroup buttonGroup;
    private final CustomPlayer player;
    private final JButton playButton, fastFowardButton, rewindButton, importTracksButton, exportTracksButton, stopButton,
            sortTrackButton, deleteTracksButton, shuffleTrackButton, shuffleTracksButton, addATrackButton, createPlaylistButton, saveMyPlayListButton;

    private Track thisTrack;
    private Timer myTimer;
    private final ImageIcon playIcon, pauseIcon, fForwardIcon, rewindIcon, stopIcon, shuffleIcon;
    private volatile int value = 0;
    private Playlist thelist;

    public MainView() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(1000, 800);
        setTitle("Udoka Playlist Manager");

        //Initialize Player
        player = new CustomPlayer();
//Intilize icons 
        playIcon = new ImageIcon("ImageIcons/play.png");
        pauseIcon = new ImageIcon("ImageIcons/pause.png");
        fForwardIcon = new ImageIcon("ImageIcons/fast.png");
        rewindIcon = new ImageIcon("ImageIcons/rewind.png");
        stopIcon = new ImageIcon("ImageIcons/stop.png");
        shuffleIcon = new ImageIcon("ImageIcons/shuffle.png");

        //Button panel
        mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraint = new GridBagConstraints();
        //constraint.anchor = GridBagConstraints.FIRST_LINE_START;
        constraint.insets = new Insets(3, 3, 3, 3);
        constraint.fill = GridBagConstraints.BOTH;
        //Initialize Top Layout
        //Order top Buttons
        //Random pane
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());

        GridBagConstraints top = new GridBagConstraints();

        //Stop Button
        stopButton = new JButton(stopIcon);
        stopButton.addActionListener(this);
//

        rewindButton = new JButton(rewindIcon);
        rewindButton.addActionListener(this);
        top.fill = GridBagConstraints.HORIZONTAL;
        top.weightx = .33;
        top.weighty = 1;
        top.gridx = 0;
        top.gridy = 0;
        pane.add(rewindButton, top);
        //Play Pause

        playButton = new JButton(playIcon);
        playButton.addActionListener(this);
        top.weightx = .33;
        top.weighty = 1;
        top.gridx = 1;
        top.gridy = 0;
        pane.add(playButton, top);

        //Forward
        fastFowardButton = new JButton(fForwardIcon);
        fastFowardButton.addActionListener(this);
        top.weightx = .33;
        top.weighty = 1;
        top.gridx = 2;
        top.gridy = 0;
        pane.add(fastFowardButton, top);

        //Shuffle
        shuffleTracksButton = new JButton(shuffleIcon);
        shuffleTracksButton.addActionListener(this);
        top.weightx = .5;
        top.weighty = 1;
        top.gridx = 0;
        top.gridy = 1;
        pane.add(shuffleTracksButton, top);

        //Search Text    
        JLabel searchLabel = new JLabel("Search:", SwingConstants.RIGHT);
        top.weightx = .5;
        top.weighty = 1;
        top.gridx = 1;
        top.gridy = 1;
        pane.add(searchLabel, top);

        searchText = new JTextField();
        searchText.addKeyListener(new ListenForKeys());
        top.weightx = .5;
        top.weighty = 1;
        top.gridx = 2;
        top.gridy = 1;
        //top.gridwidth = 3;
        //pane.add(volumeSlider, top);
        pane.add(searchText, top);

        //Initially disable Buttons 
        disableButton();

        // add pane to panel
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.weightx = 0.2;
        constraint.weighty = 0.1;
        constraint.gridwidth = 1;

        mainPanel.add(pane, constraint);

        slideBar = new JSlider();
        //slideBar.setSize(200, 30);
        slideBar.addMouseListener(new ListenForMouse());
        //slideBar.setPaintTicks(true);
        slideBar.setMajorTickSpacing(1);
        // slideBar.setLabelTable(slideBar.createStandardLabels(20));
        // slideBar.setPaintLabels(true);
        slideBar.setSnapToTicks(true);
        slideBar.setBorder(new TitledBorder("Track Seek Bar"));

        constraint.gridx = 1;
        constraint.gridy = 0;
        constraint.weightx = 0.7;
        constraint.gridwidth = 1;
        mainPanel.add(slideBar, constraint);

        albumArt = new JLabel();
        constraint.gridx = 2;
        constraint.gridy = 0;
        constraint.weightx = 0.1;
        constraint.gridwidth = 1;
        mainPanel.add(albumArt, constraint);
       // mainPanel.add(searchText, constraint);

        //Initialise Second layout
        playlistModel = new PlaylistModel();
        playlistTable = new JTable(playlistModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                //even index, selected or not selected
                if (Index_row % 2 == 0) {
                    comp.setBackground(new Color(240, 240, 240));
                } else {
                    comp.setBackground(Color.white);
                }

                if (isCellSelected(Index_row, Index_col)) {
                    comp.setBackground(new Color(187, 198, 239));

                }
                return comp;
            }
        };

        importSavedPlaylists();
        playlistTable.getSelectionModel().addListSelectionListener(new RowListener());

        constraint.fill = GridBagConstraints.BOTH;
        constraint.ipady = 50;
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.weightx = 0.2;
        constraint.weighty = 0.8;
        mainPanel.add(new JScrollPane(playlistTable), constraint);

        //Track Table
        trackModel = new TrackTableModel();
        trackTable = new JTable(trackModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                //even index, selected or not selected
                if (Index_row % 2 == 0) {
                    comp.setBackground(new Color(240, 240, 240));
                } else {
                    comp.setBackground(Color.white);
                }

                if (isCellSelected(Index_row, Index_col)) {
                    comp.setBackground(new Color(187, 198, 239));

                }
                return comp;
            }

        };

        trackTable.addMouseListener(new ListenForMouse());
        //trackTable.setDragEnabled(true);
        trackTable.setFillsViewportHeight(true);
        trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Track table COnstraints 
        constraint.gridx = 1;
        constraint.gridy = 1;
        constraint.weightx = 0.8;
        constraint.gridwidth = 2;
        trackModelPane = new JScrollPane(trackTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(trackModelPane, constraint);

        buttomLeftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttomLeft = new GridBagConstraints();

        //Buttom Buttons
        //LEFT row
        //Import track Button
        importTracksButton = new JButton("Create Playlist From Dir");
//        importTracksButton.setBorderPainted(false);
//        importTracksButton.setFocusPainted(false);
//        importTracksButton.setContentAreaFilled(false);
        importTracksButton.addActionListener(this);
        buttomLeft.fill = GridBagConstraints.HORIZONTAL;
        buttomLeft.gridx = 0;
        buttomLeft.gridy = 0;
        buttomLeft.weightx = 1;
        buttomLeft.weighty = 1;
        buttomLeftPanel.add(importTracksButton, buttomLeft);

        //Sort Tracks Button
        sortTrackButton = new JButton("Sort Tracks : Name");
        sortTrackButton.addActionListener(this);
        buttomLeft.gridx = 1;
        buttomLeft.gridy = 0;
        buttomLeft.weightx = 1;
        buttomLeft.weighty = 1;
        buttomLeftPanel.add(sortTrackButton, buttomLeft);

        //Delete Tracks
        deleteTracksButton = new JButton("Delete Track");
        deleteTracksButton.addActionListener(this);

        buttomLeft.gridx = 0;
        buttomLeft.gridy = 1;
        buttomLeft.weightx = 1;
        buttomLeft.weighty = 1;
        buttomLeftPanel.add(deleteTracksButton, buttomLeft);

        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.ipady = 0;
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.weightx = 0.2;
        constraint.weighty = 0.1;
        constraint.gridwidth = 1;

        mainPanel.add(buttomLeftPanel, constraint);

        buttomRightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttomRight = new GridBagConstraints();
        buttomRight.fill = GridBagConstraints.HORIZONTAL;
        //RIGHT Row
        //Export Tracks Button
        //exportTracksButton = new JButton(new ImageIcon("ImageIcons/export.png"));
        exportTracksButton = new JButton("Export Playlist");
        exportTracksButton.addActionListener(this);

        buttomRight.gridx = 0;
        buttomRight.gridy = 0;
        buttomRight.weightx = 1;
        buttomRight.weighty = 1;
        buttomRightPanel.add(exportTracksButton, buttomRight);

        //Add Track Button
        addATrackButton = new JButton("Add a Track");
        addATrackButton.addActionListener(this);
        buttomRight.gridx = 1;
        buttomRight.gridy = 0;
        buttomRight.weightx = 1;
        buttomRight.weighty = 1;
        buttomRightPanel.add(addATrackButton, buttomRight);

        //Shuffle Tracks Button
        shuffleTrackButton = new JButton("Shuffle Tracks");
        shuffleTrackButton.addActionListener(this);
        buttomRight.gridx = 2;
        buttomRight.gridy = 0;
        buttomRight.weightx = 1;
        buttomRight.weighty = 1;
        buttomRightPanel.add(shuffleTrackButton, buttomRight);

        //Third Row Buttons
        createPlaylistButton = new JButton("Create Playlist");
        createPlaylistButton.addActionListener(this);
        buttomRight.gridx = 0;
        buttomRight.gridy = 1;
        buttomRight.weightx = 1;
        buttomRight.weighty = 1;
        buttomRightPanel.add(createPlaylistButton, buttomRight);

        saveMyPlayListButton = new JButton("Save");
        saveMyPlayListButton.addActionListener(this);
        buttomRight.gridx = 1;
        buttomRight.gridy = 1;
        buttomRight.weightx = 1;
        buttomRight.weighty = 1;
        buttomRightPanel.add(saveMyPlayListButton, buttomRight);

        constraint.gridx = 1;
        constraint.gridy = 2;
        constraint.weightx = 0.8;
        constraint.gridwidth = 2;
        mainPanel.add(buttomRightPanel, constraint);

        //MenuItems
        menuBar = new JMenuBar();
        menu = new JMenu();
        setJMenuBar(menuBar);
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "File Menu");
        menuBar.add(menu);

        //First Menu
        menu.add(addMenuItem("Import M3U File", KeyEvent.VK_P));
        menu.add(addMenuItem("Create Playlist From Dir", KeyEvent.VK_B));
        menu.add(addMenuItem("Export Playlist as M3U", KeyEvent.VK_P));
        menu.add(addMenuItem("Create Empty Playlist", KeyEvent.VK_P));

        //Second Menu 
        menu = new JMenu("Edit");
        //menu.add(addMenuItem("Delete Track", 0));
        menu.add(addMenuItem("Delete Tracks", 0));
        menuBar.add(menu);

        //Third  menu Item
        menu = new JMenu("Track Menu");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "Track Menu");
        menuBar.add(menu);

        buttonGroup = new ButtonGroup();
        menu.add(addMenuItem("Sort Tracks By Name", KeyEvent.VK_5));
        menu.add(addMenuItem("Sort Tracks By Artist", KeyEvent.VK_5));
        menu.add(addMenuItem("Sort Tracks By Duration", KeyEvent.VK_5));
        menu.addSeparator();

        menu.add(addMenuRadio("Multiple Track Selection", false));
        menu.add(addMenuRadio("Single Track Selection", true));

        menu = new JMenu("Playlist Menu");
        menu.add(addMenuItem("Save", 0));
        menu.add(addMenuItem("Delete Playlist", 0));
        menuBar.add(menu);

        add(mainPanel);
        setJMenuBar(menuBar);
        pack();
        setVisible(true);
    }

    private void importSavedPlaylists() {
      if(Files.exists(Paths.get(DIRECTORY_LOCATION))){
        ArrayList<Playlist> lop = PlaylistIO.checkForFilesAtStart(DIRECTORY_LOCATION);
        if (lop != null) {
            playlistModel.addPlaylists(lop);
        } else {
            System.out.println("No Tracks here");
        }
        playlistModel.fireTableDataChanged();
        //playlistTable.repaint();
      }else {
        JOptionPane.showMessageDialog(this, "");
      }
        
    }

    private JRadioButtonMenuItem addMenuRadio(String text, boolean x) {
        JRadioButtonMenuItem b = new JRadioButtonMenuItem(text, x);
        b.addActionListener(this);
        buttonGroup.add(b);
        return b;
    }

    private JMenuItem addMenuItem(String name, int key) {
        JMenuItem mItem = new JMenuItem(name);
        mItem.setMnemonic(key);
        mItem.addActionListener(this);
        return mItem;
    }

    public GridBagConstraints setLayout(int weightx, int weighty, int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        return null;

    }

    private JButton addButton(String name) {
        JButton button = new JButton(name);
        button.addActionListener(this);
        return button;
    }

    private JButton addButton(String name, String pathToIcon) {
        if (Files.exists(Paths.get(pathToIcon))) {
            JButton button = new JButton(name, new ImageIcon(pathToIcon));
            return button;
        } else {
            JOptionPane.showMessageDialog(this, "The Image dont exist");
            return addButton(name);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int row;
        // Text Generated Buttons
        if (null != command) {
            switch (command) {
                case "Sort Tracks By Artist":
                    try {
                        trackModel.getPlaylist().sortTrackInPlaylistByArtist();
                        trackTable.repaint();
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(this, "No Tracks to Sort ");
                    }
                    break;
                case "Sort Tracks By Duration":
                    try {
                        trackModel.getPlaylist().sortTrackByDuration();
                        trackTable.repaint();
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(this, "No Tracks to Sort ");
                    }
                    break;

                case "Multiple Track Selection":
                    trackTable.setSelectionMode(
                            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    break;
                case "Single Track Selection":
                    trackTable.setSelectionMode(
                            ListSelectionModel.SINGLE_SELECTION);
                    break;
                case "Import M3U File":
                    importM3UFiles();
                    break;
                case "Delete Playlist":
                    deletePlaylist();
                    break;
            }
        }

        if (e.getSource() == playButton) {
            playTrack();
        } else if (e.getSource() == stopButton) {
            stopTrack();
        } else if (e.getSource() == fastFowardButton) {
            fastFoward();
        } else if (e.getSource() == rewindButton) {
            rewindTrack();
        } else if (e.getSource() == importTracksButton || "Create Playlist From Dir".equals(command)) {
            try {
                updatePlaylist();
            } catch (NullPointerException ex) {
                System.out.println("No Tracks In here");
            }
        } else if (e.getSource() == sortTrackButton || "Sort Tracks By Name".equals(command)) {
            try {
                trackModel.getPlaylist().sortTrackInPlaylistByTitle();
                trackTable.repaint();
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "No Tracks to Sort ");
            }
        } else if (e.getSource() == shuffleTrackButton || "Shuffle Track".equals(command) || e.getSource() == shuffleTracksButton) {
            shuffleTracks();
        } else if (e.getSource() == addATrackButton) {
            addTrackToPlaylist();
        } else if (e.getSource() == exportTracksButton || "Export Playlist as M3U".equals(command)) {
            exportPlaylist();
        } else if (e.getSource() == createPlaylistButton || "Create Empty Playlist".equals(command)) {
            createNewPlaylist("Enter a Playlist Name");
        } else if (e.getSource() == deleteTracksButton || "Delete Tracks".equals(command)) {
            deleteTracks();
        } else if (e.getSource() == saveMyPlayListButton || "Save".equals(command)) {
            savePlaylists();
        }
    }

    private void savePlaylists() throws HeadlessException {
        ArrayList<Playlist> listToSave = playlistModel.getListOfPlayList();
        if (listToSave != null) {
            PlaylistIO.saveAllMyPlayList(listToSave, DIRECTORY_LOCATION);
            JOptionPane.showMessageDialog(this, "Saved Playlist");
        }
    }

    public void createNewPlaylist(String dialog) {
        String input = JOptionPane.showInputDialog(this, dialog);
        while (((input = checkIfNameExists(input)) == null) || input.equals("")) {
            input = JOptionPane.showInputDialog(this, "Name Exist or Enter A Name.");
        }
        Playlist p = new Playlist();
        p.setName(input);
        playlistModel.addPlaylist(p);
        playlistModel.updateTable();
        playlistTable.repaint();
    }

    public void addTrackToPlaylist() {
        if (playlistTable.getSelectedRow() == -1) {
            createNewPlaylist("Please Create a Playlist");
        } else {
            Track t = GUIHelpers.ChooseMp3File();
            if (t != null) {
                trackModel.getPlaylist().addTrack(t);
                trackModel.fireTableDataChanged();
                trackTable.repaint();
                playlistTable.repaint();
            }
        }
    }

    private void shuffleTracks() throws HeadlessException {
        try {
            trackModel.getPlaylist().ShuffleTracks();
            trackTable.repaint();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "No Tracks to Sort ");
        }
    }

    private void deleteTracks() {
        try {
            stopTrack();
           // PlaylistIO.deleteTracksFromPlaylist(playlistModel.getPlayList(playlistTable.getSelectedRow()), trackTable.getSelectedRows());
            PlaylistIO.deleteTracksFromPlaylist(trackModel.getPlaylist(), trackTable.getSelectedRows());
            trackTable.repaint();
            trackTable.clearSelection();
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Please Select Row / Rows");
        }
    }

    private void updatePlaylist() {
        Playlist play = PlaylistIO.getPlayistFromPath(GUIHelpers.chooseDirectory());
        if (play.getNumberOfTracks() != 0) {
            String name = JOptionPane.showInputDialog(this, "Enter a Name ");

            while (((name = checkIfNameExists(name)) == null) || name.equals("")) {
                name = JOptionPane.showInputDialog(this, "Name Exist or Enter A Name.");
            }
            play.setName(name);
            play.tagMetaData();
            updateAndRefreshPlaylist(play);
        } else {
            JOptionPane.showMessageDialog(this, "No Tracks In this Folder");
        }
    }

    private void updateAndRefreshPlaylist(Playlist play) {
        playlistModel.addPlaylist(play);
        playlistModel.fireTableDataChanged();
        playlistTable.repaint();
    }

    private void exportPlaylist() {
        int row = playlistTable.getSelectedRow();
        if (row >= 0) {
            boolean isSaved = GUIHelpers.saveFile(playlistModel.getPlayList(row));
            if (isSaved) {
                JOptionPane.showMessageDialog(this, "Your playlist has been Saved");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No Playlist Selected");
        }
    }

    private void importM3UFiles() {
        Playlist playlist = GUIHelpers.ChooseM3UFile();
        if (playlist != null) {
            updateAndRefreshPlaylist(playlist);
        }
    }

    private void deletePlaylist() {
        stopTrack();
        String s;
        int row = playlistTable.getSelectedRow();
        if (Integer.valueOf(row) != null) {
            s = playlistModel.getPlayList(row).getName();
            playlistModel.removePlaylist(row);
            JOptionPane.showMessageDialog(this, "Playlist " + s + " has been deleted");
            trackModel.removePlayList();
            trackModel.fireTableDataChanged();
            trackTable.repaint();
            playlistTable.repaint();
            try {
                if (Files.deleteIfExists(Paths.get(DIRECTORY_LOCATION + File.separator + s + ".m3u"))) {
                }
            } catch (IOException ex) {
                System.out.println("File Cannot Be Delete");
            }
            trackModel.fireTableDataChanged();
            trackTable.repaint();

        }
    }

    private String checkIfNameExists(String filename) {
        String input = filename;
        for (Playlist p : playlistModel.getListOfPlayList()) {
            if (p.getName().equals(input)) {
                return null;
            }
        }

        return input;
    }

    private class RowListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (e.getValueIsAdjusting()) {
                return;
            }
            updateTrack();
        }
    }

    public void updateTrack() {
        int theSelectedRow = playlistTable.getSelectedRow();

        if (theSelectedRow >= 0) {
            trackModel.setPlaylist(playlistModel.getPlayList(theSelectedRow));
            thelist = trackModel.getPlaylist();
            trackModelPane.setViewportView(trackTable);
        }
    }

    private void playTrack() {
        int row = trackTable.getSelectedRow();
        
        if ((row >= 0 && row <= trackTable.getRowCount() - 1)) {
            thisTrack = getPathFromTrackIndex(row);
            String path = thisTrack.getFilePath();
            int kbps = thisTrack.getKbps();
            if (playButton.getIcon().equals(playIcon)) {
                if (player.canResume()) {
                    player.resume();
                    myTimer.start();
                } else {
                    enableButtons();
                    player.setKBPS(kbps);
                    playTrackFromPath(path);
                    activateTimer();
                    albumArt.setIcon(thisTrack.trackAlbumArtWork());
                }
                playButton.setIcon(pauseIcon);
            } else if (playButton.getIcon() == pauseIcon) {
                myTimer.stop();
                player.pause();
                playButton.setIcon(playIcon);
            }
        } else {
            System.out.println("Select a Track");
        }
    }

    private Track getPathFromTrackIndex(int row) {
        //Track t = playlistModel.getPlayList(playlistTable.getSelectedRow()).getTrack(row);
        Track t = trackModel.getPlaylist().getTrack(row);
        return t;
    }

    private void playTrackFromPath(String path) {
        player.setPath(path);
        player.play(-1);
    }

    private void rewindTrack() {
        int row = trackTable.getSelectedRow();
        row = skipToPrevious(row);
        //killMyThread();
        initiateSkipping(row);

    }

    private int skipToPrevious(int row) {
        if (row == 0) {
            row = trackTable.getRowCount() - 1;
        } else {
            row = trackTable.getSelectedRow() - 1;
        }
        return row;
    }

    private void fastFoward() {
        int row;
        row = skipToNextTrack();
        initiateSkipping(row);
    }

    private int skipToNextTrack() {
        int row;
        row = trackTable.getSelectedRow();
        if (row == trackTable.getRowCount() - 1) {
            row = 0;
        } else {
            row = trackTable.getSelectedRow() + 1;
        }
        return row;
    }

    private void initiateSkipping(int row) {
        if (row >= 0 && row <= trackTable.getRowCount() - 1) {
            if (playButton.getIcon() == playIcon) {
                if (player.canResume()) {
                    stopTrack();
                    trackTable.setRowSelectionInterval(row, row);
                } else {
                    trackTable.setRowSelectionInterval(row, row);
                }
            } else if (playButton.getIcon() == pauseIcon) {
                stopTrack();
                trackTable.setRowSelectionInterval(row, row);
                playTrack();
            }
        }
    }

    public void stopTrack() {
//        player.stop();
        if (myTimer != null) {
            if (myTimer.isRunning()) {
                myTimer.stop();
            }
        }

        playButton.setIcon(playIcon);
        slideBar.setValue(0);
        player.stopTrack();
    }

    public void enableButtons() {
        rewindButton.setEnabled(true);
        fastFowardButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public final void disableButton() {
        rewindButton.setEnabled(false);
        fastFowardButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    public void activateTimer() {
        slideBar.setValue(0);
        count = 0;
        myTimer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.isComplete()) {
                    slideBar.setValue(0);
                    fastFoward();
                } else {
                    loadPlayProgressBar();
                }

            }

            private void loadPlayProgressBar() {
                int tt = thisTrack.getDuration();
                slideBar.setMaximum(tt);
                //slideBar.setMajorTickSpacing(tt);

                count = player.getRemainingTimeLeft();
                slideBar.setValue(count);
                if (slideBar.getValue() < tt) {
                    slideBar.setValue(tt - player.getRemainingTimeLeft());
                }
            }
        });
        myTimer.start();
    }

    private class ListenForMouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == trackTable) {
                if (e.getClickCount() == 2) {
                    stopTrack();
                    playTrack();

                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() == slideBar) {
                getAndPlay();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }

    private void getAndPlay() {
        value = slideBar.getValue();
        player.setStoppedValue(value);
    }

    private class ListenForKeys implements KeyListener {

        final Playlist original = null;

        public ListenForKeys() {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            final Playlist original = trackModel.getPlaylist();

            if (e.getSource() == searchText) {
                searchForName(searchText.getText());
            }
        }

        private void searchForName(String text) {
            if (!text.isEmpty() || !text.equals(" ") || !text.equals("")) {
                Playlist newList = new Playlist();
                if (thelist != null) {
                    for (Track t : thelist.getTracks()) {
                        String txtLowerCase = text.toLowerCase();
                        if (t.getTitle().toLowerCase().contains(txtLowerCase)
                                || t.getArtistName().toLowerCase().contains(txtLowerCase)
                                || t.getGenre().contains(txtLowerCase)) {
                            newList.addTrack(t);
                        }
                    }
                    trackModel.setPlaylist(newList);
                    trackModel.fireTableDataChanged();
                    trackTable.repaint();
                } else {
                    System.out.println("No tracks to Search For");
                }

            }
        }
    }
}
