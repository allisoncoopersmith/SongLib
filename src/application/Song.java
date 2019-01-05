/*
 * @author Allison Coopersmith
 * @author Kaushal Parikh
 */

package application;

public class Song implements Comparable<Song> {
	String name;
	String artist;
	String album;
	String year;



	public Song(String name, String artist, String album, String year) {
		this.name=name;
		this.artist=artist;
		this.album=album;
		this.year=year;	}


	public void setName (String name) {
		this.name=name;
	}
	public String getName () {
		return this.name;
	}
	public void setArtist(String artist) {
		this.artist=artist;

	}
	public String getArtist () {
		return this.artist;
	}
	public void setAlbum (String album) {
		this.album=album;
	}
	public String getAlbum() {
		return this.album;
	}
	public void setYear (String year) {
		this.year = year;
	}
	public String getYear () {
		return this.year;
	}

	public String toString () {
		return "\"" + name + "\"" + " by " + artist;
	}

	public int compareTo(Song s) {
		if (getName().toLowerCase().equals(s.getName().toLowerCase())){
			return getArtist().toLowerCase().compareTo(s.getArtist().toLowerCase());
		} else {
			return getName().toLowerCase().compareTo(s.getName().toLowerCase());
		}
	}
}