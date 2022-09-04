package netuser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

public class NetUserClass {

    @FXML
    private TextField fullName;

    @FXML
    private TextField accountExpires;

    @FXML
    private TextField activeAccount;

    @FXML
    private Button changeBtn;

    @FXML
    private TextField lastChange;

    @FXML
    private TextField lastLogin;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField repeatPassword;

    @FXML
    private TextField passwordExpires;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField username;

    boolean checkUsername(TextField textField) {
        String usernameText = textField.getText();
        return !usernameText.isEmpty();
    }

    String getUsername() {
        String usernameText = username.getText();
        return usernameText;
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
            result.append(parts[number] + " ");
            number++;
        }
        return result.toString();
    }

    void GetInformation(Process p) {
        try {
            BufferedReader output_reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String output = "";
            int i = 0;
            while ((output = output_reader.readLine()) != null) {
                i++;
                if (i == 2) {
                    fullName.setText(formatString(output, 2));
                }

                if (i == 6) {
                    activeAccount.setText(formatString(output, 3));
                    if(activeAccount.getText().matches("(.*)Tak(.*)")){
                        activeAccount.setStyle("-fx-border-color: green");
                    }
                    else if(activeAccount.getText().matches("(.*)Nie(.*)")){
                        activeAccount.setStyle("-fx-border-color: tomato");
                    }
                }

                if (i == 7) {
                    accountExpires.setText(formatString(output, 2));
                }

                if (i == 9) {
                    lastChange.setText(formatString(output, 3));
                }

                if (i == 10) {
                    passwordExpires.setText(formatString(output, 3));
                }

                if (i == 19) {
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
            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername());
                Process p = build_test.start();
                GetInformation(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    void clearPasswords(){
        for (PasswordField passwordField : Arrays.asList(newPassword, repeatPassword)) {
            passwordField.clear();
        }
    }

    @FXML
    void changePassword() {
        String password = newPassword.getText();
        String repeatedPassword = repeatPassword.getText();

        if(password.equals(repeatedPassword)){
            if (checkUsername(username)) {
                try {
                    ProcessBuilder build_test = new ProcessBuilder(
                            "cmd.exe", "/c", "net user " + getUsername() + " " + password);
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
        else{
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wprowadzone dane są niepoprawne", "Hasła różnią się od siebie");
        }

    }

    void manageAccount(String operation){

        if (checkUsername(username)) {
            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername() + " /ACTIVE:"+ operation );
                Process p = build_test.start();
                int r = p.waitFor(); // Let the process finish.
                if (r != 0) { // Error
                    myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Wystąpił problem z oblokowaniem/zablokowaniem konta", "Nie można było uzyskać dostępu do konta " + getUsername());
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

    @FXML
    void setDefaultPassword(){
        if (checkUsername(username)) {
            myConfirmation("Potwierdź operację", "Czy na pewno chcesz wykonać tą akcję?", "Hasło do konta użytkownika " + getUsername() +" zmieni się na standardowe SD");
        }

        else{

        }
    }

    @FXML
    void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchBtn.fire();
        }
    }

    @FXML
    void aboutAuthor() {
        myAlert(Alert.AlertType.INFORMATION, "Autor programu", "Informacja o autorze programu", "Autor: Wojciech Kozioł\nFirma: Fujitsu");
    }

    @FXML
    void aboutProgram() {
        myAlert(Alert.AlertType.INFORMATION, "O programie", "Informacja o programie", "Program służy do wyświetlania informacji o użytkowniku których brakuje w przystawce Active Directory\n" +
                "Jest on ciągle w rozwoju, aktualnie posiada podstawowe funkcje - wyświetlania informacji, odblokowania konta oraz zmiany hasła.");
    }

    @FXML
    void clearFields() {
        for (TextField textField : Arrays.asList(username, fullName, activeAccount, accountExpires, lastChange, passwordExpires, lastLogin, newPassword, repeatPassword)) {
            textField.clear();
        }
    }

    @FXML
    void exit() {
        System.exit(0);
    }


}
