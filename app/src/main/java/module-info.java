module netuser.app.main {

    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    opens netuser to javafx.fxml;

    exports netuser;
}