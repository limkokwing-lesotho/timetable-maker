package com.breakoutms.timetable.service;

import com.breakoutms.timetable.Main;
import com.breakoutms.timetable.model.beans.Project;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

@Log4j2
public class ProjectFileManager {

    private static final String DEFAULT_FILE_CHOOSER_PATH = "DEFAULT_FILE_CHOOSER_PATH";
    private static final Preferences prefs = Preferences.userNodeForPackage(ProjectFileManager.class);
    public static final String FILE_EX = ".ttb";

    private File file;

    public void saveFile() {
        FileChooser fileChooser = createFileChooser();

        if(file == null){
            file = fileChooser.showSaveDialog(Main.getPrimaryStage());
        }
        if (file != null) {
            try {
                writeToFile();
                prefs.put(DEFAULT_FILE_CHOOSER_PATH, file.getParent());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error: "+e.getMessage());
                alert.showAndWait();
            }
        }

    }
    public Optional<Project> openFile() {
        FileChooser fileChooser = createFileChooser();

        file = fileChooser.showOpenDialog(Main.getPrimaryStage());

        if (file != null) {
            try {
                prefs.put(DEFAULT_FILE_CHOOSER_PATH, file.getParent());
                return Optional.of(readFile(file));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error: "+e.getMessage());
                alert.showAndWait();
            }
        }
        return Optional.empty();
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Timetable files (*" + FILE_EX + ")", "*" + FILE_EX);
        fileChooser.getExtensionFilters().add(extFilter);

        File path = projectFilePath();
        fileChooser.setInitialDirectory(path);

        return fileChooser;
    }

    public static File projectFilePath() {
        String filePath = prefs.get(DEFAULT_FILE_CHOOSER_PATH, null);
        return new File(Objects.requireNonNullElseGet(filePath,
                () -> FileSystemView.getFileSystemView().getDefaultDirectory().getPath()));
    }

    private Project readFile(File path)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            Project project = (Project) is.readObject();
            if(StringUtils.isBlank(project.getName())) {
                project.setName(parseName(path.getName()));
            }
            return project;
        }
    }

    private String parseName(String name) {
        String[] parts = name.replace(FILE_EX, "").split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private void writeToFile()
            throws IOException {
        String filePath = file.getAbsolutePath();
        if(!filePath.endsWith(FILE_EX)) {
            file = new File(filePath + FILE_EX);
        }
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(Project.INSTANCE);
        }
    }
}
