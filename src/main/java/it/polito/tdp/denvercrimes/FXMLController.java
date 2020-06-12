package it.polito.tdp.denvercrimes;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import it.polito.tdp.denvercrimes.model.Model;
import it.polito.tdp.denvercrimes.model.Vicino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Integer> boxAnno;

    @FXML
    private ComboBox<Integer> boxMese;

    @FXML
    private ComboBox<Integer> boxGiorno;

    @FXML
    private Button btnCreaReteCittadina;

    @FXML
    private Button btnSimula;

    @FXML
    private TextField txtN;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaReteCittadina(ActionEvent event) {
    	txtResult.clear();
    	Integer anno = boxAnno.getValue();
    	if(anno == null) {
    		txtResult.appendText("SELEZIONA ANNO!");
    		return ;
    	}
    	model.creaGrafo(anno);
    	txtResult.appendText("Grafo creato!\n\n");
    	
    	for(Integer distretto: model.getDistretti()) {
    		txtResult.appendText(distretto + "\n");
    		for(Vicino v: model.trovaVicini(distretto)) {
    			txtResult.appendText("distretto: "+v.getId()+", distanza media: "+v.getDistanzaMedia()+"\n");
    		}
    	}
    }

    @FXML
    void doSimula(ActionEvent event) {
    	txtResult.clear();
    	Integer anno, mese, giorno, N;
    	try {
    		N = Integer.parseInt(txtN.getText());
    	}catch(NumberFormatException e) {
    		txtResult.appendText("Formato N non corretto");
    		return ;
    	}
    	
    	anno = this.boxAnno.getValue();
    	mese = boxMese.getValue();
    	giorno = boxGiorno.getValue();
    	
    	if(anno == null || mese == null || giorno == null) {
    		txtResult.appendText("Seleziona tutti i campi");
    		return ;
    	}
    	
    	try {
    		LocalDate.of(anno, mese, giorno);
    	}catch(DateTimeException e) {
    		txtResult.appendText("Data non corretta");
    		return ;
    	}
    	
    	txtResult.appendText("Simulo con "+N+" agenti");
    	txtResult.appendText("\nCRIMINI MAL GESTITI: "+this.model.simula(anno, mese, giorno, N));
    	
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Crimes.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		this.boxAnno.getItems().clear();
		this.boxAnno.getItems().addAll(model.getAnni());
		this.boxMese.getItems().clear();
		this.boxMese.getItems().addAll(model.getMesi());
		this.boxGiorno.getItems().clear();
		this.boxGiorno.getItems().addAll(model.getGiorni());
	}
}
