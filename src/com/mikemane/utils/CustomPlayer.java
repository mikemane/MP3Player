package com.mikemane.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class CustomPlayer {

    private AdvancedPlayer advPlayer;
    private FileInputStream FIS;
    private BufferedInputStream BIS;
    private boolean canResume;
    private String path;
    private int total;
    private int stopped;
    private boolean valid;
    private Thread playThread;
    public volatile boolean isComplete;
    int KBPS;
    int kilobit = 1024;

    public final double BYTES_TO_KILOBITS = 0.008;
    public final int KILOBIT_TO_BYTES = 125;

    public CustomPlayer() {
        advPlayer = null;
        FIS = null;
        valid = false;
        BIS = null;
        path = null;
        total = 0;
        stopped = 0;
        canResume = false;
        isComplete = false;
    }
    
    public boolean checkRunningThread(){
      boolean running = false;
        if(playThread != null){
            if(playThread.isAlive()){
                running = true;
            }
        }
        return running;
    }    

    public boolean canResume() {
        return canResume;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setKBPS(int i) {
        KBPS = i;
    }

    public void resume() {
        if (!canResume) {
            return;
        }
        if (play(total - stopped)) {
            canResume = false;
        }
    }

    public boolean play(int pos) {
        valid = true;
        canResume = false;
        isComplete = false;
        try {
            pause();

            FIS = new FileInputStream(path);
            total = FIS.available();
            if (pos > -1) {
                FIS.skip(pos);
            }
            BIS = new BufferedInputStream(FIS);
            advPlayer = new AdvancedPlayer(BIS);
            isComplete = false;
            playThread = new Thread(() -> {
                try {
                    advPlayer.play();
                    isComplete = true;
                } catch (Exception e) {
                    valid = false;
                }
            });
            playThread.start();
        } catch (IOException | JavaLayerException e) {
            valid = false;
        }
        return valid;
    }

    public void stopTrack() {
        if (playThread != null) {
            if (playThread.isAlive()) {
                playThread.interrupt();
                advPlayer.close();
            } else {
                playThread.interrupt();
            }
            canResume = false;
        }
    }

    public void pause() {
        try {
            stopped = FIS.available();
            advPlayer.close();
            FIS = null;
            BIS = null;
            advPlayer = null;
            if (valid) {
                canResume = true;
            }
        } catch (Exception e) {
        }
    }

    public int getRemainingTimeLeft() {
        try {
            if (Integer.valueOf(FIS.available()) != null) {
                int i = convertToSeconds(total);
                int j = convertToSeconds(FIS.available());
                return Integer.valueOf(FIS.available()) == null ? 0
                        : convertToSeconds(FIS.available());
            } else {
                return 0;
            }
        } catch (IOException | NullPointerException ex) {
            System.out.println("Fis exception caught" + ex.getMessage());
            return stopped;
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public int getTotalTimeOfTrack() {
        return Integer.valueOf(total) == null ? 0 : convertToSeconds(total);
    }

    public int pausedValue() {
        return stopped;
    }

    public void setStoppedValue(int value) {
        pause();
        stopped = convertNumberToBytes(value);
        play(stopped);
    }

    public int convertToSeconds(int bytes) {
        double result = bytes * (BYTES_TO_KILOBITS) / KBPS;
        Double d = result;
        return d.intValue();

    }

    private int convertNumberToBytes(int value) {
        double result = value * KILOBIT_TO_BYTES * KBPS;
        Double d = result;
        return d.intValue();
    }
}
