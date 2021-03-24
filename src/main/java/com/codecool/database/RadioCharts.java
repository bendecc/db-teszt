package com.codecool.database;

import java.sql.*;
import java.util.*;

public class RadioCharts {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public RadioCharts(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public String getMostPlayedSong() {
        String mostPlayed = "";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT song, times_aired FROM music_broadcast";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Song song = new Song(
                        resultSet.getString("song"),
                        resultSet.getInt("times_aired")
                );

                if (songs.contains(song)) {
                    songs.get(songs.indexOf(song)).addAirTime(song.getTimesAired());
                } else {
                    songs.add(song);
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        if (songs.size() != 0) mostPlayed = doGetMostPlayed(songs);
        return mostPlayed;
    }

    private String doGetMostPlayed(List<Song> songs) {
        Song mostPlayed = songs.get(0);
        for (Song song : songs) {
            if (song.getTimesAired() > mostPlayed.getTimesAired()) {
                mostPlayed = song;
            }
        }
        return mostPlayed.getTitle();
    }

    public String getMostActiveArtist() {
        String mostActive = "";
        List<Artist> artists = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT artist, song FROM music_broadcast";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Artist artist = new Artist(resultSet.getString("artist"));
                if (artists.contains(artist)) artist = artists.get(artists.indexOf(artist));
                artist.addSongTitle(resultSet.getString("song"));
                artists.add(artist);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        if (artists.size() != 0) mostActive = doGetMostActive(artists);
        return mostActive;
    }

    private String doGetMostActive(List<Artist> artists) {
        Artist mostActive = artists.get(0);
        for (Artist artist : artists) {
            if (artist.getAmountOfSongs() > mostActive.getAmountOfSongs()) mostActive = artist;
        }
        return mostActive.getName();
    }
}
