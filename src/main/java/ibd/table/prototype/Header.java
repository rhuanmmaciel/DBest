package ibd.table.prototype;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Header {

    public static final String TABLE_TYPE = "table-type";
    public static final String FILE_PATH = "file-path";
    public static final String TABLE_NAME = "tablename";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private HashMap<String, String> information;
    private HashMap<String, Header> subHeaders;
    private Prototype prototype;

    private String path = null;

    
    public Header(String path) {
        this.path = path;
    }
    
    public Header(Prototype pt, String tableName) {
        this.information = new HashMap<>();
        this.prototype = pt;
        this.subHeaders = new HashMap<>();
        this.set(TABLE_NAME, tableName);
    }

    public static Header load(String path) throws IOException {
        String json = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Header header = (Header) gson.fromJson(json, Header.class);
        header.path = path;
        if (header.subHeaders == null) {
            header.subHeaders = new HashMap<>();
        }
        return header;
    }

    public String save(String path) throws IOException {
        String filePath = get(Header.FILE_PATH);
        this.path = path;
        if (filePath != null) {
            String filePathRelative = new File(new File(path).getAbsolutePath()).getParentFile().toURI().relativize(new File(new File(filePath).getAbsolutePath()).toURI()).getPath();
            set(Header.FILE_PATH, filePathRelative);
        }
        setBool("saved", true);
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Files.writeString(Paths.get(path), json, StandardCharsets.UTF_8);
        return path;
    }

    public static String replaceFileName(String filePathA, String fileNameB) {
        // Obter o caminho do arquivo A
        Path pathA = Paths.get(filePathA);

        // Obter o diretório do arquivo A
        Path parentPath = pathA.getParent();

        // Construir o novo caminho com o nome do arquivo B
        Path newPath = parentPath.resolve(fileNameB);

        // Retornar o novo caminho como string
        return newPath.toString();
    }

    public void set(String key, String value) {
        information.put(key, value);
    }

    public String get(String key) {
        return information.get(key);
    }

    public Header getSubHeader(String key) {
        if (!subHeaders.containsKey(key)) {
            subHeaders.put(key, new Header(new Prototype(prototype), "subheader_" + key));
        }
        return subHeaders.get(key);
    }

    public Header setBool(String key, boolean value) {
        information.put(key, value ? Header.TRUE : Header.FALSE);
        return this;
    }

    public boolean getBool(String key) {
        return information.get(key) == Header.TRUE;
    }

    public String getTablePath() {
        String filePath = this.get(Header.FILE_PATH);
        if (filePath == null) {
            filePath = this.get(Header.TABLE_NAME) + ".dat";
        }
        if (this.path != null) {
            filePath = Path.of(filePath).isAbsolute() ? filePath : new File(new File(this.path).getAbsolutePath()).getParentFile() + "/" + filePath;
        }
        return filePath;
    }

    public String getFileName() {
        Path path_ = Paths.get(path);
        String fileNameWithoutExtension = path_.getFileName().toString();

        // Remove the extension
        int index = fileNameWithoutExtension.lastIndexOf('.');
        if (index > 0) {
            return fileNameWithoutExtension.substring(0, index);
        }
        return fileNameWithoutExtension;
    }

    public void setPrototype(Prototype pt) {
        this.prototype = pt;
    }

    public Prototype getPrototype() {
        return prototype;
    }
}
