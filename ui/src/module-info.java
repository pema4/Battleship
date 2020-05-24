module battleship.ui {
    requires battleship.basics;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    exports battleship;

    opens battleship.controllers to javafx.fxml;
    opens battleship.controls to javafx.fxml;
    opens battleship.validation to javafx.fxml;
}