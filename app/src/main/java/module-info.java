module netuser.app.main {

    requires javafx.controls;
    requires javafx.fxml;
    opens netuser to javafx.fxml;

    exports netuser;
}