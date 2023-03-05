package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        if(!artists.contains(artistName))
        {
            Artist artist=new Artist(artistName);
            artists.add(artist);
        }
        Album album=new Album(title);
        albums.add(album);
        artistAlbumMap.put(new Artist(artistName),albums);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = null;
        for (Album al : albums) {
            if (al.getTitle().equals(albumName)) {
                album = al;
                break;
            }
        }
        if (album == null) {
            throw new Exception("Album does not exist");
        } else {
            Song song = new Song(title, length);
            songs.add(song);
            albumSongMap.put(album, songs);
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song>songlist=new ArrayList<>();
        for(Song song:songs)
        {
            if(song.getLength()==length)
            {
                songlist.add(song);
            }
        }
        playlistSongMap.put(playlist,songlist);
        User user=null;
        for(User u:users)
        {
            if(u.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }
        else
        {
            List<User>userList=new ArrayList<>();
            userList.add(user);
            playlistListenerMap.put(playlist,userList);
            creatorPlaylistMap.put(user,playlist);
            if(userPlaylistMap.containsKey(user))
            {
                List<Playlist>list=userPlaylistMap.get(user);
                list.add(playlist);
                userPlaylistMap.put(user,list);
            }
            else
            {
                List<Playlist>playlists1=new ArrayList<>();
                playlists1.add(playlist);
                userPlaylistMap.put(user,playlists1);
            }
            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist=new Playlist(title);
        List<Song>songslist=new ArrayList<>();
        for(Song song:songs)
        {
            if(song.getTitle().equals(title))
            {
                songslist.add(song);
            }
        }
        playlistSongMap.put(playlist,songslist);
        User user=null;
        for(User u:users)
        {
            if(u.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }
        else
        {
            List<User>userPlaylist=new ArrayList<>();
            userPlaylist.add(user);
            playlistListenerMap.put(playlist,userPlaylist);
            creatorPlaylistMap.put(user,playlist);
            if(userPlaylistMap.containsKey(user))
            {
                List<Playlist>userList=userPlaylistMap.get(user);
                userList.add(playlist);
                userPlaylistMap.put(user,userList);
            }
            else
            {
                List<Playlist>playlists1=new ArrayList<>();
                playlists1.add(playlist);
                userPlaylistMap.put(user,playlists1);
            }
            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist=null;
        for(Playlist p:playlists)
        {
            if(p.getTitle().equals(playlistTitle))
            {
                playlist=p;
                break;
            }
        }
        if(playlist==null)
        {
            throw new Exception("Playlist does not exist");
        }
        User user=null;
        for(User u:users)
        {
            if(u.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }
        List<User>listnerlist=playlistListenerMap.get(playlist);
        if(listnerlist.contains(user))
        {
            return playlist;
        }
        listnerlist.add(user);
        playlistListenerMap.put(playlist,listnerlist);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Song song = null;
        for (Song s : songs) {
            if (s.getTitle().equals(songTitle)) {
                song = s;
                break;
            }
        }
        if (song == null) {
            throw new Exception("Song does not exist");
        }
        if (songLikeMap.containsKey(song)) {
            List<User> userList = songLikeMap.get(song);
            if (userList.contains(user)) {
                return song;
            } else {
                int count = song.getLikes() + 1;
                song.setLikes(count);
                userList.add(user);
                songLikeMap.put(song, userList);
                Album album = null;
                for (Album a : albumSongMap.keySet()) {
                    List<Song> songslist = albumSongMap.get(a);
                    if (songslist.contains(song)) {
                        album = a;
                        break;
                    }
                }
                Artist artist = null;
                for (Artist a : artistAlbumMap.keySet()) {
                    List<Album> albums1 = artistAlbumMap.get(a);
                    if (albums1.contains(a)) {
                        artist = a;
                        break;
                    }
                }
                int like = artist.getLikes() + 1;
                artist.setLikes(like);
                artists.add(artist);
                return song;
            }
        } else {
            int likecount = song.getLikes() + 1;
            song.setLikes(likecount);
            List<User> userList = new ArrayList<>();
            userList.add(user);
            songLikeMap.put(song, userList);
            Album album = null;
            for (Album a : albumSongMap.keySet()) {
                List<Song> list = albumSongMap.get(a);
                if (list.contains(song)) {
                    album = a;
                    break;
                }
            }
            Artist artist = null;
            for (Artist artist1 : artistAlbumMap.keySet()) {
                List<Album> albums1 = artistAlbumMap.get(artist1);
                if (albums1.contains(album)) {
                    artist = artist1;
                    break;
                }
                int likes = artist.getLikes() + 1;
                artist.setLikes(likes);
                artists.add(artist);
            }
            return song;
        }
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist = null;
        for (Artist a : artists) {
            if (a.getLikes() >= max) {
                artist = a;
                max = a.getLikes();
            }
        }
        if (artist == null) {
            return null;
        } else {
            return artist.getName();
        }
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;
        for (Song s : songs) {
            if (s.getLikes() >= max) {
                song = s;
                max = s.getLikes();
            }
        }
        if (song == null) {
            return null;
        } else {
            return song.getTitle();
        }
    }
}