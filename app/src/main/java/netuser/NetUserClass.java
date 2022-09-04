package netuser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

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
    private TextField passwordExpires;

    @FXML
    private Button searchBtn;

    @FXML
    private Button unlockBtn;

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

        alert.showAndWait();
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

    boolean check(String text, String first, String second) {
        if (text.contains(first) || (text.contains(second))) {
            return true;
        } else {
            return false;
        }
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Search(ActionEvent event) {
        if (checkUsername(username)) {

            try {
                ProcessBuilder build_test = new ProcessBuilder(
                        "cmd.exe", "/c", "net user " + getUsername());
                build_test.redirectErrorStream(true);
                Process p = build_test.start();
                GetInformation(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            myAlert(Alert.AlertType.ERROR, "Wystąpił błąd", "Brak wszystkich wymaganych danych", "Podaj nazwę użytkownika");
        }
    }

    @FXML
    void changePassword(ActionEvent event) {

    }

    @FXML
    void txtFieldKeyReleased(KeyEvent event) {

    }

    @FXML
    void unclockAccount(ActionEvent event) {

    }

    @FXML
    void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchBtn.fire();
        }
    }

    @FXML
    void aboutAuthor(ActionEvent event) {
        myAlert(Alert.AlertType.INFORMATION, "Autor programu", "Informacja o autorze programu", "Autor: Wojciech Kozioł\nFirma: Fujitsu");
    }

    @FXML
    void aboutProgram(ActionEvent event) {
        myAlert(Alert.AlertType.INFORMATION, "O programie", "Informacja o programie", "Program służy do wyświetlania informacji o użytkowniku których brakuje w przystawce Active Directory\n" +
                "Jest on ciągle w rozwoju, aktualnie posiada podstawowe funkcje - wyświetlania informacji, odblokowania konta oraz zmiany hasła.");
    }

    @FXML
    void clearFields(ActionEvent event) {
        for (TextField textField : Arrays.asList(username, fullName, activeAccount, accountExpires, lastChange, passwordExpires, lastLogin)) {
            textField.clear();
        }
    }

    @FXML
    void exit(ActionEvent event) {
        System.exit(0);
    }


}
