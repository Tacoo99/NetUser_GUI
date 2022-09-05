package netuser;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

import io.github.palexdev.materialfx.controls.MFXButton;


public class NetUserClass {

    @FXML
    private MFXCheckbox domainCheckbox;

    @FXML
    private MFXTextField fullName;

    @FXML
    private MFXTextField accountExpires;

    @FXML
    private MFXTextField activeAccount;


    @FXML
    private MFXTextField lastChange;

    @FXML
    private MFXTextField lastLogin;

    @FXML
    private MFXPasswordField newPassword;

    @FXML
    private MFXPasswordField repeatPassword;

    @FXML
    private MFXTextField passwordExpires;

    @FXML
    private MFXButton searchBtn;

    @FXML
    private MFXTextField username;

    String domain = "";

    boolean checkUsername(MFXTextField textField) {
        String usernameText = textField.getText();
        return !usernameText.isEmpty();
    }

    boolean checkCheckbox(){
        if(domainCheckbox.isSelected()){
            return true;
        }
        else {
            return false;
        }
    }

    String setDomain(){
        return " /domain";
    }

    String getUsername() {
        return username.getText();
    }

    void myAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(searchBtn.getScene().getWindow());

        alert.showAndWait();
    }

    boolean myConfirmation(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(searchBtn.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    String formatString(String text, int number) {
        int size;
        StringBuilder result = new StringBuilder();
        String after = text.trim().replaceAll(" +", " ");
        String[] parts = after.split(" ");
        size = parts.length;
        while (number < size) {
            result.append(parts[number]).append(" ");
            number++;
        }
        return result.toString();
    }

    void GetInformation(Process p, int name, int activeAcc, int accExp, int lastCha, int passExp, int lastLog) {
        try {
            BufferedReader output_reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String output;
            int i = 0;
            while ((output = output_reader.readLine()) != null) {
                i++;
                if (i == name) {
                    fullName.setText(formatString(output, 2));
                }

                if (i == activeAcc) {
                    activeAccount.setText(formatString(output, 3));
                    if (activeAccount.getText().matches("(.*)Tak(.*)")) {
                        activeAccount.setStyle("-fx-border-color: green");
                    } else if (activeAccount.getText().matches("(.*)Nie(.*)")) {
                        activeAccount.setStyle("-fx-border-color: tomato");
                    }
                }

                if (i == accExp) {
                    accountExpires.setText(formatString(output, 2));
                    if (accountExpires.getText().matches("(.*)N(.*)")) {
                        accountExpires.setStyle("-fx-border-color: green");
                    }
                }

                if (i == lastCha) {
                    lastChange.setText(formatString(output, 3));
                }

                if (i == passExp) {
                    passwordExpires.setText(formatString(output, 3));
                    if (passwordExpires.getText().matches("(.*)N(.*)")) {
                        passwordExpires.setStyle("-fx-border-color: green");
                    }
                }

                if (i == lastLog) {
                    lastLogin.setText(formatString(output, 2));
                }
            }

            int r = p.waitFor(); // Let the process finish.
            if (r != 0) { // Error
                myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wystąpił problem z danymi wejściowymi", "Nie znaleziono użytkownika " + getUsername());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Search() {
        if (checkUsername(username)) {
            if(checkCheckbox()){
                domain = setDomain();
            }
            else{
                domain = "";
            }
            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername() + domain);
                Process p = build_test.start();
                if(checkCheckbox()) {
                    GetInformation(p, 4, 8, 9, 11, 12, 21);
                }
                else{
                    GetInformation(p, 2, 6, 7, 9, 10, 19);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    void clearPasswords(){
        for (MFXPasswordField passwordField : Arrays.asList(newPassword, repeatPassword)) {
            passwordField.clear();
        }
    }

    @FXML
    void changePassword() {
        String password = newPassword.getText();
        String repeatedPassword = repeatPassword.getText();

        if(password.equals(repeatedPassword)){
            setPassword(password);
        }
        else{
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wprowadzone dane są niepoprawne", "Hasła różnią się od siebie");
        }

    }

    void manageAccount(String operation){

        if (checkUsername(username)) {
            if(checkCheckbox()){
                domain = setDomain();
            }
            else{
                domain = "";
            }
            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername() + " /ACTIVE:"+ operation + domain );
                Process p = build_test.start();
                int r = p.waitFor(); // Let the process finish.
                if (r != 0) { // Error
                    myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wystąpił problem z odblokowaniem/zablokowaniem konta", "Nie można było uzyskać dostępu do konta " + getUsername());
                }
                else{
                    searchBtn.fire();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else {
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    @FXML
    void unclockAccount() {
        manageAccount("YES");
    }

    @FXML
    void blockAccount(){
        manageAccount("NO");
    }

    void setPassword(String password){
        if (checkUsername(username)) {
            if(checkCheckbox()){
                domain = setDomain();
            }
            else{
                domain = "";
            }
            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername() + " " + password + domain);
                Process p = build_test.start();
                int r = p.waitFor(); // Let the process finish.
                if (r != 0) { // Error
                    myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wystąpił problem ze zmianą hasła", "Hasło użytkownika " + getUsername() + " nie zostało pomyślnie zmienione");
                    clearPasswords();
                }
                else{
                    myAlert(Alert.AlertType.INFORMATION, "Operacja powiodła się", "Hasło zostało ustawione", "Hasło użytkownika " + getUsername() + " zostało pomyślnie zmienione");
                    clearPasswords();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else {
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    @FXML
    void setDefaultPassword(){
        String defaultPassword = "Lato2022";
        if (checkUsername(username)) {
            boolean confirmation = myConfirmation("Potwierdź operację", "Czy na pewno chcesz wykonać tą akcję?", "Hasło do konta użytkownika " + getUsername() +" zmieni się na standardowe SD");
            if(confirmation){
                setPassword(defaultPassword);
            }
        }

        else{
            myAlert(Alert.AlertType.ERROR, "Wystapił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    @FXML
    void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchBtn.fire();
        }
        if (event.getCode() == KeyCode.BACK_SPACE) {
            clearFields();
        }

    }

    @FXML
    void aboutAuthor() {
        myAlert(Alert.AlertType.INFORMATION, "Autor programu", "Informacja o autorze programu", "Autor: Wojciech Koziol\nFirma: Fujitsu");
    }

    @FXML
    void aboutProgram() {
        myAlert(Alert.AlertType.INFORMATION, "O programie", "Informacja o programie", "Program służy do wyświetlania informacji o użytkowniku których brakuje w przystawce Active Directory\n" +
                "Jest on ciągle w rozwoju, aktualnie posiada podstawowe funkcje - wyświetlania informacji, odblokowania konta oraz zmiany hasła.");
    }

    @FXML
    void clearFields() {
        for (MFXTextField textField : Arrays.asList(username, fullName, activeAccount, accountExpires, lastChange, passwordExpires, lastLogin, newPassword, repeatPassword)) {
            textField.clear();
            textField.setStyle("-fx-background-color: #a9a9a9 , white , white");
        }
    }

    @FXML
    void exit() {
        System.exit(0);
    }


}
