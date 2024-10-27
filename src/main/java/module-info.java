module at.rayman.savethespirepc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.io;

    opens at.rayman.savethespirepc to javafx.fxml;
    exports at.rayman.savethespirepc;
}