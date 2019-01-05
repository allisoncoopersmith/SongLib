/*
 * @author Allison Coopersmith
 * @author Kaushal Parikh
 */

package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Optional;

import application.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class Controller{
	Stage primaryStage;
	//FXML refs:
	@FXML
	ListView<Song> listView;
	@FXML
	Button addSongButton;
	@FXML
	Button deleteSongButton;
	@FXML
	Button editSongButton;
	@FXML
	TextField nameTF;
	@FXML
	TextField artistTF;
	@FXML
	TextField albumTF;
	@FXML
	TextField yearTF;

	private ObservableList<Song> obsList =  FXCollections.observableArrayList(); 

	public void setMainStage(Stage stage) {
		primaryStage = stage;

		File f = new File("src/songList.txt");
		if(f.exists() && !f.isDirectory()) { 
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				String songName;
				while ((songName = reader.readLine()) != null){
					String artistName = reader.readLine();
					String album = reader.readLine();
					String year = reader.readLine();
					obsList.add(new Song(songName, artistName, album, year));

				}

				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		listView.setItems(obsList);

		listView.getSelectionModel().select(0);
		listView
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) ->
				showSongDetails(primaryStage)); 


		showSongDetails(primaryStage);

		stage.setOnCloseRequest(event -> {
			PrintWriter pw;
			try {
				File file = new File ("src/songList.txt");
				file.createNewFile();
				pw = new PrintWriter(file);
				for(int x=0; x< obsList.size(); x++)  {
					Song curr = obsList.get(x);
					pw.println(curr.getName());
					pw.println(curr.getArtist());
					pw.println(curr.getAlbum());
					pw.println(curr.getYear());

				}
				pw.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			} 

		});

	}

	private void showSongDetails(Stage mainStage) { 

		Song selectedSong = listView.getSelectionModel().getSelectedItem();

		if (selectedSong !=null) {
			nameTF.setText(selectedSong.getName());
			artistTF.setText(selectedSong.getArtist());
			albumTF.setText(selectedSong.getAlbum());
			yearTF.setText(selectedSong.getYear());
		}
		else {
			nameTF.setText("");
			artistTF.setText("");
			albumTF.setText("");
			yearTF.setText("");

		}

	}

	public void handleEditSongBtn (ActionEvent event) {
		Song editedSong = null;

		//update details on a song iff the artist and song name boxes are changed
		if (obsList.isEmpty()) { //can't edit on an empty list
			printIfEmpty();
			return;
		}

		String newName = nameTF.getText();
		String newArtist = artistTF.getText();
		String newAlbum = albumTF.getText();
		String newYear = yearTF.getText();

		int index =obsList.indexOf(listView.getSelectionModel().getSelectedItem());
		Song selectedSong = listView.getSelectionModel().getSelectedItem();
		String oldName = selectedSong.getName(); //name of the song that the user is editing
		String oldArtist = selectedSong.getArtist(); //artist of the song that the user is editing
		String oldAlbum = selectedSong.getAlbum();
		String oldYear = selectedSong.getYear();

		if (oldName.equals(newName) && oldArtist.equals(newArtist) && oldAlbum.equals(newAlbum) && oldYear.equals(newYear)) {
			Alert alert = 
					new Alert(AlertType.INFORMATION);
			alert.setTitle("Error");
			alert.setHeaderText("No change made.");
			String content = "For a valid change to occur, please make at least one change to a song field.";
			alert.setContentText(content);
			alert.showAndWait();
			return;
		}

		if (newName.trim().isEmpty() || newArtist.trim().isEmpty()) { //if an artist and name aren't entered
			missingFields();
			return;
		}
		for (int x=0; x<obsList.size(); x++) { //see if edited version of song already exists in library
			String currName = obsList.get(x).name; //current song's name in the loop
			String currArtist = obsList.get(x).artist; //current song's artist
			if (currName.equals(newName) && currArtist.equals(newArtist) && x!=index){
				Alert alert = 
						new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("Duplicate song");
				String content = "This song is already in the library.";
				alert.setContentText(content);
				alert.showAndWait();
				resetTextFields();
				return;
			}

		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Edit Song");
		alert.setContentText("Are you sure you want to edit this song?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			for (int x=0; x<obsList.size(); x++) { //find the song in the obs list and update it
				String currName = obsList.get(x).name; //current song's name in the loop
				String currArtist = obsList.get(x).artist; //current song's artist
				if (oldName.equals(currName) && oldArtist.equals(currArtist)){
					editedSong = obsList.get(x);
					editedSong.setName(newName);
					editedSong.setArtist(newArtist);
					editedSong.setAlbum(newAlbum);
					editedSong.setYear(newYear);
					break;

				}

			} 
			resetTextFields();
			Collections.sort(obsList);	
			//	int x =obsList.indexOf(editedSong);
			listView.getSelectionModel().select(index);
		}
		else {
			resetTextFields();
			listView.getSelectionModel().select(index);
			showSongDetails(primaryStage);
			return;

		}

	}

	public void handleDeleteSongBtn (ActionEvent event) {
		if (obsList.isEmpty()) { //can't delete from an empty list
			printIfEmpty();
			return;
		}
		Song selectedSong = listView.getSelectionModel().getSelectedItem();
		int index = listView.getSelectionModel().getSelectedIndex();

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Confirm deletion");
		alert.setContentText("Are you sure you want to delete this song?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			obsList.remove(selectedSong);
			listView.getSelectionModel().select(index++);
			showSongDetails(primaryStage);
		} 
		else {
			listView.getSelectionModel().select(index);
			showSongDetails(primaryStage);
			return;		
		}

	}

	public void handleAddSongBtn (ActionEvent event) {
		//code to add a song goes here. need to read it to a txt doc and then load it to obsList
		int index = listView.getSelectionModel().getSelectedIndex();

		String name = nameTF.getText();
		String artist = artistTF.getText();
		
		if (name.trim().isEmpty() || artist.trim().isEmpty()) { //if an artist and name aren't entered
			missingFields();
			return;
		}

		if (checkDuplicate (name, artist)) {
			resetTextFields();
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Confirm add song");
		alert.setContentText("Are you sure you want to add this song?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			Song newSong = makeNewSong();
			obsList.add(newSong);
			resetTextFields();
			Collections.sort(obsList);	
			int x =obsList.indexOf(newSong);
			listView.getSelectionModel().select(x);		
		}
		
		else {
			listView.getSelectionModel().select(index);
			showSongDetails(primaryStage);
		}

	}
	
	public void handleClearFieldsBtn(ActionEvent event) {
		resetTextFields();
	}

	private boolean checkDuplicate(String name, String artist) {
		for (Song curr: obsList) {
			if (curr.name.toLowerCase().equals(name.toLowerCase()) && curr.artist.toLowerCase().equals(artist.toLowerCase())) {
				Alert alert = 
						new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("Duplicate song");
				String content = "This song is already in the library.";
				alert.setContentText(content);
				alert.showAndWait();
				return true;
			}
		}
		return false;
	}

	public void resetTextFields () {
		nameTF.setText("");
		artistTF.setText("");
		albumTF.setText("");
		yearTF.setText("");
	}

	private Song makeNewSong() {
		Song newSong = new Song (nameTF.getText(), artistTF.getText(), albumTF.getText(), yearTF.getText());
		return newSong;

	}

	private void missingFields () { //for when you try to add/edit a song without having a name and artist
		Alert alert = 
				new Alert(AlertType.INFORMATION);
		alert.setTitle("Error");
		alert.setHeaderText("Missing fields");
		String content = "Enter a title and artist.";
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void printIfEmpty () { //for when you try to edit or delete a song in an empty list
		Alert alert = 
				new Alert(AlertType.INFORMATION);
		alert.setTitle("Error");
		alert.setHeaderText("Empty list");
		String content = "List is empty";
		alert.setContentText(content);
		alert.showAndWait();

	}

}