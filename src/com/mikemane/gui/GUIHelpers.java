
package com.mikemane.gui;

import com.mikemane.model.Playlist;
import com.mikemane.model.Track;
import com.mikemane.utils.PlaylistIO;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mikemane
 */
public class GUIHelpers {

    private static JFileChooser chooser;

    public static Path chooseDirectory() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.showOpenDialog(new Component() {
        });
        if (Files.exists(chooser.getSelectedFile().toPath())) {
            File file = chooser.getSelectedFile();
            String s = file.getAbsolutePath();
            return Paths.get(s);
        } else {
            return null;
        }
    }

    public static Track ChooseMp3File() {
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Mp3 Files", "mp3");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Choose an Mp3 File");
        Component parent = null;
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + chooser.getSelectedFile().getName());
            return new Track(Paths.get(chooser.getSelectedFile().getAbsolutePath()));
        } else {
            JOptionPane.showMessageDialog(null, "No Track Selected");
            return null;
        }
    }

    public static Playlist ChooseM3UFile() {
        Playlist p;
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "M3U Files", "m3u");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Choose an M3U File");
        Component parent = null;
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + chooser.getSelectedFile().getName());

            p = PlaylistIO.readTracksFromFile(chooser.getSelectedFile());
            p.setName(chooser.getSelectedFile().getName().replace(".m3u", ""));
            return p;
        } else {
            JOptionPane.showMessageDialog(null, "No Playlist Selected");
            return null;
        }

    }

    public static boolean saveFile(Playlist p) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".m3u")) {
                fw.write(PlaylistIO.playlistAsM3UString(p));
                return true;
            } catch (Exception ex) {
                System.out.println("Error " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

}
